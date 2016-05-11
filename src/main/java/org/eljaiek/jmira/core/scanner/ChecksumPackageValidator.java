/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eljaiek.jmira.core.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.eljaiek.jmira.data.model.DebPackage;

final class ChecksumPackageValidator implements PackageValidator {

    @Override
    public boolean validate(DebPackage pkg) {
        File file = new File(pkg.getLocalUrl());

        try (InputStream stream = new FileInputStream(file)) {
            String md5 = DigestUtils.md5Hex(stream);
            return md5.equals(pkg.getChecksum());
        } catch (IOException ex) {
            return false;
        }
    }
}
