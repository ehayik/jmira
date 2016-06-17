
package org.eljaiek.jmira.core;

/**
 *
 * @author eduardo.eljaiek
 */
public class SyncFailedException extends RuntimeException {

    /**
     * Creates a new instance of <code>SyncFailedException</code> without detail
     * message.
     */
    public SyncFailedException() {
        //default constructor
    }

    public SyncFailedException(String message, Throwable cause) {
        super(message, cause);
    } 

    /**
     * Constructs an instance of <code>SyncFailedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SyncFailedException(String msg) {
        super(msg);
    }
}
