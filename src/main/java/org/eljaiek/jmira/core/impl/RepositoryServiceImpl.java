package org.eljaiek.jmira.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eljaiek.jmira.core.MessageResolver;
import org.eljaiek.jmira.core.PackageScanner;
import org.eljaiek.jmira.core.RepositoryAccessException;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.data.model.Repository;
import org.eljaiek.jmira.data.repositories.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.LongConsumer;

import static org.eljaiek.jmira.core.NamesUtils.SETTINGS_JSON;

/**
 * @author eduardo.eljaiek
 */
@Service
public final class RepositoryServiceImpl implements RepositoryService {

    private static final LongConsumer DEF_CONSUMER = (long value) -> {
    };

    private static final String SLASH = "/";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageResolver messages;

    @Autowired
    private PackageRepository packages;

    @Override
    public final void save(Repository repository) throws RepositoryAccessException {
        Assert.notNull(repository);

        try {
            File home = new File(String.join(SLASH, repository.getHome(), SETTINGS_JSON));

            if (!home.exists()) {
                home.createNewFile();
            }

            objectMapper.writeValue(home, repository);
        } catch (IOException ex) {
            throw new RepositoryAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public final Repository open(String home) throws RepositoryAccessException {

        try {
            File homeFile = new File(String.join(SLASH, home, SETTINGS_JSON));
            Assert.isTrue(homeFile.exists(), messages.getMessage("repository.homeError"));
            return objectMapper.readValue(homeFile, Repository.class);
        } catch (IOException ex) {
            throw new RepositoryAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public final void synchronize(Repository repository, LongConsumer progress) {
        Optional<LongConsumer> consumer = Optional.ofNullable(progress);
        Progress.reset(repository.getSources().size());
        List<SourcesHelper.SourceFiles> sfs = SourcesHelper.download(repository);
        repository.setDownloadedCount(0);
        repository.setDownloadedSize(0);
        repository.setSize(0);
        repository.setPackagesCount(0);
        packages.removeAll();

        sfs.forEach(sf -> sf.stream().forEach(f -> {
            String localHome = String.join("/", repository.getHome(), sf.getFolderName());

            try (PackageScanner scanner = new PackageScanner(f, localHome, sf.getUrl())) {
                PackageScanner.PackageList packageList = scanner.list();
                packages.saveAll(packageList.getPackages());
                repository.setPackagesCount(repository.getPackagesCount() + packageList.getCount());
                repository.setSize(repository.getSize() + packageList.getSize());
                repository.setDownloadedSize(repository.getDownloadedSize() + packageList.getDownloaded());
                repository.setDownloadedCount(repository.getDownloadedCount() + packageList.getDownloadedCount());
                consumer.orElse(DEF_CONSUMER).accept(Progress.update());
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }));
    }

    private static final class Progress {

        private static int total;

        private static int current;

        private static int update() {
            return (++current * 100) / total;
        }

        private static void reset(int total) {
            Progress.total = total;
            Progress.current = 0;
        }
    }
}
