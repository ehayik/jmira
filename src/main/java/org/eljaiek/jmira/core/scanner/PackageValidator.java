/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eljaiek.jmira.core.scanner;

import org.eljaiek.jmira.data.model.DebPackage;

/**
 *
 * @author eduardo.eljaiek
 */
interface PackageValidator {

    boolean validate(DebPackage pkg);
}
