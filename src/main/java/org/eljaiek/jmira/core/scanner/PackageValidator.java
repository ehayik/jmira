package org.eljaiek.jmira.core.scanner;

import org.eljaiek.jmira.core.model.DebPackage;

/**
 *
 * @author eljaiek
 */
@FunctionalInterface
public interface PackageValidator {

    boolean validate(DebPackage pkg);
}
