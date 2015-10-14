package org.eljaiek.jmira.app.controller;

import java.io.File;
import org.eljaiek.jmira.app.model.RepositoryModel;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.Views;
import org.eljaiek.jmira.core.NamesUtils;
import org.eljaiek.jmira.core.RepositoryService;
import org.eljaiek.jmira.data.model.Repository;
import org.eljaiek.jmira.data.repositories.PackagesFileProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Controller
public class HomeController implements Initializable, PackagesFileProvider {

    @Autowired
    private ViewLoader viewLoader;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private RepositoryService repositories;

    private Repository current;

    private final Function<RepositoryModel, Void> open = (RepositoryModel t) -> {
        open(t);
        return null;
    };

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    void newRepository(ActionEvent event) {
        RepositoryModel model = new RepositoryModel();
        Map<String, Object> bindings = new HashMap<>(1);
        bindings.put("repository", model);
        bindings.put("acceptAction", open);
        Parent parent = (Parent) viewLoader.load(Views.EDIT_REPOSITORY, bindings);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("New Repository");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(((MenuItem) event.getSource()).getParentPopup().getOwnerWindow());
        stage.setScene(scene);
        stage.setWidth(489);
        stage.setHeight(272);
        stage.showAndWait();

    }

    @FXML
    void openRepository(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(((MenuItem) event.getSource()).getParentPopup().getOwnerWindow());

        if (dir != null) {
            current = repositories.open(dir.getAbsolutePath());
        }
    }

    private void open(RepositoryModel model) {
        current = mapper.map(model, Repository.class);
        repositories.open(current);
    }

    @Override
    public File getFile() {
        return new File(String.join("/", current.getHome(), NamesUtils.SETTINGS_JSON));
    }
}
