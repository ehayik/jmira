package org.eljaiek.jmira.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import static org.eljaiek.jmira.core.util.DebianNamesUtils.DISTS_FOLDER;
import static org.eljaiek.jmira.core.util.DebianNamesUtils.PACKAGES_BZ2;
import static org.eljaiek.jmira.core.util.DebianNamesUtils.PACKAGES_GZ;
import static org.eljaiek.jmira.core.util.DebianNamesUtils.RELEASE;
import static org.eljaiek.jmira.core.util.DebianNamesUtils.RELEASE_GPG;
import org.eljaiek.jmira.core.io.DownloadBuilderFactory;
import org.eljaiek.jmira.core.io.DownloadFailedException;
import org.eljaiek.jmira.core.model.Architecture;
import org.eljaiek.jmira.core.model.Repository;
import org.eljaiek.jmira.core.model.Source;
import org.itadaki.bzip2.BZip2InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author eduardo.eljaiek
 */
final class SourcesDownloadManager {

    private static final String SLASH = "/";

    private static final Logger LOG = LoggerFactory.getLogger(SourcesDownloadManager.class);

    private ExecutorService pool;

    private final DownloadBuilderFactory factory;

    SourcesDownloadManager(DownloadBuilderFactory factory) {
        this.factory = factory;
    }

    List<SourceFiles> download(Repository repository) {
        List<SourceFiles> result = new ArrayList<>(repository.getSources().size());

        repository.getSources().forEach(src -> {

            if (src.isEnabled()) {
                try {
                    resetThreadsPool();
                    SourceFiles sf = download(repository, src);
                    result.add(sf);
                    pool.shutdown();
                    pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | DownloadFailedException ex) {
                    pool.shutdownNow();
                    LOG.error(ex.getMessage(), ex);
                    throw new DownloadFailedException(ex);
                }
            }
        });

        return result;
    }

    private void resetThreadsPool() {
        if (pool != null) {
            pool.shutdownNow();
        }

        pool = Executors.newWorkStealingPool();
    }

    private SourceFiles download(Repository repository, Source source) {
        final SourceFiles sf = new SourceFiles(source.getUri());
        String[] arr = source.getUri().split(SLASH);
        final String remoteFolder = String.join(SLASH, source.getUri(), DISTS_FOLDER, source.getDistribution());
        final File folder = new File(String.join(SLASH, repository.getHome(), arr[arr.length - 1], DISTS_FOLDER, source.getDistribution()));
        folder.mkdirs();

        pool.submit((Runnable) factory.create()
                .url(String.join(SLASH, remoteFolder, RELEASE))
                .localFolder(folder.getAbsolutePath())
                .get());

        pool.submit((Runnable) factory.create()
                .url(String.join(SLASH, remoteFolder, RELEASE_GPG))
                .localFolder(folder.getAbsolutePath())
                .get());

        for (String component : source.getComponentsList()) {
            repository.getArchitectures()
                    .forEach(arch -> download(remoteFolder, folder, component, arch, sf));
        }

        return sf;
    }

    private void download(String remoteFolder, File localFolder, String component, Architecture arch, SourceFiles sf) {

        String remote = String.join(SLASH, remoteFolder, component, arch.getFolder());
        File local = new File(String.join(SLASH, localFolder.getAbsolutePath(), component, arch.getFolder()));
        local.mkdirs();

        pool.submit((Runnable) factory.create()
                .url(String.join(SLASH, remote, RELEASE))
                .localFolder(local.getAbsolutePath())
                .get());

        pool.submit((Runnable) factory.create()
                .url(String.join(SLASH, remote, PACKAGES_GZ))
                .localFolder(local.getAbsolutePath())
                .get());

        pool.submit((Runnable) () -> {

            try {
                factory.create()
                        .url(String.join(SLASH, remote, PACKAGES_BZ2))
                        .localFolder(local.getAbsolutePath())
                        .get().start();

                File file = Files.createTempFile(UUID.randomUUID().toString(), null).toFile();
                InputStream inputStream = new FileInputStream(String.join(SLASH, local.getAbsolutePath(), PACKAGES_BZ2));
                IOUtils.copy(new BZip2InputStream(inputStream, false), new FileOutputStream(file));
                sf.add(file.getAbsolutePath());
            } catch (IOException ex) {
                throw new DownloadFailedException(ex.getMessage(), ex);
            }
        });
    }

    class SourceFiles {

        private final String url;

        private final Queue<String> files;

        SourceFiles(String url) {
            this.url = url;
            files = new ConcurrentLinkedQueue<>();
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
