package org.eljaiek.jmira.app.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.*;
import org.apache.commons.lang.StringUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.eljaiek.jmira.app.util.AlertHelper;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.ViewMode;
import org.eljaiek.jmira.app.view.ViewModel;
import org.eljaiek.jmira.app.view.Views;
import org.eljaiek.jmira.core.MessageResolver;
import org.eljaiek.jmira.data.model.Architecture;
import org.eljaiek.jmira.data.model.Source;
import org.eljaiek.jmira.data.model.SourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

import static org.eljaiek.jmira.core.NamesUtils.SETTINGS_JSON;
import org.springframework.context.annotation.Lazy;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Lazy
@Controller
public class EditRepositoryController implements Initializable {

    @Autowired
    private ViewLoader viewLoader;

    @Autowired
    private MessageResolver messages;

    @FXML
    private CheckComboBox<Architecture> archsComboBox;

    @FXML
    private ListView<SourceModel> sourcesListView;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField homeTextField;
    
    @ViewModel("viewMode")
    private ViewMode viewMode = ViewMode.CREATE;

    private final BooleanProperty disabled = new SimpleBooleanProperty();

    private final BooleanProperty noSelection = new SimpleBooleanProperty();

    private Optional<RepositoryModel> model = Optional.ofNullable(null);

    @ViewModel("acceptAction")
    private Function<RepositoryModel, Void> accept;

    private final ValidationSupport validationSupport = new ValidationSupport();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noSelection.set(true);
        archsComboBox.getItems().addAll(Architecture.values());
        validationSupport.registerValidator(nameTextField, true, Validator.createEmptyValidator(messages.getMessage("field.required")));
        validationSupport.registerValidator(homeTextField, true, (Control t, String value) -> {
            File f = new File(String.join("/", value, SETTINGS_JSON));
            boolean condition = f.exists();
            
            if (ViewMode.EDIT == viewMode) {
               String home = String.join("/", model.get().getHome(), SETTINGS_JSON);

               try {
                  condition = f.exists() && !Files.isSameFile(f.toPath(), new File(home).toPath());
                } catch (IOException ex) {  
                  condition = false;  
                }              
            }           
           
            return ValidationResult.fromMessageIf(t, messages.getMessage("homeTextField.error"), Severity.ERROR, condition);
        });

        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            disabled.set(newValue);
        });

        archsComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<Architecture>) c -> {
            if (c.getList().size() == 0) {
                archsComboBox.getCheckModel().check(0);
            }

            if (model.isPresent()) {
                model.get().setArchitectures(archsComboBox.getCheckModel().getCheckedItems());
            }
        });

        sourcesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            noSelection.set(newValue == null);
        });        
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

    public boolean getNoSelection() {
        return noSelection.get();
    }

    public BooleanProperty noSelectionProperty() {
        return noSelection;
    }

    public void setNoSelection(boolean noSelection) {
        this.noSelection.set(noSelection);
    }  

    @ViewModel("model")   
    public void setRepository(RepositoryModel repo) {
        model = Optional.of(repo);
        nameTextField.textProperty().bindBidirectional(repo.nameProperty());
        homeTextField.setText(repo.getHome());
        repo.getArchitectures().forEach(archsComboBox.getCheckModel()::check);
        sourcesListView.itemsProperty().bindBidirectional(repo.sourcesProperty());        
    }

    @FXML
    void newAction(ActionEvent event) {
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        Optional<String> aptline = sourceDialog(window).showAndWait();

        if (aptline.isPresent() && StringUtils.isNotEmpty(aptline.get())) {
            try {
                Source source = SourceBuilder.create()
                        .enabled(true)
                        .aptLine(aptline.get())
                        .get();
                SourceModel sourceModel = SourceModel.create(source);
                sourcesListView.getItems().add(sourceModel);
            } catch (IllegalArgumentException ex) {
                AlertHelper.error(window, messages.getMessage("newSourceDialog.error"), ex.getMessage());
            }
        }
    }

    @FXML
    void deleteAction(ActionEvent event) {
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        Optional<ButtonType> result = AlertHelper.confirmation(
                window,
                messages.getMessage("removeSourceDialog.header"), messages.getMessage("removeSourceDialog.contextText"));

        if (result.isPresent() && result.get() == ButtonType.OK) {
            SourceModel src = sourcesListView.getSelectionModel().getSelectedItem();
            model.get().getSources().remove(src);
        }
    }

    @FXML
    void editAction(ActionEvent event) {
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        Map<String, Object> bindings = new HashMap<>(1);
        bindings.put("model", sourcesListView.getSelectionModel().getSelectedItem());
        Parent parent = (Parent) viewLoader.load(Views.EDIT_SOURCE, Optional.of(bindings));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.getIcons().add(Views.APP_ICON);
        stage.setTitle(messages.getMessage("editSourceDialog.title"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(window);
        stage.setScene(scene);
        stage.showAndWait();
        ObservableList<SourceModel> sources = model.get().getSources();
        sourcesListView.setItems(null);
        sourcesListView.setItems(sources);
    }
    
    @FXML
    void editOnClick(MouseEvent event) {
        
        if (event.getClickCount() == 2 && sourcesListView.getSelectionModel().getSelectedItem() != null) {
           editAction(new ActionEvent(null, event.getTarget()));
        }
    }

    private Dialog<String> sourceDialog(Window window) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("");
        dialog.initOwner(window);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setHeaderText(messages.getMessage("newSourceDialog.header"));

        Label contextText = new Label(messages.getMessage("newSourceDialog.contextText"));
        contextText.setPrefWidth(400);
        contextText.setWrapText(true);
        Label label = new Label(messages.getMessage("newSourceDialog.label"));
        TextField textField = new TextField();
        GridPane.setVgrow(textField, Priority.ALWAYS);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(20);
        gridPane.add(contextText, 2, 1);
        gridPane.add(label, 1, 2);
        gridPane.add(textField, 2, 2);

        dialog.getDialogPane().setContent(gridPane);
        dialog.setResultConverter(btn -> {

            if (ButtonBar.ButtonData.OK_DONE == btn.getButtonData()) {
                return textField.getText();
            }

            return null;
        });

        return dialog;
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
    void accept(ActionEvent event) {
        model.get().setHome(homeTextField.getText());

        if (accept != null) {
            accept.apply(model.get());
        }

        cancel(event);
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
        stage.close();
    }
}
