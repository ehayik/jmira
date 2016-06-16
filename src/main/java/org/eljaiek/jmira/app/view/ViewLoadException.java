
package org.eljaiek.jmira.app.view;

/**
 *
 * @author eduardo.eljaiek
 */
public class ViewLoadException extends RuntimeException {

    /**
     * Creates a new instance of <code>ViewLoadException</code> without detail
     * message.
     */
    public ViewLoadException() {
        //default constructor
    }

    /**
     * Constructs an instance of <code>ViewLoadException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ViewLoadException(String msg) {
        super(msg);
    }

    public ViewLoadException(String message, Throwable cause) {
        super(message, cause);
    }    
}
