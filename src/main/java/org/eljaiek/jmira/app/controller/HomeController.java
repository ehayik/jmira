package org.eljaiek.jmira.app.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Controller
public class HomeController implements Initializable {

    @Autowired
    private ViewLoader viewLoader;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    void newRepository(ActionEvent event) {
        Parent parent = (Parent) viewLoader.load(Views.EDIT_REPOSITORY);
         Scene scene = new Scene(parent);
        Stage stage = new Stage();   
        stage.setTitle("New Repository");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(((MenuItem)event.getSource()).getParentPopup().getOwnerWindow());
        stage.setScene(scene);       
        stage.showAndWait(); 
    }
}
