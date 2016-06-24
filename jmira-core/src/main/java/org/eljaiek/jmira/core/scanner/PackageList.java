
package org.eljaiek.jmira.core.scanner;

import java.util.Iterator;
import java.util.List;
import org.eljaiek.jmira.core.model.DebPackage;

/**
 *
 * @author eduardo.eljaiek
 */
public final class PackageList implements Iterable<DebPackage> {
     private final List<DebPackage> packages;

        private final int downloads;

        private final long availableSize;

        private final long downloadsSize;

        public PackageList(List<DebPackage> packages, int downloads, long availableSize, long downloadsSize) {
            this.packages = packages;
            this.downloads = downloads;
            this.availableSize = availableSize;
            this.downloadsSize = downloadsSize;
        }

        @Override
        public Iterator<DebPackage> iterator() {
            return packages.iterator();
        }

        public List<DebPackage> getPackages() {
            return packages;
        }

        public int getAvailable() {
            return packages.size();
        }

        public int getDownloads() {
            return downloads;
        }

        public long getDownloadsSize() {
            return downloadsSize;
        }

        public long getAvailableSize() {
            return availableSize;
        }
}
