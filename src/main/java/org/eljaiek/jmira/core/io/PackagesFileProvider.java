package org.eljaiek.jmira.core.io;

import java.io.File;
import java.util.Optional;

/**
 *
 * @author eduardo.eljaiek
 */
@FunctionalInterface
public interface PackagesFileProvider {

    Optional<File> getFile();
}
