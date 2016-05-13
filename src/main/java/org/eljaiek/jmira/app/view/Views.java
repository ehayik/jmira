
package org.eljaiek.jmira.app.view;

import javafx.scene.image.Image;

/**
 *
 * @author eduardo.eljaiek
 */
public final class Views {

    public static final String HOME = "/org/eljaiek/jmira/app/view/Home.fxml";
    
    public static final String EDIT_REPOSITORY = "/org/eljaiek/jmira/app/view/EditRepository.fxml";

    public static final String EDIT_SOURCE = "/org/eljaiek/jmira/app/view/EditSource.fxml";
    
    public static final String ABOUT_BOX = "/org/eljaiek/jmira/app/view/AboutBox.fxml";
    
    public static final Image APP_ICON = new Image(Views.class.getResourceAsStream("resources/icons/jmira64.png"));
    
    private Views() {
    }    
}
