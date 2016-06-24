package org.eljaiek.jmira.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import org.eljaiek.jmira.core.util.FileUtils;

/**
 *
 * @author eduardo.eljaiek
 */
public abstract class DownloadAdapter extends Observable implements Download {

    private static final int MAX_BUFFER_SIZE = 8192;

    protected final String checksum;

    private DownloadStatus status = DownloadStatus.DOWNLOADING;

    private int size = -1;

    private int downloaded;

    private final String localFolder;

    private final URL url;

    public DownloadAdapter(String localFolder, URL url, String checksum) {
        this.localFolder = localFolder;
        this.url = url;
        this.checksum = checksum;
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
        start();
    }

    @Override
    public void cancel() {
        status = DownloadStatus.CANCELLED;
        stateChanged();
    }

//    @Override
//    public void run() {
//        start();
//    }  

//    @Override
//    public void clean() {
//        new File(getLocalUrl()).delete();
//    }

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

//    protected void setUrl(URL url) {
//        this.url = url;
//    }

    @Override
    public String getLocalUrl() {
        File folder = new File(localFolder);

        if (!folder.exists()) {
            folder.mkdirs();
        }

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

    protected void write(RandomAccessFile raf, InputStream stream) throws IOException {

        while (getStatus() == DownloadStatus.DOWNLOADING) {
            /* Size buffer according to how much of the
             file is left to download. */
            byte[] buffer;

            if (getSize() - getDownloaded() > MAX_BUFFER_SIZE) {
                buffer = new byte[MAX_BUFFER_SIZE];
            } else {
                buffer = new byte[getSize() - getDownloaded()];
            }

            // Read from server into buffer.
            int read = stream.read(buffer);

            if (read <= 0) {
                break;
            }

            // Write buffer to file.
            raf.write(buffer, 0, read);
            setDownloaded(getDownloaded() + read);
            stateChanged();
        }

        /* Change status to complete if this point was
         reached because downloading has finished. */
        if (getStatus() == DownloadStatus.DOWNLOADING) {
            DownloadStatus stat = DownloadStatus.COMPLETE;
            
            if (isFileCorrupted()) {
                stat = DownloadStatus.CORRUPTED;
            }

            setStatus(stat);
            stateChanged();
        }
    }
    
    private boolean isFileCorrupted() {
        File file = new File(getLocalUrl());
        
        if(checksum != null && !checksum.trim().isEmpty()) {
            return FileUtils.checkSum(file, checksum);
        }
        
        return file.length() == size;
    }

    @Override
    public void register(Observer observer) {
        addObserver(observer);
    }
}
