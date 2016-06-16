package org.eljaiek.jmira.core.io;

/**
 *
 * @author eduardo.eljaiek
 */
@FunctionalInterface
public interface DownloadResolver {

    Download resolve(String localFolder, String url, String checksum);
}
