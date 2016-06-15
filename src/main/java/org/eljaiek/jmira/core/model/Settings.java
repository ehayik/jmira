package org.eljaiek.jmira.core.model;

/**
 *
 * @author eljaiek
 */
public class Settings {

    private boolean checksum;

    private int downloadThreads;

    public Settings() {    
    }

    public Settings(boolean checksum, int downloadThreads) {
        this.checksum = checksum;
        this.downloadThreads = downloadThreads;
    }

    public boolean isChecksum() {
        return checksum;
    }

    public void setChecksum(boolean checksum) {
        this.checksum = checksum;
    }

    public int getDownloadThreads() {
        return downloadThreads;
    }

    public void setDownloadThreads(int downloadThreads) {
        this.downloadThreads = downloadThreads;
    }
}
