
package org.eljaiek.jmira.core.scanner;

/**
 *
 * @author eljaiek
 */
@FunctionalInterface
public interface PackageScanner {
    
    PackageList scan(ScannerConfiguration configuration) throws InvalidPackagesFileException;
}
