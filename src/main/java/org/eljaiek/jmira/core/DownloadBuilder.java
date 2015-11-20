package org.eljaiek.jmira.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import org.eljaiek.jmira.core.util.ValidationUtils;
import org.springframework.util.Assert;

/**
 *
 * @author eduardo.eljaiek
 */
public final class DownloadBuilder {
    
    private static final Map<String, DownloadResolver> resolvers = new ConcurrentHashMap<>();
    
    private String url;
    
    private String localFolder;
    
    private final List<Observer> observers = new ArrayList<>();
    
    private DownloadBuilder() {
    }
    
    public static void register(String prefix, DownloadResolver resolver) {
        resolvers.put(prefix, resolver);
    }
    
    public static DownloadBuilder create() {
        return new DownloadBuilder();
    }
    
    public final DownloadBuilder url(String url) {
        Assert.isTrue(ValidationUtils.isValidUrl(url));
        this.url = url;
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
        String prefix = url.substring(0, url.indexOf(":"));
        DownloadResolver resolver = resolvers.get(prefix);
        
        if (resolver == null) {
            throw new IllegalArgumentException(MessageResolver.getDefault().getMessage("dowloadBuilder.error"));
        }
        
        Download download = resolver.resolve(localFolder, url);        
        observers.forEach(download::register);
        observers.clear();
        return download;
    }
}
