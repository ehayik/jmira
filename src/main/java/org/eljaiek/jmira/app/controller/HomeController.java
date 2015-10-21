package org.eljaiek.jmira.app.controller;

import java.io.File;

import org.eljaiek.jmira.app.model.RepositoryModel;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.eljaiek.jmira.app.util.AlertHelper;
import org.eljaiek.jmira.app.util.ModelMapperHelper;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.ViewMode;
import org.eljaiek.jmira.app.view.Views;
import org.eljaiek.jmira.core.MessageResolver;
import org.eljaiek.jmira.core.NamesUtils;
import org.eljaiek.jmira.core.RepositoryAccessException;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.data.model.Repository;
import org.eljaiek.jmira.data.repositories.PackagesFileProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Controller
public class HomeController implements Initializable, PackagesFileProvider, RepositoryProvider {

    private static final String TITLE_TMPL = "JMira 1.0 - %s";
    
    @Autowired
    private ViewLoader viewLoader;

    @Autowired
    private MessageResolver messages;

    @Autowired
    private RepositoryService repositories;

    @Autowired
    private ServicePool servicePool;

    private RepositoryModel current;

    private final BooleanProperty disabled = new SimpleBooleanProperty();

    private final Function<RepositoryModel, Void> open;

    public HomeController() {
        open = (RepositoryModel t) -> {
            open(t);
            return null;
        };
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        disabled.set(true);
    }

    public BooleanProperty disabledProperty() {
        return disabled;
    }

    public boolean isDisabled() {
        return disabled.get();
    }

    public void setDisabled(boolean disabled) {
        this.disabled.set(disabled);
    }

    @FXML
    final void newRepository(ActionEvent event) {
        RepositoryModel model = new RepositoryModel();
        Window window = ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        showRepositoryView(messages.getMessage("repository.newDialog.title"), window, model, ViewMode.CREATE);
        ((Stage) window).setTitle(String.format(TITLE_TMPL, model.getName()));
    }

    @FXML
    final void openRepository(ActionEvent event) {
        Window window = ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(window);

        if (dir != null) {
            try {
                Repository repo = repositories.open(dir.getAbsolutePath());
                current = ModelMapperHelper.map(repo);
                ((Stage) window).setTitle(String.format(TITLE_TMPL, repo.getName()));
                disabled.set(false);
            } catch (IllegalArgumentException | RepositoryAccessException ex) {
                AlertHelper.error(window, messages.getMessage("repository.openError"), ex.getMessage(), ex);
            }
        }
    }

    @FXML
    final void editRepository(ActionEvent event) {
        Window window = ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        showRepositoryView(current.getName(), window, new RepositoryModel(current), ViewMode.EDIT);
        ((Stage) window).setTitle(String.format(TITLE_TMPL, current.getName()));
    }

    @FXML
    final void syncronize(ActionEvent event) {
        AlertHelper.progress(messages.getMessage("repository.sync.progressHeader"), messages.getMessage("repository.sync.progressContext", current.getName()), servicePool.getService("syncronizeService"));
    }

    private void showRepositoryView(String title, Window owner, RepositoryModel model, ViewMode mode) {
        Map<String, Object> bindings = new HashMap<>(1);
        bindings.put("model", model);
        bindings.put("acceptAction", open);
        bindings.put("viewMode", mode);
        Parent parent = (Parent) viewLoader.load(Views.EDIT_REPOSITORY, bindings);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.setWidth(489);
        stage.setHeight(272);
        stage.showAndWait();
    }

    private void open(RepositoryModel model) {
        try {
            Repository repo = ModelMapperHelper.map(model);
            repositories.open(repo);
            current = model;
            disabled.set(false);
        } catch (RepositoryAccessException ex) {
            AlertHelper.error(null, messages.getMessage("repository.createError"), ex.getMessage(), ex);
        }
    }

    @Override
    public final File getFile() {
        return new File(String.join("/", current.getHome(), NamesUtils.PACKAGES_DAT));
    }

    @Override
    public final Repository getRepository() {
        return ModelMapperHelper.map(current);
    }
}
