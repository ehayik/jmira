package org.eljaiek.jmira.core.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import org.eljaiek.jmira.core.DownloadAdapter;
import org.eljaiek.jmira.core.DownloadException;
import org.eljaiek.jmira.core.DownloadStatus;

/**
 *
 * @author shidara
 */
final class FileDownload extends DownloadAdapter {

    private static final int MAX_BUFFER_SIZE = 1024;

    public FileDownload(String localFolder, URL url) {
        super(localFolder, url);
    }

    @Override
    public final void run() {
        File file = new File(getUrl().getFile());

        if (getSize() == -1) {
            setSize((int) file.length());
            stateChanged();
        }

        try (RandomAccessFile raf = new RandomAccessFile(getLocalUrl(), "rw")) {
            raf.seek(getDownloaded());

            try (RandomAccessFile stream = new RandomAccessFile(file, "r")) {
                stream.seek(getDownloaded());
                write(raf, stream);
            }

        } catch (IOException ex) {
            error();
            throw new DownloadException(ex.getMessage(), ex);
        }
    }

    private void write(RandomAccessFile raf, RandomAccessFile stream) throws IOException {

        while (getStatus() == DownloadStatus.DOWNLOADING 
                && stream.getFilePointer() != stream.length()) {
            byte buffer[];

            if (getSize() - getDownloaded() > MAX_BUFFER_SIZE) {
                buffer = new byte[MAX_BUFFER_SIZE];
            } else {
                buffer = new byte[getSize() - getDownloaded()];
            }

            int read = stream.read(buffer);

            if (read == -1) {
                break;
            }

            raf.write(buffer, 0, read);
            setDownloaded(getDownloaded() + read);
            stateChanged();
        }

        if (getStatus() == DownloadStatus.DOWNLOADING) {
            setStatus(DownloadStatus.COMPLETE);
            stateChanged();
        }
    }
}
