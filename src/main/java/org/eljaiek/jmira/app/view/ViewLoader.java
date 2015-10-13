package org.eljaiek.jmira.app.view;

import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXMLLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author eduardo.eljaiek
 */
@Component
public final class ViewLoader implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        CONTEXT = ac;
    }

    public static final Object load(String url) {

        try (InputStream fxmlStream = ViewLoader.class.getResourceAsStream(url)) {
            return loader().load(fxmlStream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static FXMLLoader loader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> CONTEXT.getBean(param));
        return loader;
    }
}
