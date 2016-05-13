
package org.eljaiek.jmira.core.scanner.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.eljaiek.jmira.core.scanner.PackageValidator;
import org.eljaiek.jmira.core.model.DebPackage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
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
