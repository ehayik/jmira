package org.eljaiek.jmira.app.controller.util;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.eljaiek.jmira.app.model.RepositoryModel;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.data.model.Repository;


/**
 *
 * @author eduardo.eljaiek
 */
public final class SynchronizeService extends Service {

    private final RepositoryModel model;

    private final RepositoryService repositories;

    public SynchronizeService(RepositoryModel repository, RepositoryService repositoryService/*, PackageService packageService*/) {
        model = repository;
        repositories = repositoryService;
    }  

    @Override
    protected final Task createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                Repository repository = model.getRepository();
                repositories.synchronize(repository, (long value1) -> updateProgress(value1 / 0.01, 1));
                model.setPackagesCount(repository.getPackagesCount());
                model.setDownloadedCount(repository.getDownloadedCount());
                model.setSize(repository.getSize());
                model.setDownloaded(repository.getDownloadedSize());
                return null;
            }
        };
    }
}
