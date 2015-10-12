
package org.eljaiek.jmira.data.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 *
 * @author eduardo.eljaiek
 */
public class SourceSerializer extends JsonSerializer<Source> {

    @Override
    public void serialize(Source value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
         jgen.writeStartObject();
         jgen.writeBooleanField("enabled", value.isEnabled());
         jgen.writeStringField("url", value.getAtpline());
         jgen.writeEndObject();
    }
}
