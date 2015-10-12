package org.eljaiek.jmira.data.model;

/**
 *
 * @author eduardo.eljaiek
 */
public final class SourceBuilder {

    private final Source source = new Source();

    private SourceBuilder() {
    }    

    public static final SourceBuilder build() {
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
        int index = aptLine.lastIndexOf("/");
        source.setUri(aptLine.substring(0, index));
        String[] arr = aptLine.substring(index + 1, aptLine.length()).trim().split(" ");
        source.setDistribution(arr[0]);

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < arr.length; i++) {
            builder.append(arr[i]).append(" ");
        }

        source.setComponents(builder.toString().trim());
        return this;
    }
}
