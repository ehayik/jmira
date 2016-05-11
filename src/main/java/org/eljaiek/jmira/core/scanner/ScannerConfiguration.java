
package org.eljaiek.jmira.core.scanner;

/**
 *
 * @author eduardo.eljaiek
 */
public final class ScannerConfiguration {

    private static final ChecksumPackageValidator CHECKSUM_VALIDATOR = new ChecksumPackageValidator();
    
    private static final LengthPackageValidator LENGTH_VALIDATOR = new LengthPackageValidator();
    
    private final boolean checksum;

    private final String localHome;

    private final String remoteHome;

    public ScannerConfiguration(boolean checksum, String localHome, String remoteHome) {
        this.checksum = checksum;
        this.localHome = localHome;
        this.remoteHome = remoteHome;
    }

    public String getLocalHome() {
        return localHome;
    }

    public String getRemoteHome() {
        return remoteHome;
    }
    
    public PackageValidator getPackageValidator() {
        return checksum ? CHECKSUM_VALIDATOR : LENGTH_VALIDATOR; 
    }
}
