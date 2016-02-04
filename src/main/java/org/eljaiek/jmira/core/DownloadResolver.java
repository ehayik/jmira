package org.eljaiek.jmira.core;

import java.util.Optional;

/**
 *
 * @author eduardo.eljaiek
 */
public interface DownloadResolver {

    Download resolve(String localFolder, String url, Optional<String> checksum);
}
