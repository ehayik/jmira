package org.eljaiek.jmira.app.controls;

import org.eljaiek.jmira.core.logs.LogHandler;
import javafx.application.Platform;
import org.fxmisc.richtext.InlineCssTextArea;

final class LogViewer extends InlineCssTextArea implements LogHandler {

    private static final String ERROR_STYLE = "-fx-fill: red";

    private static final String INFO_STYLE = "-fx-fill: green";
    
    private static final String WARN_STYLE = "-fx-fill: #fdc918";

    @Override
    public void info(String log) {
        Platform.runLater(() -> log(log, INFO_STYLE));
    }

    @Override
    public void error(String log) {
        Platform.runLater(() -> log(log, ERROR_STYLE));
    }

    @Override
    public void warn(String log) {
         Platform.runLater(() -> log(log, WARN_STYLE));
    }

    private void log(String message, String style) {

        Platform.runLater(() -> {
            int from = getText().length();
            replaceText(from, from, message);
            setStyle(from, getText().length(), style);
        });
    }
}
