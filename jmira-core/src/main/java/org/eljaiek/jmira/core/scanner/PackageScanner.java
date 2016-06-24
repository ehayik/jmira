package org.eljaiek.jmira.core.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author eljaiek
 */
@FunctionalInterface
public interface PackageScanner {

    PackageList scan(Context context) throws InvalidIndexFileException;

    public final class Context {

        private static final String CHAR_SET = "UTF8";

        private final String packagesFile;

        private final boolean checksum;

        private final String localHome;

        private final String remoteHome;

        public Context(String packagesFile, boolean checksum, String localHome, String remoteHome) {
            this.packagesFile = packagesFile;
            this.checksum = checksum;
            this.localHome = localHome;
            this.remoteHome = remoteHome;
        }

        public Scanner getScanner() throws FileNotFoundException {
            return new Scanner(new File(packagesFile), CHAR_SET);
        }

        public String getLocalHome() {
            return localHome;
        }

        public String getRemoteHome() {
            return remoteHome;
        }

        public boolean isChecksum() {
            return checksum;
        }
    }
}
