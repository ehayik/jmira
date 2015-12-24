package org.eljaiek.jmira.core.impl;

import java.io.*;
import java.net.URL;
import org.eljaiek.jmira.core.DownloadAdapter;
import org.eljaiek.jmira.core.DownloadException;

/**
 *
 * @author shidara
 */
final class FileDownload extends DownloadAdapter {

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

            try(InputStream stream = new FileInputStream(file)) {
                write(raf, stream);
            }

        } catch (IOException ex) {
            error();
            throw new DownloadException(ex.getMessage(), ex);
        }
    }
}
