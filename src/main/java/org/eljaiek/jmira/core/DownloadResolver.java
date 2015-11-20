package org.eljaiek.jmira.core;

/**
 *
 * @author eduardo.eljaiek
 */
public interface DownloadResolver {

    Download resolve(String localFolder, String url);
}
