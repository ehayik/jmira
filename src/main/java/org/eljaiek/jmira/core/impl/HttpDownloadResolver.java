package org.eljaiek.jmira.core.impl;

import java.net.MalformedURLException;
import java.net.URL;
import org.eljaiek.jmira.core.Download;
import org.eljaiek.jmira.core.DownloadResolver;
import org.springframework.util.Assert;

/**
 *
 * @author eduardo.eljaiek
 */
public final class HttpDownloadResolver implements DownloadResolver {

    @Override
    public final Download resolve(String localFolder, String url) {
        Assert.isTrue(url.startsWith("http"));

        try {
            return new HttpDownload(localFolder, new URL(url));
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
