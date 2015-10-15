/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eljaiek.jmira.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author eduardo.eljaiek
 */
@Component
final class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        CONTEXT = ac;
    }    

    public static ApplicationContext getContext() {
        return CONTEXT;
    }    
}
