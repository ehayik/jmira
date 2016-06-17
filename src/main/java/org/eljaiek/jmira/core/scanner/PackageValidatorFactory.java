
package org.eljaiek.jmira.core.scanner;

/**
 *
 * @author eljaiek
 */
@FunctionalInterface
public interface PackageValidatorFactory {
    
    PackageValidator getPackageValidator(boolean checksum);
}
