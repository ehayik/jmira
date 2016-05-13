
package org.eljaiek.jmira.core.scanner;

/**
 *
 * @author eljaiek
 */
public interface PackageScanner {
    
    PackageList scan(ScannerConfiguration configuration) throws InvalidPackagesFileException;
}
