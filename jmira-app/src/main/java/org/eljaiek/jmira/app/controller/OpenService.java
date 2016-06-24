package org.eljaiek.jmira.app.controller;

import org.eljaiek.jmira.app.model.RepositoryModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.core.model.Repository;

/**
 * Created by shidara on 11/10/2015.
 */
final class OpenService extends Service<Void> {

    private final String home;

    private final RepositoryService repositories;

    private final ObjectProperty<EventHandler<OpenEvent>> onOpen;

    public OpenService(String home, RepositoryService repositoryService) {
        this.home = home;
        repositories = repositoryService;      
        onOpen = new SimpleObjectProperty<>();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                Repository repository = repositories.open(home);              
                RepositoryModel model = RepositoryModel.getInstance(repository);                   
                onOpen.get().handle(new OpenEvent(EventType.ROOT, model));                
                return null;
            }
        };
    }

    public void setOnOpen(EventHandler<OpenEvent> onOpen) {
        this.onOpen.set(onOpen);
    }
    
    public class OpenEvent extends Event {

        private final RepositoryModel model;
        
        public OpenEvent(EventType<? extends Event> eventType, RepositoryModel model) {
            super(eventType);
            this.model = model;
        }

        public RepositoryModel getModel() {
            return model;
        }     
    }
}
