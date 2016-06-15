package org.eljaiek.jmira.app.events;

import javafx.stage.Window;

/**
 * Created by shidara on 5/02/16.
 */
@FunctionalInterface
public interface CloseRequestHandler {

    void onClose(Window window);
}
