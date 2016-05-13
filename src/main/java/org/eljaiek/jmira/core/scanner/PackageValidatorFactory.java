
package org.eljaiek.jmira.core.scanner;

/**
 *
 * @author eljaiek
 */
public interface PackageValidatorFactory {
    
    PackageValidator getPackageValidator(boolean checksum);
}
