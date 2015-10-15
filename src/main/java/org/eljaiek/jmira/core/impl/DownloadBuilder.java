
package org.eljaiek.jmira.core.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import org.eljaiek.jmira.core.Download;
import org.eljaiek.jmira.core.DownloadAdapter;
import org.springframework.util.Assert;

/**
 *
 * @author eduardo.eljaiek
 */
public final class DownloadBuilder {

    private URL url; 
    
    private String localFolder;
    
    private final List<Observer> observers = new ArrayList<>();
    
    private DownloadBuilder() {
    }
    
    public static final DownloadBuilder create() {
        return new DownloadBuilder();
    }
    
    public final DownloadBuilder url(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        return this;
    }
    
    public final DownloadBuilder localFolder(String localFolder) {
        this.localFolder = localFolder;
        return this;
    }
    
    public final DownloadBuilder observer(Observer observer) {
        observers.add(observer);
        return this;
    }
    
    public final Download get() {
        Assert.notNull(url);
        Assert.hasText(localFolder);
        Download download;
        
        if (url.getFile().startsWith("http")) {
            download = new HttpDownload(localFolder, url);
        } else {
            download = new FileDownload(localFolder, url);
        }
        
        observers.forEach(ob -> ((DownloadAdapter) download).addObserver(ob));     
        return download;        
    }
}
