package org.eljaiek.jmira.app.util;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 *
 * @author eduardo.eljaiek
 */
public final class SettingsHelper {

    private static final String PREFS_KEY = "org.eljaiek.jmira.app.Preferences";

    public static final String USE_CHECKSUM = "app.settings.useChecksum";

    public static final String THREADS_NUMBER = "app.settings.threadsNumber";

    private static final Preferences PREFS;

    private static final Map<String, Object> DEF_VALUES;

    static {
        Preferences prefsRoot = Preferences.userRoot();
        PREFS = prefsRoot.node(PREFS_KEY);
        DEF_VALUES = new HashMap<>(2);
        DEF_VALUES.put(USE_CHECKSUM, false);
        DEF_VALUES.put(THREADS_NUMBER, Runtime.getRuntime().availableProcessors());
    }

    private SettingsHelper() {
    }

    public static String get(String key) {
        return PREFS.get(key, (String) DEF_VALUES.get(key));
    }

    public static boolean getBoolean(String key) {
        return PREFS.getBoolean(key, (boolean) DEF_VALUES.get(key));
    }

    public static int getInt(String key) {
        return PREFS.getInt(key, (int) DEF_VALUES.get(key));
    }

    public static <T> void set(String key, T value) {
        PREFS.put(key, value.toString());
    }
}
