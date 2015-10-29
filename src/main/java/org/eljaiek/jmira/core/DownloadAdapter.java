package org.eljaiek.jmira.core;

import java.net.URL;
import java.util.Observable;

/**
 *
 * @author eduardo.eljaiek
 */
public abstract class DownloadAdapter extends Observable implements Download {

    private DownloadStatus status = DownloadStatus.DOWNLOADING;

    private int size = -1;

    private int downloaded;

    private final String localFolder;

    private URL url;

    public DownloadAdapter(String localFolder, URL url) {
        this.localFolder = localFolder;
        this.url = url;
    }

    @Override
    public void pause() {
        status = DownloadStatus.PAUSED;
        stateChanged();
    }

    @Override
    public void resume() {
        status = DownloadStatus.DOWNLOADING;
        stateChanged();
        run();
    }

    @Override
    public void cancel() {
        status = DownloadStatus.CANCELLED;
        stateChanged();
    }

    @Override
    public final synchronized DownloadStatus getStatus() {
        return status;
    }

    protected void setStatus(DownloadStatus status) {
        this.status = status;
    }

    @Override
    public int getSize() {
        return size;
    }

    protected void setSize(int size) {
        this.size = size;
    }

    @Override
    public synchronized float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    @Override
    public synchronized int getDownloaded() {
        return downloaded;
    }

    public URL getUrl() {
        return url;
    }

    protected void setUrl(URL url) {
        this.url = url;
    }

    protected String getLocalUrl() {       
        String fileName = url.getFile();
        return String.join("/", localFolder, fileName.substring(fileName.lastIndexOf('/') + 1));
    }

    protected void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    protected void error() {
        status = DownloadStatus.ERROR;
        stateChanged();
    }

    // Notify observers that this download's status has changed.
    protected void stateChanged() {
        setChanged();
        notifyObservers();
    }
}
