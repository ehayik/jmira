
package org.eljaiek.jmira.core.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author eljaiek
 */
public final class ScannerConfiguration {

    public static final String CHAR_SET = "UTF8";

    private final String packagesFile;
    
    private final boolean checksum;

    private final String localHome;

    private final String remoteHome;
    
        public ScannerConfiguration(String packagesFile, boolean checksum, String localHome, String remoteHome) {
        this.checksum = checksum;
        this.localHome = localHome;
        this.remoteHome = remoteHome;
        this.packagesFile = packagesFile;
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
