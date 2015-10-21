package org.eljaiek.jmira.app.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.eljaiek.jmira.app.util.AlertHelper;
import org.eljaiek.jmira.core.MessageResolver;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.data.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author eduardo.eljaiek
 */
@Component("syncronizeService")
@Scope("prototype")
final class SyncronizeService extends Service {

    private static final Logger LOG = LoggerFactory.getLogger(SyncronizeService.class);
    
    @Autowired
    private RepositoryService repositories;
    
    @Autowired
    private MessageResolver messages;

    @Autowired
    private RepositoryProvider provider;

    public SyncronizeService() {
        setOnFailed(evt -> {
            Repository repo = provider.getRepository();  
            String error = messages.getMessage("repository.sync.errorContext",repo.getName());                           
            LOG.error(error, getException());
            AlertHelper.error(null
                    , messages.getMessage("repository.sync.errorHeader")
                    , null
                    , new RuntimeException(error, getException()));
        });
    }  

    @Override
    protected final Task createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                repositories.synchronize(provider.getRepository(), (long value1) -> {
                    updateProgress(value1, 100);
                });

                return null;
            }
        };
    }
}
