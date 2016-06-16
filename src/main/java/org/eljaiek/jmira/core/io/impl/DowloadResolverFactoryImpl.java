package org.eljaiek.jmira.core.io.impl;

import org.eljaiek.jmira.core.io.DownloadResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.eljaiek.jmira.core.io.DownloadResolverFactory;
import org.eljaiek.jmira.core.logs.MessageResolver;
import org.springframework.beans.factory.annotation.Autowired;

@Lazy
@Component
final class DowloadResolverFactoryImpl implements DownloadResolverFactory, ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private MessageResolver messages;

    @Override
    public DownloadResolver create(String scheme) {

        try {
            Class type = DownloadResolver.class;
            String beanName = scheme.concat(type.getSimpleName());
            return (DownloadResolver) context.getBean(beanName, type);
        } catch (BeansException ex) {
            throw new IllegalArgumentException(messages.getMessage("dowload.resolver.definitionError", scheme), ex);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }
}
