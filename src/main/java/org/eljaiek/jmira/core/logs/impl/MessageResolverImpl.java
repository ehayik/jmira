
package org.eljaiek.jmira.core.logs.impl;

import java.util.Locale;
import org.eljaiek.jmira.core.logs.MessageResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageResolverImpl implements MessageResolver {

    private final ResourceBundleMessageSource messageSource;

    @Autowired
    public MessageResolverImpl(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    @Override
    public String getMessage(String code) {
        return getMessage(code, new Object[]{});
    }
    
}
