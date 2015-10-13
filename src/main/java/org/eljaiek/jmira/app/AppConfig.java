
package org.eljaiek.jmira.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author eduardo.eljaiek
 */
@Configuration
@ComponentScan({"org.eljaiek.jmira.app"})
class AppConfig {
    
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
       
        return om;
    }
}
