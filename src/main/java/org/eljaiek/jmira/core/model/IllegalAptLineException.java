
package org.eljaiek.jmira.core.model;

/**
 *
 * @author eduardo.eljaiek
 */
public class IllegalAptLineException extends Exception {

    /**
     * Creates a new instance of <code>IllegalAptLineException</code> without
     * detail message.
     */
    public IllegalAptLineException() {
        //defualt constructor
    }

    /**
     * Constructs an instance of <code>IllegalAptLineException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public IllegalAptLineException(String msg) {
        super(msg);
    }

    public IllegalAptLineException(Throwable cause) {
        super(cause);
    }   
}
