package org.eljaiek.jmira.core.scanner;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javax.xml.ws.WebServiceException;
import org.eljaiek.jmira.app.util.ValidationUtils;
import org.eljaiek.jmira.data.model.DebPackage;

/**
 *
 * @author eduardo.eljaiek
 */
public final class PackageScanner implements Iterator<DebPackage>, Closeable {

    public static final String CHAR_SET = "UTF8";

    private static final String PACKAGE_TAG = "Package";

    private static final String DESCRIPTION_TAG = "Description";

    private static final String FILENAME_TAG = "Filename";

    private static final String VERSION_TAG = "Version";

    private static final String SIZE_TAG = "Size";

    private static final String CHECKSUM_TAG = "MD5sum";

    private final Scanner scanner;

    private final Optional<ScannerConfiguration> config;

    public PackageScanner(String packagesFile) throws FileNotFoundException {
        this(packagesFile, null);
    }

    public PackageScanner(String packagesFile, ScannerConfiguration configuration) throws FileNotFoundException {
        scanner = new Scanner(new File(packagesFile), CHAR_SET);
        config = Optional.ofNullable(configuration);
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNextLine();
    }

    @Override
    public DebPackage next() {
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
    public void close() throws WebServiceException {
        scanner.close();
    }

    public final PackageList list() {
        int downloads = 0;
        long availableSize = 0;
        long downloadsSize = 0;
        List<DebPackage> packages = new ArrayList<>();
        String localHome = (config.isPresent()) ? config.get().getLocalHome() : null;
        String remoteHome = (config.isPresent()) ? config.get().getRemoteHome() : null;
        PackageValidator validator = config.get().getPackageValidator();

        while (hasNext()) {
            DebPackage pkg = next();

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

            if (ValidationUtils.isValidFile(pkg.getLocalUrl(), pkg.getChecksum())) {
                downloadsSize += pkg.getLength();
                downloads++;
            }

            availableSize += pkg.getLength();
            packages.add(pkg);
        }

        return new PackageList(packages, downloads, availableSize, downloadsSize);
    }

}
