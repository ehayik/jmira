package org.eljaiek.jmira.app.util;


import java.io.File;
import java.text.DecimalFormat;

/**
 *
 * @author eduardo.eljaiek
 */
public final class FileSystemHelper {

    private FileSystemHelper() {
    }

    public static final String formatSize(long bytes) {
        String format = bytes + "Bytes";
        long kilobyte = 1024;
        long megabyte = kilobyte * 1024;
        long gigabyte = megabyte * 1024;
        long terabyte = gigabyte * 1024;
        DecimalFormat formatter = new DecimalFormat("#.##");

        if (bytes > terabyte) {
            float value = ((float) bytes / (float) terabyte);
            format = formatter.format(value) + " TB";
        } else if (bytes > gigabyte) {
            float value = ((float) bytes / (float) gigabyte);
            format = formatter.format(value) + " GB";
        } else if (bytes > megabyte) {
            float value = ((float) bytes / (float) megabyte);                           
            format = formatter.format(value) + " MB";
        } else if ((bytes > kilobyte)) {
            float value = ((float) bytes / (float) kilobyte);
            format = formatter.format(value) + " KB";
        }

        return format;
    }

    public static final double getUsedSpacePercent(String path) {
        File f = new File(path);
        long used = f.getTotalSpace() - f.getFreeSpace();
        return used * 100 / f.getTotalSpace();
    }
}
