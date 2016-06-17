package org.eljaiek.jmira.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eljaiek.jmira.core.logs.MessageResolver;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.core.model.Repository;
import org.eljaiek.jmira.core.io.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.LongConsumer;
import org.eljaiek.jmira.core.AccessFailedException;
import org.eljaiek.jmira.core.io.DownloadBuilderFactory;
import org.eljaiek.jmira.core.model.Settings;

import static org.eljaiek.jmira.core.util.NamesUtils.SETTINGS_JSON;
import org.eljaiek.jmira.core.scanner.PackageList;
import org.eljaiek.jmira.core.scanner.PackageScanner;
import org.eljaiek.jmira.core.scanner.ScannerConfiguration;
import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author eljaiek
 */
@Service
public final class RepositoryServiceImpl implements RepositoryService {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    private static final String SLASH = "/";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageResolver messages;

    @Autowired
    private PackageRepository packages;

    @Autowired
    private PackageScanner scanner;

    private final SourcesDownloadManager sourcesManager;

    @Autowired
    public RepositoryServiceImpl(DownloadBuilderFactory factory) {
        sourcesManager = new SourcesDownloadManager(factory);
    }

    @Override
    public final void save(Repository repository) throws AccessFailedException {
        Assert.notNull(repository);

        try {
            File home = new File(String.join(SLASH, repository.getHome(), SETTINGS_JSON));

            if (!home.exists()) {
                home.createNewFile();
            }

            objectMapper.writeValue(home, repository);
        } catch (IOException ex) {
            throw new AccessFailedException(ex.getMessage(), ex);
        }
    }

    @Override
    public final Repository open(String home) throws AccessFailedException {

        try {
            File homeFile = new File(String.join(SLASH, home, SETTINGS_JSON));
            Assert.isTrue(homeFile.exists(), messages.getMessage("repository.homeError"));
            Repository repository = objectMapper.readValue(homeFile, Repository.class);

            if (!home.equals(repository.getHome())) {
                repository.setHome(home);
            }

            return repository;
        } catch (IOException ex) {
            throw new AccessFailedException(ex.getMessage(), ex);
        }
    }

    @Override
    public final Status synchronize(Repository repository, LongConsumer progress) {
        final Status status = new Status();
        Optional<LongConsumer> consumer = Optional.ofNullable(progress);
        Progress.reset(repository.getSources().size());
        List<SourcesDownloadManager.SourceFiles> sfs = sourcesManager.download(repository);
        packages.removeAll();

        sfs.forEach(sf -> sf.stream().forEach(Unchecked.consumer(packagesFile -> {
            String localHome = String.join("/", repository.getHome(), sf.getFolderName());
            ScannerConfiguration config = new ScannerConfiguration(packagesFile, false, localHome, sf.getRemoteHome());
            PackageList packageList = scanner.scan(config);
            packages.saveAll(packageList.getPackages());
            status.addAvailable(packageList.getAvailable());
            status.addAvailableSize(packageList.getAvailableSize());
            status.addDownloadsSize(packageList.getDownloadsSize());
            status.addDownloads(packageList.getDownloads());
            consumer.orElse((long value) -> LOG.debug(String.valueOf(value))).accept(Progress.update());
        }, error -> {
           LOG.error(error.getMessage(), error); 
           throw new org.eljaiek.jmira.core.SyncFailedException(error.getMessage(), error);
        })));

        return status;
    }

    @Override
    public Status refresh(Repository repository) {
        Status status = new Status();
        Settings prefs = repository.getSettings();
        status.addAvailable(packages.count());
        status.addAvailableSize(packages.size());
        status.addDownloadsSize(packages.downloadsSize(prefs.isChecksum()));
        status.addDownloads(packages.downloads(prefs.isChecksum()));
        return status;
    }

    private static final class Progress {

        private static int total;

        private static int current;

        private Progress() {
            throw new AssertionError();
        }

        private static int update() {
            return (++current * 100) / total;
        }

        private static void reset(int total) {
            Progress.total = total;
            Progress.current = 0;
        }
    }
}
