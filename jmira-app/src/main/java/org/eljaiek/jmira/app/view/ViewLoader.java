package org.eljaiek.jmira.app.view;

import org.eljaiek.jmira.core.logs.MessageResolver;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.lang.reflect.InvocationTargetException;
import javafx.fxml.FXMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author eduardo.eljaiek
 */
public final class ViewLoader implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ViewLoader.class);

    private ApplicationContext context;

    private final Map<String, String> resources;

    @Autowired
    private MessageResolver messages;

    public ViewLoader(Map<String, String> resources) {
        this.resources = resources;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) {
        context = ac;
    }

    public final Object load(String url) {
        return load(url, Optional.ofNullable(null));
    }

    public final Object load(String url, Optional<Map<String, Object>> bindings) {

        try {
            ResourceBundle resourceBundle = loadResourceBundle(url).get();
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(url), resourceBundle);
            loader.setControllerFactory(context::getBean);
            Object view = loader.load();

            if (bindings.isPresent()) {
                bindModel(bindings.get(), loader.getController());
            }

            return view;
        } catch (IOException ex) {
            throw new ViewLoadException(ex.getMessage(), ex);
        }
    }

    private Optional<ResourceBundle> loadResourceBundle(String url) {
        ResourceBundle resourceBundle = null;

        try {
            resourceBundle = ResourceBundle.getBundle(resources.get(url));
        } catch (NullPointerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return Optional.ofNullable(resourceBundle);
    }

    private <T> void bindModel(Map<String, Object> bindings, T controller) {

        bindings.forEach((String key, Object value) -> {
            boolean bonded;
            Optional<Field> field = findField(controller.getClass(), key);

            if (field.isPresent()) {
                bindToField(field.get(), controller, value);
                bonded = true;
            } else {
                bonded = bindToMethod(key, controller, value);
            }

            if (!bonded) {
                LOG.warn(messages.getMessage("viewLoader.bindField.warn",
                        controller.getClass().getCanonicalName(), key));
            }
        });
    }

    private Optional<Field> findField(Class clazz, String key) {
        return Arrays.asList(clazz.getDeclaredFields())
                .stream()
                .filter(f -> {
                    ViewModel vb = f.getAnnotation(ViewModel.class);
                    return vb != null && vb.value().equals(key);
                })
                .findFirst();
    }

    private static <T> void bindToField(Field field, T controller, Object value) {

        try {
            field.setAccessible(true);
            field.set(controller, value);
            field.setAccessible(false);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ViewLoadException(ex.getMessage(), ex);
        }
    }

    private static <T> boolean bindToMethod(String key, T controller, Object value) {
        boolean bonded = false;
        Optional<Method> method = Arrays.asList(controller.getClass().getMethods())
                .stream()
                .filter(m -> {
                    ViewModel vb = m.getAnnotation(ViewModel.class);
                    return vb != null && vb.value().equals(key);
                })
                .findFirst();

        if (method.isPresent()) {
            try {
                method.get().invoke(controller, value);
            } catch (IndexOutOfBoundsException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new ViewLoadException(ex.getMessage(), ex);
            }

            bonded = true;
        }

        return bonded;
    }
}
