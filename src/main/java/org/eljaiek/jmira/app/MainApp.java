package org.eljaiek.jmira.app;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eljaiek.jmira.app.events.CloseRequestHandler;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.Views;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class MainApp extends Application {
    
    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    private static ViewLoader VIEW_LOADER;

    private static CloseRequestHandler CLOSE_HANDLER;

    private static Environment ENV; 

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> LOG.error(e.getMessage(), e));
        
        Locale.setDefault(Locale.ENGLISH);
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        VIEW_LOADER = context.getBean(ViewLoader.class);
        CLOSE_HANDLER = context.getBean(CloseRequestHandler.class);
        ENV = context.getBean(Environment.class);
        launch(args);        
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = (Parent) VIEW_LOADER.load(Views.HOME);
        Scene scene = new Scene(root);
        stage.getIcons().add(Views.APP_ICON);
        stage.setTitle(String.join(" ", ENV.getProperty("app.title"), ENV.getProperty("app.version")));
        stage.setScene(scene);
        stage.setOnCloseRequest(evt -> CLOSE_HANDLER.onClose(stage));
        stage.show();       
    }
}
