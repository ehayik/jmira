package org.eljaiek.jmira.app.controller.util;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.eljaiek.jmira.app.model.RepositoryModel;
import org.eljaiek.jmira.core.PackageService;
import org.eljaiek.jmira.core.RepositoryService;


/**
 *
 * @author eduardo.eljaiek
 */
public final class SyncronizeService extends Service {

    private final RepositoryModel repository;

    private final RepositoryService repositories;
    
    private final PackageService packages;

    public SyncronizeService(RepositoryModel repository, RepositoryService repositoryService, PackageService packageService) {
        this.repository = repository;
        repositories = repositoryService;
        packages = packageService;
    }  

    @Override
    protected final Task createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                repositories.synchronize(repository.getRepository(), (long value1) -> {
                    updateProgress(value1 / 0.01, 1);                   
                });

                repository.setSize(packages.size());                
                return null;
            }
        };
    }
}
