package org.eljaiek.jmira.app;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.HashMap;
import java.util.Map;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.Views;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 *
 * @author eduardo.eljaiek
 */
@Configuration
@ComponentScan({"org.eljaiek.jmira.app",
    "org.eljaiek.jmira.core",
    "org.eljaiek.jmira.data.repositories.impl"})
@PropertySource("classpath:application.properties")
class AppConfig {

    @Bean
    public ViewLoader viewLoader() {
        Map<String, String> resources = new HashMap<>(3);
        resources.put(Views.EDIT_REPOSITORY, "org.eljaiek.jmira.app.view.resources.editRepository");
        resources.put(Views.HOME, "org.eljaiek.jmira.app.view.resources.home");
        resources.put(Views.EDIT_SOURCE, "org.eljaiek.jmira.app.view.resources.editSource");
        resources.put(Views.ABOUT_BOX, "org.eljaiek.jmira.app.view.resources.aboutBox");
        return new ViewLoader(resources);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        om.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return om;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ResourceBundleMessageSource bundleMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("org/eljaiek/jmira/app/view/resources/viewLoader",
                "org/eljaiek/jmira/app/view/resources/home",
                "org/eljaiek/jmira/app/view/resources/editRepository",
                "org/eljaiek/jmira/app/view/resources/editSource",
                "org/eljaiek/jmira/app/view/resources/common",
                "org/eljaiek/jmira/core/impl/resources/core",
                "org/eljaiek/jmira/app/view/resources/downloadScheduler");
        source.setFallbackToSystemLocale(false);
        return source;
    }
}
