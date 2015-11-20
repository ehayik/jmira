package org.eljaiek.jmira.data.repositories;

import java.io.File;
import java.util.Optional;

/**
 *
 * @author eduardo.eljaiek
 */
public interface PackagesFileProvider {

    Optional<File> getFile();
}
