package org.eljaiek.jmira.app.controller;

import org.eljaiek.jmira.app.model.RepositoryModel;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.core.SyncFailedException;
import org.eljaiek.jmira.core.model.Repository;


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
                
                try {
                    Repository repository = model.getRepository();
                    RepositoryService.Status status = repositories.synchronize(repository, (long value1) -> updateProgress(value1 / 0.01, 1));
                    model.setAvailable(status.getAvailable());
                    model.setDownloads(status.getDownloads());
                    model.setAvailableSize(status.getAvailableSize());
                    model.setDownloadsSize(status.getDownloadsSize());
                    return null;
                } catch (SyncFailedException ex) {
                    throw  ex;
                }               
            }
        };
    }
}
