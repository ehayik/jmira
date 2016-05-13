package org.eljaiek.jmira.core.io.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.eljaiek.jmira.core.io.Download;
import org.eljaiek.jmira.core.io.DownloadResolver;
import org.springframework.util.Assert;

/**
 *
 * @author eduardo.eljaiek
 */
public final class FileDownloadResolver implements DownloadResolver {

    @Override
    public Download resolve(String localFolder, String url, Optional<String> checksum) {
        Assert.isTrue(url.startsWith("file"));

        try {
            return new FileDownload(localFolder, new URL(url), checksum);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
