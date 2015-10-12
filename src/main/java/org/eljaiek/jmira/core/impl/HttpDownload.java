package org.eljaiek.jmira.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eljaiek.jmira.core.DownloadAdapter;
import org.eljaiek.jmira.core.DownloadException;
import org.eljaiek.jmira.core.DownloadStatus;

/**
 *
 * @author eduardo.eljaiek
 */
final class HttpDownload extends DownloadAdapter {

    // Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;
    
    public HttpDownload(String localFolder, URL url) {
        super(localFolder, url);
    }    
    
    @Override
    public final void run() {
        
        try {
            HttpURLConnection connection = (HttpURLConnection) getUrl().openConnection();
            connection.setRequestProperty("Range", "bytes=" + getDownloaded() + "-");
            connection.connect();
            
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
            }

            /* Set the size for this download if it
             hasn't been already set. */
            if (getSize() == -1) {
                setSize(contentLength);
                stateChanged();
            }
            
            try (RandomAccessFile file = new RandomAccessFile(getLocalUrl(), "rw")) {
                file.seek(getDownloaded());
                
                try (InputStream stream = connection.getInputStream()) {
                    write(file, stream);
                }
            }
            
        } catch (IOException ex) {
            error();
            throw new DownloadException(ex.getMessage(), ex);
        }
    }
    
    private void write(RandomAccessFile file, InputStream stream) throws IOException {
        
        while (getStatus() == DownloadStatus.DOWNLOADING) {
            /* Size buffer according to how much of the
             file is left to download. */
            byte buffer[];
            
            if (getSize() - getDownloaded() > MAX_BUFFER_SIZE) {
                buffer = new byte[MAX_BUFFER_SIZE];
            } else {
                buffer = new byte[getSize() - getDownloaded()];
            }

            // Read from server into buffer.
            int read = stream.read(buffer);
            
            if (read == -1) {
                break;
            }

            // Write buffer to file.
            file.write(buffer, 0, read);
            setDownloaded(getDownloaded() + read);            
            stateChanged();
        }

        /* Change status to complete if this point was
         reached because downloading has finished. */
        if (getStatus() == DownloadStatus.DOWNLOADING) {
            setStatus(DownloadStatus.COMPLETE);
            stateChanged();
        }
    }
}
