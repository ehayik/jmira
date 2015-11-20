package org.eljaiek.jmira.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.function.LongConsumer;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.core.PackageScanner;
import org.eljaiek.jmira.core.MessageResolver;
import static org.eljaiek.jmira.core.NamesUtils.SETTINGS_JSON;
import org.eljaiek.jmira.core.RepositoryAccessException;
import org.eljaiek.jmira.data.model.Repository;
import org.eljaiek.jmira.data.repositories.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author eduardo.eljaiek
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {

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
    public final void open(Repository reposiory) throws RepositoryAccessException {

        try {
            File home = new File(String.join(SLASH, reposiory.getHome(), SETTINGS_JSON));

            if (!home.exists()) {
                home.createNewFile();
            }

            objectMapper.writeValue(home, reposiory);
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
    public final long synchronize(Repository repository, LongConsumer progress) {
        Optional<LongConsumer> consumer = Optional.ofNullable(progress);
        Progress.reset(repository.getSources().size());
        List<SourcesHelper.SourceFiles> sfs = SourcesHelper.download(repository);
        packages.removeAll();

        sfs.forEach(sf -> {
            sf.stream().forEach(f -> {
                String locaHome = String.join("/", repository.getHome(), sf.getFolderName());

                try (PackageScanner scanner = new PackageScanner(f, locaHome, sf.getUrl())) {
                    packages.saveAll(scanner.list());
                    Progress.downloaded += scanner.getDownloaded();
                    consumer.orElse(DEF_CONSUMER).accept(Progress.update());
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });

        return Progress.downloaded;
    }

    private static final class Progress {

        private static int total;

        private static int current;

        private static long downloaded;

        private static int update() {
            return (++current * 100) / total;
        }

        private static void reset(int total) {
            Progress.total = total;
            Progress.current = 0;
            Progress.downloaded = 0;
        }
    }
}
