package org.eljaiek.jmira.app.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Lazy
@Controller
public class AboutBoxController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(AboutBoxController.class);

    @FXML
    private Label nameLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Tooltip websiteTooltip;

    @FXML
    private Tooltip feedbackTooltip;

    @Autowired
    private Environment env;

    public AboutBoxController() {
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameLabel.setText(env.getProperty("app.title"));
        versionLabel.setText(env.getProperty("app.version"));
        websiteTooltip.setText(env.getProperty("app.website"));
        feedbackTooltip.setText(env.getProperty("app.vendor.email"));
    }

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void gotoWebsite(ActionEvent event) {
        new GotoWebsiteService().start();
    }

    @FXML
    void feedback(ActionEvent event) {
        new FeedbackService().start();
    }
    
    public class FeedbackService extends Service {

        @Override
        protected Task createTask() {
            return new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.mail(new URI(String.join(":", "mailto", env.getProperty("app.vendor.email"))));
                    } catch (IOException | URISyntaxException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                    return null;
                }
            };
        }
    }

    public class GotoWebsiteService extends Service {

        @Override
        protected final Task createTask() {
            return new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.browse(new URI(env.getProperty("app.website")));
                    } catch (IOException | URISyntaxException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                    return null;
                }
            };
        }
    }
}
