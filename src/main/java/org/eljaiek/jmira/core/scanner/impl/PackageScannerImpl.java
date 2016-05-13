package org.eljaiek.jmira.core.scanner.impl;

import java.io.IOException;
import java.util.*;
import org.eljaiek.jmira.core.scanner.PackageList;
import org.eljaiek.jmira.core.scanner.PackageScanner;
import org.eljaiek.jmira.core.scanner.InvalidPackagesFileException;
import org.eljaiek.jmira.core.scanner.PackageValidator;
import org.eljaiek.jmira.core.scanner.PackageValidatorFactory;
import org.eljaiek.jmira.core.scanner.ScannerConfiguration;
import org.eljaiek.jmira.core.model.DebPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author eljaiek
 */
@Component
public final class PackageScannerImpl implements PackageScanner {

    public static final String CHAR_SET = "UTF8";

    private static final String PACKAGE_TAG = "Package";

    private static final String DESCRIPTION_TAG = "Description";

    private static final String FILENAME_TAG = "Filename";

    private static final String VERSION_TAG = "Version";

    private static final String SIZE_TAG = "Size";

    private static final String CHECKSUM_TAG = "MD5sum";

    private final PackageValidatorFactory validatorFactory;

    @Autowired
    public PackageScannerImpl(PackageValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }    
    
    private static DebPackage next(Scanner scanner) {
        DebPackage pkg = new DebPackage();
        String line = scanner.nextLine();

        while (line.trim().length() != 0) {

            switch (line.split(":")[0]) {

                case PACKAGE_TAG: {
                    pkg.setName(line.split(": ")[1]);
                }
                break;

                case SIZE_TAG: {
                    long size = Long.parseLong(line.split(": ")[1]);
                    pkg.setLength(size);
                }
                break;

                case FILENAME_TAG: {
                    pkg.setRelativeUrl(line.split(": ")[1]);
                }
                break;

                case VERSION_TAG: {
                    pkg.setVersion(line.split(": ")[1]);
                }
                break;

                case DESCRIPTION_TAG: {
                    pkg.setDescription(line.split(": ")[1]);
                }
                break;

                case CHECKSUM_TAG: {
                    pkg.setChecksum(line.split(": ")[1]);
                }
                break;
            }

            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException ex) {
                line = "";
            }
        }

        return pkg;
    }

    @Override
    public final PackageList scan(ScannerConfiguration configuration) throws InvalidPackagesFileException {
        int downloads = 0;
        long availableSize = 0;
        long downloadsSize = 0;
        List<DebPackage> packages = new ArrayList<>();
        String localHome = configuration.getLocalHome();
        String remoteHome = configuration.getRemoteHome();
        PackageValidator validator = validatorFactory.getPackageValidator(configuration.isChecksum());

        try (Scanner scanner = configuration.getScanner()) {

            while (scanner.hasNextLine()) {
                DebPackage pkg = next(scanner);

                if (localHome != null) {
                    pkg.setLocalUrl(String.join("/", localHome, pkg.getRelativeUrl()));
                }

                if (remoteHome != null) {
                    pkg.setRemoteUrl(String.join("/", remoteHome, pkg.getRelativeUrl()));
                }

                if (validator.validate(pkg)) {
                    downloadsSize += pkg.getLength();
                    downloads++;
                }

                availableSize += pkg.getLength();
                packages.add(pkg);
            }
            
             return new PackageList(packages, downloads, availableSize, downloadsSize);
            
        } catch (IOException ex) {
            throw new InvalidPackagesFileException(ex);
        }       
    }  
}
