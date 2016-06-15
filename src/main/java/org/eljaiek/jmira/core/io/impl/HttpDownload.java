package org.eljaiek.jmira.core.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eljaiek.jmira.core.io.DownloadAdapter;
import org.eljaiek.jmira.core.io.DownloadFailedException;

/**
 *
 * @author eduardo.eljaiek
 */
final class HttpDownload extends DownloadAdapter {

    public HttpDownload(String localFolder, URL url, String checksum) {
        super(localFolder, url, checksum);
    }

    @Override
    public void start() {
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
            throw new DownloadFailedException(ex.getMessage(), ex);
        }
    }

    @Override
    protected boolean isFileCorrupted() {
        return false;
    }
}
