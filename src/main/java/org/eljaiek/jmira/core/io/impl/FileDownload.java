package org.eljaiek.jmira.core.io.impl;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import org.eljaiek.jmira.core.io.DownloadAdapter;
import org.eljaiek.jmira.core.io.DownloadFailedException;

/**
 *
 * @author shidara
 */
final class FileDownload extends DownloadAdapter {

    public FileDownload(String localFolder, URL url, Optional<String> checksum) {
        super(localFolder, url, checksum);
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

            try (InputStream stream = new FileInputStream(file)) {
                write(raf, stream);
            }

        } catch (IOException ex) {
            error();
            throw new DownloadFailedException(ex.getMessage(), ex);
        }
    }
}
