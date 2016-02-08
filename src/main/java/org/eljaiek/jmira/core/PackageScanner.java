package org.eljaiek.jmira.core;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javax.xml.ws.WebServiceException;
import org.eljaiek.jmira.core.util.ValidationUtils;
import org.eljaiek.jmira.data.model.DebPackage;

/**
 *
 * @author eduardo.eljaiek
 */
public final class PackageScanner implements Iterator<DebPackage>, Closeable {

    public static final String CHAR_SET = "UTF8";
    
    private static final String PACKAGE_TAG = "Package";

    private static final String DESCRIPTION_TAG = "Description";

    private static final String FILENAME_TAG = "Filename";

    private static final String VERSION_TAG = "Version";

    private static final String SIZE_TAG = "Size";
    
    private static final String CHECKSUM_TAG = "MD5sum";

    private final Scanner scanner;
    
    private Optional<String> remoteHome = Optional.empty();
    
    private Optional<String> localHome = Optional.empty();

    public PackageScanner(String packagesFile) throws FileNotFoundException {
        scanner = new Scanner(new File(packagesFile), CHAR_SET);
    }

    public PackageScanner(String packagesFile, String localHome, String remoteHome) throws FileNotFoundException {
        this(packagesFile);
        this.localHome = Optional.of(localHome);
        this.remoteHome = Optional.of(remoteHome);
    }    

    @Override
    public boolean hasNext() {
        return scanner.hasNextLine();
    }

    @Override
    public DebPackage next() {
        DebPackage pkg = new DebPackage();
        String line = scanner.nextLine();

        while (line.trim().length() != 0) {

            switch (line.split(":")[0]) {

                case PACKAGE_TAG: {
                    pkg.setName(line.split(": ")[1]);
                }
                break;

                case SIZE_TAG: {
                    long size = Long.parseLong(line.split(": ")[1]);
                    pkg.setSize(size);
                }
                break;

                case FILENAME_TAG: {
                    pkg.setRelativeUrl(line.split(": ")[1]);
                }
                break;

                case VERSION_TAG: {
                    pkg.setVersion(line.split(": ")[1]);
                }
                break;

                case DESCRIPTION_TAG: {
                    pkg.setDescription(line.split(": ")[1]);
                }
                break;
                
                case CHECKSUM_TAG: {
                    pkg.setChecksum(line.split(": ")[1]);
                }
                break;
            }

            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException ex) {
                line = "";
            }
        }

        return pkg;
    }

    @Override
    public void close() throws WebServiceException {
        scanner.close();
    }

    public final PackageList list() {
        int downloadedCount = 0;
        long size = 0;
        long downloaded = 0;
        List<DebPackage> packages = new ArrayList<>();

        while (hasNext()) {
            DebPackage pkg = next();
            
            if (localHome.isPresent()) {
                pkg.setLocalUrl(String.join("/", localHome.get(), pkg.getRelativeUrl()));                
            }
            
            if (remoteHome.isPresent()) {
                pkg.setRemoteUrl(String.join("/", remoteHome.get(), pkg.getRelativeUrl()));
            }
            
            if (ValidationUtils.isValidFile(pkg.getLocalUrl(), pkg.getChecksum())) {
                downloaded += pkg.getSize();
                downloadedCount++;
            }

            size += pkg.getSize();
            packages.add(pkg);
        }       

        return new PackageList(packages, downloadedCount, size, downloaded);
    }

    public class PackageList implements Iterable<DebPackage> {

        private final  List<DebPackage> packages;

        private final int downloadedCount;

        private final long size;

        private final long downloaded;

        public PackageList(List<DebPackage> packages, int downloadedCount, long size, long downloaded) {
            this.packages = packages;
            this.downloadedCount = downloadedCount;
            this.size = size;
            this.downloaded = downloaded;
        }

        @Override
        public Iterator<DebPackage> iterator() {
            return packages.iterator();
        }        

        public List<DebPackage> getPackages() {
            return packages;
        }

        public int getCount() { return packages.size(); }

        public int getDownloadedCount() {
            return downloadedCount;
        }

        public long getDownloaded() {
            return downloaded;
        }

        public long getSize() {
            return size;
        }
    }
}
