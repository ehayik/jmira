/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eljaiek.jmira.core.scanner;

import java.io.File;
import org.eljaiek.jmira.data.model.DebPackage;

final class LengthPackageValidator implements PackageValidator {

    @Override
    public boolean validate(DebPackage pkg) {
       File file = new File(pkg.getLocalUrl());       
       return file.length() == pkg.getLength();
    }
}
