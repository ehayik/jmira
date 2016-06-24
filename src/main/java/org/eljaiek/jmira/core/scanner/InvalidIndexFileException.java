
package org.eljaiek.jmira.core.scanner;

import java.io.IOException;

/**
 *
 * @author eljaiek
 */
public class InvalidIndexFileException extends IOException {

    /**
     * Creates a new instance of <code>InvalidIndexFileException</code> without
     * detail message.
     */
    public InvalidIndexFileException() {
        //default constructor
    }

    /**
     * Constructs an instance of <code>InvalidIndexFileException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidIndexFileException(String msg) {
        super(msg);
    }

    public InvalidIndexFileException(Throwable cause) {
        super(cause);
    }    
}
