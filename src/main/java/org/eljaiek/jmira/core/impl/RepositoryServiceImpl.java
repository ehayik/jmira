package org.eljaiek.jmira.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.core.DownloadException;
import org.eljaiek.jmira.core.PackageScanner;
import static org.eljaiek.jmira.core.DebianNamesUtils.DISTS_FOLDER;
import static org.eljaiek.jmira.core.DebianNamesUtils.PACKAGES;
import static org.eljaiek.jmira.core.DebianNamesUtils.PACKAGES_BZ2;
import static org.eljaiek.jmira.core.DebianNamesUtils.PACKAGES_GZ;
import static org.eljaiek.jmira.core.DebianNamesUtils.RELEASE;
import static org.eljaiek.jmira.core.DebianNamesUtils.RELEASE_GPG;
import org.eljaiek.jmira.core.MessageResolver;
import static org.eljaiek.jmira.core.NamesUtils.SETTINGS_JSON;
import org.eljaiek.jmira.core.RepositoryAccessException;
import org.eljaiek.jmira.data.model.Architecture;
import org.eljaiek.jmira.data.model.Repository;
import org.eljaiek.jmira.data.model.Source;
import org.eljaiek.jmira.data.repositories.PackageRepository;
import org.itadaki.bzip2.BZip2InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 *
 * @author eduardo.eljaiek
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {

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
    public final void synchronize(Repository repository, Function<Integer, Void> progress) {
        Assert.notNull(progress);
        Progress.total = repository.getSources().size();
        Progress.current = 0;
        repository.getSources().forEach(src -> {

            if (src.isEnabled()) {
                syncronizeSrc(repository, src);
                progress.apply(Progress.update());
            }
        });
    }

    private void syncronizeSrc(Repository repository, Source source) {
        String[] arr = source.getUri().split(SLASH);
        String remoteFolder = String.join(SLASH, source.getUri(), DISTS_FOLDER, source.getDistribution());
        File folder = new File(String.join(SLASH, repository.getHome(), arr[arr.length - 1], DISTS_FOLDER));
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
                    .forEach(arch -> syncronizeComponent(remoteFolder, folder, component, arch));
        }
    }

    private void syncronizeComponent(String remoteFolder, File folder, String component, Architecture arch) {
        String remote = String.join(SLASH, remoteFolder, component, arch.getFolder());
        File local = new File(String.join(SLASH, folder.getAbsolutePath(), component, arch.getFolder()));
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

            try (PackageScanner scanner = new PackageScanner(pkgFile)) {
                packages.saveAll(scanner.list());
            }

        } catch (IOException ex) {
            throw new DownloadException(ex.getMessage(), ex);
        }
    }

    private static final class Progress {

        private static int total;

        private static int current;

        private static int update() {
            return (total / ++current) * 100;
        }
    }
}
