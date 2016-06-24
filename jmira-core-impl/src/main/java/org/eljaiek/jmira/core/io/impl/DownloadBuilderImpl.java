package org.eljaiek.jmira.core.io.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import org.eljaiek.jmira.core.io.Download;
import org.eljaiek.jmira.core.io.DownloadBuilder;
import org.eljaiek.jmira.core.io.DownloadResolver;
import org.eljaiek.jmira.core.io.DownloadResolverFactory;
import org.eljaiek.jmira.core.logs.MessageResolver;
import org.eljaiek.jmira.core.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 *
 * @author eljaiek
 */
@Scope(scopeName = "prototype")
@Component
final class DownloadBuilderImpl implements DownloadBuilder {

    private String url;

    private String localFolder;

    private String checksum;

    private final List<Observer> observers;
    
    @Autowired
    private DownloadResolverFactory resolverFactory;

    public DownloadBuilderImpl() {
        observers = new ArrayList<>();
    }
    
    @Override
    public final DownloadBuilder url(String url) {
        Assert.isTrue(UrlUtils.isValid(url));
        this.url = url;
        return this;
    }

    @Override
    public final DownloadBuilder localFolder(String localFolder) {
        this.localFolder = localFolder;
        return this;
    }

    @Override
    public final DownloadBuilder checksum(String checksum) {
        this.checksum = checksum;
        return this;
    }

    @Override
    public final DownloadBuilder observer(Observer observer) {
        observers.add(observer);
        return this;
    }

    @Override
    public Download get() {
        Assert.notNull(url);
        Assert.hasText(localFolder);
        String scheme = url.substring(0, url.indexOf(':'));
        DownloadResolver resolver = resolverFactory.create(scheme);
        Download download = resolver.resolve(localFolder, url, checksum);
        observers.forEach(download::register);
        observers.clear();
        return download;
    }
}
