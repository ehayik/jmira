package org.eljaiek.jmira.app.controller;

import org.eljaiek.jmira.app.model.RepositoryModel;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.eljaiek.jmira.app.view.ViewModel;
import static org.eljaiek.jmira.core.NamesUtils.SETTINGS_JSON;
import org.eljaiek.jmira.data.model.Architecture;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Controller
public class EditRepositoryController implements Initializable {
    
    @FXML
    private CheckComboBox<Architecture> archsComboBox;
    
    @FXML
    private TextField nameTextField;
    
    @FXML
    private TextField homeTextField;
    
    @FXML
    private Button okButton;
    
    private RepositoryModel repository;
    
    @ViewModel("acceptAction")
    private Function<RepositoryModel, Void> acccept;
    
    private final ValidationSupport validationSupport = new ValidationSupport();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        archsComboBox.getItems().addAll(Architecture.values());       
        validationSupport.registerValidator(nameTextField, true, Validator.createEmptyValidator(rb.getString("nameTextField.error")));
        
        validationSupport.registerValidator(homeTextField, true, (Control t, String value) -> {
            File f = new File(String.join("/", value, SETTINGS_JSON));
            return ValidationResult.fromMessageIf(t, rb.getString("homeTextField.error"), Severity.ERROR, f.exists());
        });
        
        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue);
        });
        
        archsComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Architecture>) c -> {
            if (c.getList().size() == 0) {
                archsComboBox.getCheckModel().check(0);
            }
            
            if (repository != null) {
               repository.setArchictectures(archsComboBox.getCheckModel().getCheckedItems());
            }
        });
    }
    
    @ViewModel("repository")
    public void setRepository(RepositoryModel repo) {
        repository = repo;
        nameTextField.textProperty().bindBidirectional(repository.nameProperty());
        homeTextField.textProperty().bindBidirectional(repository.homeProperty());  
        repository.getArchictectures().forEach(archsComboBox.getCheckModel()::check);
    }
    
    @FXML
    void browseDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(((Node) event.getTarget()).getParent().getScene().getWindow());
        
        if (dir != null) {
            homeTextField.setText(dir.getAbsolutePath());
        }
    }
    
    @FXML
    void acceptAction(ActionEvent event) {
        
        if (acccept != null) {
            acccept.apply(repository);
        }
        
        Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void closeAction(ActionEvent event) {       
        Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
        stage.close();
    }
}
