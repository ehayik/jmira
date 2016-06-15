package org.eljaiek.jmira.core.io.impl;

import org.eljaiek.jmira.core.io.DownloadBuilder;
import org.eljaiek.jmira.core.io.DownloadBuilderFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
final class DownloadBuilderFactoryImpl implements DownloadBuilderFactory, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public DownloadBuilder create() {
        return context.getBean(DownloadBuilder.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
