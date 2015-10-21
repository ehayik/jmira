
package org.eljaiek.jmira.app.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.eljaiek.jmira.app.model.SourceModel;
import org.eljaiek.jmira.app.util.ValidationHelper;
import org.eljaiek.jmira.app.view.ViewModel;
import org.eljaiek.jmira.core.MessageResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Controller
public class EditSourceController implements Initializable {

    private Optional<SourceModel> model = Optional.ofNullable(null);

    private final BooleanProperty disabled = new SimpleBooleanProperty();

    private final ValidationSupport validationSupport = new ValidationSupport();

    @Autowired
    private MessageResolver messages;

    @FXML
    private TextField uriTextField;

    @FXML
    private TextField distsTextField;

    @FXML
    private TextField compTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validationSupport.registerValidator(uriTextField, true, (Control t, String value) -> {
            boolean isValid = ValidationHelper.isValidUrl(value);
            return ValidationResult.fromMessageIf(t, messages.getMessage("urlTextField.error"), Severity.ERROR, !isValid);
        });
        validationSupport.registerValidator(distsTextField, true, Validator.createEmptyValidator(messages.getMessage("field.required")));
        validationSupport.registerValidator(compTextField, true, Validator.createEmptyValidator(messages.getMessage("field.required")));
        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            disabled.set(newValue);
        });
    }

    @ViewModel("model")
    public void setModel(SourceModel model) {
        this.model = Optional.of(model);
        uriTextField.setText(model.getUri());
        distsTextField.setText(model.getDistribution());
        compTextField.setText(model.getComponents());
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
    void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void accept(ActionEvent event) {
        model.get().setUri(uriTextField.getText());
        model.get().setDistribution(distsTextField.getText());
        model.get().setComponents(compTextField.getText());
        cancel(event);
    }
}
