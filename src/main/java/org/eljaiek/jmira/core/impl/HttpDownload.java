package org.eljaiek.jmira.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eljaiek.jmira.core.DownloadAdapter;
import org.eljaiek.jmira.core.DownloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author eduardo.eljaiek
 */
final class HttpDownload extends DownloadAdapter {
    
    private static final Logger LOG = LoggerFactory.getLogger(HttpDownload.class);

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
}
