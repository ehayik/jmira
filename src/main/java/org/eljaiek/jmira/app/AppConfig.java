package org.eljaiek.jmira.app;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.eljaiek.jmira.app.model.RepositoryModel;
import org.eljaiek.jmira.core.MessageResolver;
import org.eljaiek.jmira.data.model.Repository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 *
 * @author eduardo.eljaiek
 */
@Configuration
@ComponentScan({"org.eljaiek.jmira.app", "org.eljaiek.jmira.core", "org.eljaiek.jmira.data.repositories.impl"})
class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        om.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return om;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        PropertyMap<RepositoryModel, Repository> pm = new PropertyMap<RepositoryModel, Repository>() {
            @Override
            protected void configure() {
                map().setArchitectures(source.getArchitureList());
            }
        };

        mapper.addMappings(pm);
        return mapper;
    }

    @Bean
    public ResourceBundleMessageSource bundleMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("org/eljaiek/jmira/app/view/resources/viewLoader",
                "org/eljaiek/jmira/app/view/resources/home",
                "org/eljaiek/jmira/app/view/resources/editRepository",
                "org/eljaiek/jmira/core/impl/resources/core");
        source.setFallbackToSystemLocale(false);
        return source;
    }

    @Bean
    public MessageResolver messageResolver() {
        return new MessageResolver(bundleMessageSource());
    }
}
