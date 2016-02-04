package org.eljaiek.jmira.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
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
    
    public static final boolean isValidFile(String file, String checksum) {        
        Assert.hasText(file);
        return isValidFile(new File(file), checksum);
    }
    
    public static final boolean isValidFile(File file, String checksum) {
        Assert.notNull(file);
        Assert.hasText(checksum);
        
        try (InputStream stream = new FileInputStream(file)) {
            String md5 = DigestUtils.md5Hex(stream);
            return md5.equals(checksum); 
        } catch (IOException ex) {
            return false;
        }
    }
}
