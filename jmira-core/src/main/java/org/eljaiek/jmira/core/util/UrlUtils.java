package org.eljaiek.jmira.core.util;

import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author eljaiek
 */
public final class UrlUtils {

    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[]{"http", "https", "file"});

    private UrlUtils() {
        throw new AssertionError();
    }

    public static final boolean isValid(String url) {
        return URL_VALIDATOR.isValid(url);
    }
}
