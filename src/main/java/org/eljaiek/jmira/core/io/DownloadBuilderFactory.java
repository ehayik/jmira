package org.eljaiek.jmira.core.io;

/**
 *
 * @author eljaiek
 */
@FunctionalInterface
public interface DownloadBuilderFactory {

    DownloadBuilder create();
}
