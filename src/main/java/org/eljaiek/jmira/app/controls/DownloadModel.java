package org.eljaiek.jmira.app.controls;

import org.eljaiek.jmira.core.io.Download;

import java.util.Observer;

/**
 * Created by shidara on 9/12/15.
 */
final class DownloadModel {

    private final String packageName;

    private final long size;

    private final Download download;

    public DownloadModel(String packageName, long size, Download download) {
        this.packageName = packageName;
        this.download = download;
        this.size = size;
    }

    public String getPackageName() {
        return packageName;
    }

    public void register(Observer observer) {
        download.register(observer);
    }

    public void run() {
        download.run();
    }

    public long getSize() {
        return size;
    }

    public int getDownloaded() {
        return download.getDownloaded();
    }

    public void cancel() {
        download.cancel();

        if (download.getSize() != download.getDownloaded()) {
            download.clean();
        }
    }
}
