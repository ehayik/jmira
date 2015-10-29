package org.eljaiek.jmira.app;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.Views;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class MainApp extends Application {
        
    private static ViewLoader VIEW_LOADER;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = (Parent) VIEW_LOADER.load(Views.HOME);
        Scene scene = new Scene(root);
        stage.setTitle("JMira 1.0");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        VIEW_LOADER = context.getBean(ViewLoader.class);
        launch(args);
    }
}
