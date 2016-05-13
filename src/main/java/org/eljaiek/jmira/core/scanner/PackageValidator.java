
package org.eljaiek.jmira.core.scanner;

import org.eljaiek.jmira.core.model.DebPackage;

/**
 *
 * @author eljaiek
 */
public interface PackageValidator {

    boolean validate(DebPackage pkg);
}
