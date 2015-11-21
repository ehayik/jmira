package org.eljaiek.jmira.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import static org.eljaiek.jmira.core.DebianNamesUtils.DISTS_FOLDER;
import static org.eljaiek.jmira.core.DebianNamesUtils.PACKAGES;
import static org.eljaiek.jmira.core.DebianNamesUtils.PACKAGES_BZ2;
import static org.eljaiek.jmira.core.DebianNamesUtils.PACKAGES_GZ;
import static org.eljaiek.jmira.core.DebianNamesUtils.RELEASE;
import static org.eljaiek.jmira.core.DebianNamesUtils.RELEASE_GPG;
import org.eljaiek.jmira.core.DownloadBuilder;
import org.eljaiek.jmira.core.DownloadException;
import org.eljaiek.jmira.data.model.Architecture;
import org.eljaiek.jmira.data.model.Repository;
import org.eljaiek.jmira.data.model.Source;
import org.itadaki.bzip2.BZip2InputStream;

/**
 *
 * @author eduardo.eljaiek
 */
final class SourcesHelper {

    private static final String SLASH = "/";

    private SourcesHelper() {
    }

    static List<SourceFiles> download(Repository repository) {
        List<SourceFiles> result = new ArrayList<>(repository.getSources().size());

        repository.getSources().forEach(src -> {

            if (src.isEnabled()) {
                SourceFiles sf = download(repository, src);
                result.add(sf);
            }
        });

        return result;
    }

    private static SourceFiles download(Repository repository, Source source) {
        final SourceFiles sf = new SourceFiles(source.getUri());
        String[] arr = source.getUri().split(SLASH);
        final String remoteFolder = String.join(SLASH, source.getUri(), DISTS_FOLDER, source.getDistribution());
        final File folder = new File(String.join(SLASH, repository.getHome(), arr[arr.length - 1], DISTS_FOLDER, source.getDistribution()));
        folder.mkdirs();

        DownloadBuilder.create()
                .url(String.join(SLASH, remoteFolder, RELEASE))
                .localFolder(folder.getAbsolutePath())
                .get().run();

        DownloadBuilder.create()
                .url(String.join(SLASH, remoteFolder, RELEASE_GPG))
                .localFolder(folder.getAbsolutePath())
                .get().run();

        for (String component : source.getComponentsList()) {
            repository.getArchitectures()
                    .forEach(arch -> download(remoteFolder, folder, component, arch, sf));
        }

        return sf;
    }

    private static void download(String remoteFolder, File localFolder, String component, Architecture arch, SourceFiles sf) {
        String remote = String.join(SLASH, remoteFolder, component, arch.getFolder());
        File local = new File(String.join(SLASH, localFolder.getAbsolutePath(), component, arch.getFolder()));
        local.mkdirs();

        DownloadBuilder.create()
                .url(String.join(SLASH, remote, RELEASE))
                .localFolder(local.getAbsolutePath())
                .get().run();

        DownloadBuilder.create()
                .url(String.join(SLASH, remote, PACKAGES_GZ))
                .localFolder(local.getAbsolutePath())
                .get().run();

        DownloadBuilder.create()
                .url(String.join(SLASH, remote, PACKAGES_BZ2))
                .localFolder(local.getAbsolutePath())
                .get().run();

        try {
            String pkgFile = String.join(SLASH, local.getAbsolutePath(), PACKAGES);
            File file = new File(pkgFile);
            InputStream inputStream = new FileInputStream(String.join(SLASH, local.getAbsolutePath(), PACKAGES_BZ2));
            IOUtils.copy(new BZip2InputStream(inputStream, false), new FileOutputStream(file));
            sf.add(pkgFile);
        } catch (IOException ex) {
            throw new DownloadException(ex.getMessage(), ex);
        }
    }

    static class SourceFiles {

        private final String url;

        private final List<String> files;

        SourceFiles(String url) {
            this.url = url;
            files = new ArrayList<>();
        }

        private void add(String file) {
            files.add(file);
        }

        Stream<String> stream() {
            return files.stream();
        }

        String getUrl() {
            return url;
        }

        String getFolderName() {
            String[] arr = url.split(SLASH);
            return arr[arr.length - 1];
        }
    }
}