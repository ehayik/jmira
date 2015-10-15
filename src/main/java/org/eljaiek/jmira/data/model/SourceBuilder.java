package org.eljaiek.jmira.data.model;

import org.eljaiek.jmira.core.MessageResolver;

/**
 *
 * @author eduardo.eljaiek
 */
public final class SourceBuilder {

    private final Source source = new Source();

    private SourceBuilder() {
    }

    public static final SourceBuilder create() {
        return new SourceBuilder();
    }

    public final Source get() {
        return source;
    }

    public SourceBuilder enabled(boolean enabled) {
        source.setEnabled(enabled);
        return this;
    }

    public SourceBuilder aptLine(String aptLine) {

        try {
            String[] arr = aptLine.trim().split(" ");

            if (arr.length <= 1) {
                throw new IllegalArgumentException(MessageResolver.getDefault().getMessage("aptLine.error"));
            }

            source.setUri(arr[0]);
            source.setDistribution(arr[1]);
            StringBuilder builder = new StringBuilder();

            for (int i = 2; i < arr.length; i++) {
                builder.append(arr[i]).append(" ");
            }

            source.setComponents(builder.toString().trim());
            return this;
        } catch (Exception e) {
            throw new IllegalArgumentException(MessageResolver.getDefault().getMessage("aptLine.error"));
        }
    }
}
