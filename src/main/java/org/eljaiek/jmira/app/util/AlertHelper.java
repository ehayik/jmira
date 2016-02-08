package org.eljaiek.jmira.app.util;

import java.util.Optional;
import javafx.concurrent.Service;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.dialog.ProgressDialog;

/**
 *
 * @author eduardo.eljaiek
 */
public final class AlertHelper {

    private AlertHelper() {
    }

    public static final void info(Window owner, String header, String message) {
        info(owner, header, message, false);
    }

    public static final void info(Window owner, String header, String message, boolean alert) {

        if (alert) {
            String path = AlertHelper.class.getResource("/org/eljaiek/jmira/app/view/resources/audio/info_alert.mp3").toString();
            Media media = new Media(path);
            MediaPlayer mp = new MediaPlayer(media);
            mp.play();
        }

        Alert alertDialog = create(AlertType.INFORMATION, owner, header, message);
        alertDialog.showAndWait();
    }

    public static final void error(Window owner, String header, String message) {
        Alert alert = create(AlertType.ERROR, owner, header, message);
        alert.showAndWait();
    }

    public static final void error(Window owner, String header, String message, Throwable error) {
        ExceptionDialog alert = new ExceptionDialog(error);
        alert.setTitle("");
        alert.initOwner(owner);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static final void progress(String header, String message, Service service) {
        ProgressDialog pd = new ProgressDialog(service);
        pd.setTitle("");
        pd.setHeaderText(header);
        pd.setContentText(message);
        pd.getDialogPane().setPrefHeight(240);
        service.start();
    }

    public static final Optional<ButtonType> confirmation(Window owner, String header, String message) {
        Alert alert = create(AlertType.CONFIRMATION, owner, header, message);
        return alert.showAndWait();
    }

    private static Alert create(AlertType type, Window owner, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("");
        alert.initOwner(owner);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert;
    }
}
