package org.eljaiek.jmira.app.util;

import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author eduardo.eljaiek
 */
public final class ValidationHelper {

    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https", "file"});

    private ValidationHelper() {
    }

    public static final boolean isValidUrl(String url) {
        return URL_VALIDATOR.isValid(url);
    }
}
