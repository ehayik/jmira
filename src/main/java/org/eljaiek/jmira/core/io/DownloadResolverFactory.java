package org.eljaiek.jmira.core.io;

/**
 *
 * @author eljaiek
 */
@FunctionalInterface
public interface DownloadResolverFactory {

    DownloadResolver create(String scheme);
}
