package org.eljaiek.jmira.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shidara
 */
public class Repository {

    private String name;

    private String home;

    private int packagesCount;

    private int downloadedCount;

    private long size;

    private long downloadedSize;

    private List<Architecture> architectures;

    private  List<Source> sources;

    public Repository() {
        sources = new ArrayList<>(4);
        architectures = new ArrayList<>(2);
    }

    public Repository(String name, String home, int packagesCount, int downloadedCount, long downloadedSize) {
        this.name = name;
        this.home = home;
        this.packagesCount = packagesCount;
        this.downloadedCount = downloadedCount;
        this.downloadedSize = downloadedSize;
        this.architectures = new ArrayList<>(2);
        sources = new ArrayList<>(4);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public int getPackagesCount() {
        return packagesCount;
    }

    public void setPackagesCount(int packagesCount) {
        this.packagesCount = packagesCount;
    }

    public int getDownloadedCount() {
        return downloadedCount;
    }

    public void setDownloadedCount(int downloadedCount) {
        this.downloadedCount = downloadedCount;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public List<Architecture> getArchitectures() {
        return architectures;
    }

    public void setArchitectures(List<Architecture> architectures) {
        this.architectures = architectures;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Repository{");
        sb.append("name='").append(name).append('\'');
        sb.append(", home='").append(home).append('\'');
        sb.append(", packagesCount=").append(packagesCount);
        sb.append(", downloadedCount=").append(downloadedCount);
        sb.append(", size=").append(size);
        sb.append(", downloadedSize=").append(downloadedSize);
        sb.append(", architectures=").append(architectures);
        sb.append(", sources=").append(sources);
        sb.append('}');
        return sb.toString();
    }
}
