package org.eljaiek.jmira.app.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import org.eljaiek.jmira.core.logs.LogbackAppenderAdapter;
import org.eljaiek.jmira.core.io.DownloadStatus;
import org.eljaiek.jmira.core.io.Download;
import org.eljaiek.jmira.core.io.DownloadFailedException;
import org.eljaiek.jmira.core.logs.MessageResolver;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import org.controlsfx.control.TaskProgressView;
import org.eljaiek.jmira.app.util.FileSystemHelper;
import org.eljaiek.jmira.core.*;
import org.eljaiek.jmira.core.model.DebPackage;
import org.eljaiek.jmira.core.io.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import org.eljaiek.jmira.app.model.SettingsModel;
import org.eljaiek.jmira.core.io.DownloadBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by eduardo.eljaiek on 12/4/2015.
 */
@Lazy
@Component
public final class DownloadScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadScheduler.class);

    private static final String APPENDER_NAME = "AREA";

    private volatile boolean stopDownloads;

    private volatile int remainingThreads;

    private final SplitPane container;

    private final TaskProgressView<Task<Void>> progressView;

    private final LogViewer logViewer;

    private final IntegerProperty errors;

    private final IntegerProperty downloadedCount;

    private final LongProperty downloaded;

    private Queue<DebPackage> queue;

    private ExecutorService pool;

    private EventHandler<DownloadEvent> onDone;

    private EventHandler<DownloadEvent> onCancelled;

    private EventHandler<DownloadEvent> onFail;

    private EventHandler<DownloadEvent> onLoadSucceeded;

    private SettingsModel settings;

    @Autowired
    private PackageService packageService;

    @Autowired
    private DownloadBuilderFactory downloadBuilderFactory;

    public DownloadScheduler() {
        downloadedCount = new SimpleIntegerProperty();
        errors = new SimpleIntegerProperty();
        downloaded = new SimpleLongProperty();
        queue = new ConcurrentLinkedQueue<>();
        progressView = new TaskProgressView<>();
        logViewer = new LogViewer();
        container = new SplitPane(progressView, logViewer);
        container.setOrientation(Orientation.VERTICAL);
    }

    public Node getControl() {
        return container;
    }

    public int getDownloadedCount() {
        return downloadedCount.get();
    }

    public IntegerProperty downloadedCountProperty() {
        return downloadedCount;
    }

    public long getDownloaded() {
        return downloaded.get();
    }

    public LongProperty downloadedProperty() {
        return downloaded;
    }

    public int getErrors() {
        return errorsProperty().get();
    }

    public IntegerProperty errorsProperty() {
        return errors;
    }

    public final void cancel() {
        stopDownloads = true;
        progressView.getTasks().forEach(task -> ((DownloadTask) task).cancel(true));
        pool.shutdown();
        fireCancelledEvent();
    }

    public final void start(SettingsModel settings) {
        this.settings = settings;
        LogbackAppenderAdapter.register(APPENDER_NAME, logViewer);
        pool = Executors.newFixedThreadPool(settings.getDownloadThreads());
        Task<Void> searchTask = new SearchTask();

        searchTask.setOnSucceeded(evt -> {
            fireLoadSucceeded();
            List<DownloadModel> downloads = createDownloads(settings.getDownloadThreads());

            if (downloads.isEmpty()) {
                fireDoneEvent();
            }

            remainingThreads = downloads.size();
            downloads.forEach(this::start);
        });

        searchTask.setOnFailed(evt -> {
            String msg = MessageResolver.getDefault()
                    .getMessage("download.process.fail",
                            searchTask.getException().getMessage());
            fireFailEvent(new DownloadFailedException(msg));
        });

        start(searchTask);
    }

    private void start(DownloadModel download) {
        DownloadTask task = new DownloadTask(download);
        download.register(task);

        task.setOnSucceeded(evt -> {

            if (stopDownloads) {
                return;
            }

            if (queue.isEmpty()) {
                remainingThreads--;

                if (remainingThreads == 0) {
                    fireDoneEvent();
                }

                return;
            }

            List<DownloadModel> pack = createDownloads(1);

            if (pack.size() > 0) {
                start(pack.iterator().next());
            }
        });

        start(task);
    }

    private void start(Task<Void> task) {

        try {
            progressView.getTasks().add(task);
            pool.submit(task);
        } catch (RejectedExecutionException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private List<DownloadModel> createDownloads(int threads) {
        int i = 0;
        List<DownloadModel> downloads = new ArrayList<>(threads);

        while (i < threads && i <= queue.size()) {
            DebPackage p = queue.poll();
            String localUrl = p.getLocalUrl();
            String folder = localUrl.substring(0, localUrl.lastIndexOf('/'));
            Download download = downloadBuilderFactory
                    .create()
                    .localFolder(folder).url(p.getRemoteUrl())
                    .checksum(settings.isChecksum() ? p.getChecksum() : null)
                    .get();
            downloads.add(new DownloadModel(p.getName(), p.getLength(), download));
            i++;
        }

        return downloads;
    }

    private void fireDoneEvent() {
        reset();

        if (onDone != null) {
            onDone.handle(new DownloadEvent(DownloadEvent.DOWN_DONE));
        }
    }

    private void fireFailEvent(Exception error) {
        reset();

        if (onFail != null) {
            onFail.handle(new DownloadFailEvent(DownloadFailEvent.DOWN_FAIL, error));
        }
    }

    private void fireLoadSucceeded() {

        if (onLoadSucceeded != null) {
            onLoadSucceeded.handle(new DownloadEvent(DownloadEvent.DOWN_SEARCH_DONE));
        }
    }

    private void fireCancelledEvent() {
        reset();

        if (onCancelled != null) {
            onCancelled.handle(new DownloadEvent(DownloadEvent.DOWN_CANCELLED));
        }

    }

    private void reset() {
        LogbackAppenderAdapter.remove(APPENDER_NAME);
        logViewer.replaceText(0, logViewer.getText().length(), "");
        progressView.getTasks().clear();
        stopDownloads = false;
        errors.set(0);
        queue.clear();
    }

    public void setOnLoadSucceeded(EventHandler<DownloadEvent> onLoadSucceeded) {
        this.onLoadSucceeded = onLoadSucceeded;
    }

    public void setOnCancelled(EventHandler onCancelled) {
        this.onCancelled = onCancelled;
    }

    public void setOnDone(EventHandler onDone) {
        this.onDone = onDone;
    }

    public void setOnFail(EventHandler<DownloadEvent> onFail) {
        this.onFail = onFail;
    }

    class SearchTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {

            try {
                updateTitle(MessageResolver.getDefault().getMessage("search.task.title"));
                queue = new ConcurrentLinkedQueue<>(packageService.listNotDownloaded(settings.isChecksum()));
                return null;
            } catch (DataAccessException ex) {              
                throw new DownloadFailedException(MessageResolver.getDefault().getMessage("search.process.fail"), ex);
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }
    }

//    class CancelTask extends Task<Void> {
//
//        @Override
//        protected Void call() throws Exception {
//            try {
//                updateTitle("Cancelling in progress downloads....");
//
//            } catch (Exception e) {
//                LOG.error(e.getMessage(), e);
//            }
//
//            return null;
//        }
//    }
    class DownloadTask extends Task<Void> implements Observer {

        private final DownloadModel download;

        public DownloadModel getDownloadModel() {
            return download;
        }

        public DownloadTask(DownloadModel download) {
            this.download = download;
            updateTitle(download.getPackageName());
            progressView.setGraphicFactory(null);
        }

        @Override
        protected Void call() throws Exception {

            try {
                download.start();
                return null;
            } catch (DownloadFailedException ex) {
                LOG.error(MessageResolver
                        .getDefault()
                        .getMessage("download.task.fail",
                                download.getPackageName(), ex.getMessage()));
                throw ex;
            }            
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {

            if (stopDownloads) {
                download.cancel();
                return super.cancel(mayInterruptIfRunning);
            }

            return false;
        }

        @Override
        public void update(Observable o, Object arg) {
            Download dn = (Download) o;

            if (DownloadStatus.DOWNLOADING == dn.getStatus()) {
                updateProgress(dn.getDownloaded(), dn.getSize());
                String formattedProgress = FileSystemHelper.formatSize(download.getDownloaded());
                String formattedSize = FileSystemHelper.formatSize(download.getSize());
                updateMessage(MessageResolver.getDefault()
                        .getMessage("download.task.info", formattedProgress, formattedSize));
                return;
            }

            if (DownloadStatus.COMPLETE == dn.getStatus()) {
                downloadedCount.set(downloadedCount.get() + 1);
                downloaded.set(downloaded.get() + dn.getSize());
                LOG.info(MessageResolver
                        .getDefault()
                        .getMessage("download.task.done", download.getPackageName()));
            }

            if (DownloadStatus.ERROR == dn.getStatus()) {
                DownloadScheduler.this.errors.add(1);
                dn.clean();
            }
        }
    }
}
