
package org.eljaiek.jmira.core.util;

/**
 *
 * @author eduardo.eljaiek
 */
public final class DebianNamesUtils {
    
    public final static String DISTS_FOLDER = "dists";
    
    public final static String POOL_FOLDER = "pool";
    
    public static final String RELEASE = "Release";
    
    public static final String RELEASE_GPG = "Release.gpg";
    
    public static final String PACKAGES = "Packages";
    
    public static final String PACKAGES_GZ = "Packages.gz";

    public static final String PACKAGES_BZ2 = "Packages.bz2";
    
    private DebianNamesUtils() {
        throw new AssertionError();
    }   
}
