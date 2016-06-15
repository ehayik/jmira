package org.eljaiek.jmira.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author eljaiek
 */
public final class FileUtils {

    private FileUtils() {
        throw new AssertionError();
    }

    public static boolean checkSum(File file, String checksum) {

        try (InputStream stream = new FileInputStream(file)) {
            String md5 = DigestUtils.md5Hex(stream);
            return md5.equals(checksum);
        } catch (IOException ex) {
            return false; 
        }
    }
}
