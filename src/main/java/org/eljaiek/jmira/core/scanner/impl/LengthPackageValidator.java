package org.eljaiek.jmira.core.scanner.impl;

import java.io.File;
import org.eljaiek.jmira.core.scanner.PackageValidator;
import org.eljaiek.jmira.core.model.DebPackage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
final class LengthPackageValidator implements PackageValidator {

    @Override
    public boolean validate(DebPackage pkg) {
        File file = new File(pkg.getLocalUrl());
        return file.length() == pkg.getLength();
    }
}
