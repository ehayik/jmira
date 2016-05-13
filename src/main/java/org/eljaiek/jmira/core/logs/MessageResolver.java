package org.eljaiek.jmira.core.logs;

import java.util.Locale;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 *
 * @author eduardo.eljaiek
 */
public final class MessageResolver {

    private final ResourceBundleMessageSource messageSource;

    public MessageResolver(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;

        if (MessageResolverHolder.DEFAULT == null) {
            MessageResolverHolder.DEFAULT = MessageResolver.this;
        }
    }

    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    public String getMessage(String code) {
        return getMessage(code, new Object[]{});
    }

    public static MessageResolver getDefault() {
        return MessageResolverHolder.DEFAULT;
    }

    private static class MessageResolverHolder {

        private static MessageResolver DEFAULT = null;
    }
}
