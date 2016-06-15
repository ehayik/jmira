package org.eljaiek.jmira.core.io;

/**
 *
 * @author eljaiek
 */
public interface DownloadResolverFactory {

    DownloadResolver create(String scheme);
}
