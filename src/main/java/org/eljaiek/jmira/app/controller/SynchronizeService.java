package org.eljaiek.jmira.app.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.data.model.Repository;


/**
 *
 * @author eduardo.eljaiek
 */
final class SynchronizeService extends Service {

    private final RepositoryModel model;

    private final RepositoryService repositories;

    public SynchronizeService(RepositoryModel repository, RepositoryService repositoryService) {
        model = repository;
        repositories = repositoryService;
    }  

    @Override
    protected final Task createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                Repository repository = model.getRepository();
                RepositoryService.Status status = repositories.synchronize(repository, (long value1) -> updateProgress(value1 / 0.01, 1));
                model.setAvailable(status.getAvailable());
                model.setDownloads(status.getDownloads());
                model.setAvailableSize(status.getAvailableSize());
                model.setDownloadsSize(status.getDownloadsSize());
                return null;
            }
        };
    }
}
