package org.eljaiek.jmira.core.util;

import java.io.File;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.util.Assert;

/**
 *
 * @author eduardo.eljaiek
 */
public final class ValidationUtils {

    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https", "file"});

    private ValidationUtils() {
    }

    public static final boolean isValidUrl(String url) {
        return URL_VALIDATOR.isValid(url);
    }
    
    public static final boolean isValid(String packagePath, String checksum) {
        Assert.hasText(packagePath);
        return new File(packagePath).exists();        
    }
}
