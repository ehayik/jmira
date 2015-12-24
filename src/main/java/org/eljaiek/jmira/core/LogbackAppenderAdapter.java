package org.eljaiek.jmira.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.HashMap;
import java.util.Map;

public final class LogbackAppenderAdapter extends AppenderBase<ILoggingEvent> {
    
    private static final Map<String, LogHandler> COMPONENT_MAP = new HashMap<>(5);
    
    private PatternLayout layout;
    
    public static void register(String appenderName, LogHandler handler) {
        COMPONENT_MAP.put(appenderName, handler);
    }
    
    public static void remove(String appenderName) {
        COMPONENT_MAP.remove(appenderName);
    }
    
    public void setPattern(String pattern) {
        layout = new PatternLayout();
        layout.setPattern(pattern);
        layout.setContext(getContext());
        layout.start();
    }
    
    @Override
    protected void append(final ILoggingEvent event) {
        
        if (layout != null) {            
            LogHandler handler = COMPONENT_MAP.get(getName());            
            
            if (handler != null) {
                String log = layout.doLayout(event);                
                
                if (Level.INFO == event.getLevel()) {
                    handler.info(log);
                    return;
                }
                
                if (Level.ERROR == event.getLevel()) {
                    handler.error(log);
                    return;
                }
                
                handler.warn(log);
            }
        }
    }
}
