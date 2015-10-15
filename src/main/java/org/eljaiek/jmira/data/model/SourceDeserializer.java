package org.eljaiek.jmira.data.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 *
 * @author eduardo.eljaiek
 */
public final class SourceDeserializer extends JsonDeserializer<Source> {

    @Override
    public Source deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        boolean enabled = node.get("enabled").asBoolean();
        String aptLine = node.get("url").asText();
        return SourceBuilder.create()
                .enabled(enabled)
                .aptLine(aptLine).get();
    }
}
