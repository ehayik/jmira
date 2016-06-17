package org.eljaiek.jmira.core.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author eduardo.eljaiek
 */
public final class SourceDeserializer extends JsonDeserializer<Source> {

    private static final Logger LOG = LoggerFactory.getLogger(SourceDeserializer.class);

    @Override
    public Source deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        boolean enabled = node.get("enabled").asBoolean();
        String aptLine = node.get("url").asText();
        try {
            return SourceBuilder.create()
                    .enabled(enabled)
                    .aptLine(aptLine).get();
        } catch (IllegalAptLineException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IOException(ex);
        }
    }
}
