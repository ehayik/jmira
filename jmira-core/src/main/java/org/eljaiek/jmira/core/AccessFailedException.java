/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eljaiek.jmira.core;

import java.io.IOException;

/**
 *
 * @author eduardo.eljaiek
 */
public class AccessFailedException extends IOException {

    /**
     * Creates a new instance of <code>OpenFailedException</code> without detail
     * message.
     */
    public AccessFailedException() {
        //default constructor
    }

    /**
     * Constructs an instance of <code>OpenFailedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AccessFailedException(String msg) {
        super(msg);
    }

    public AccessFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessFailedException(Throwable cause) {
        super(cause);
    }    
}
