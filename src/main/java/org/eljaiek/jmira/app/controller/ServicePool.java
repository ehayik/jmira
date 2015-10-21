
package org.eljaiek.jmira.app.controller;

import javafx.concurrent.Service;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author eduardo.eljaiek
 */
@Component
class ServicePool implements ApplicationContextAware {

    private ApplicationContext context;
       
    public <T> Service<T>  getService(String name) {
        return (Service<T>) context.getBean(name);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
         this.context = ac;
    }    
}
