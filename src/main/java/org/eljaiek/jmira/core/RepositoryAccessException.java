
package org.eljaiek.jmira.core;

/**
 *
 * @author eduardo.eljaiek
 */
public class RepositoryAccessException extends Exception {

    /**
     * Creates a new instance of <code>OpenRepositoryException</code> without
     * detail message.
     */
    public RepositoryAccessException() {
    }

    /**
     * Constructs an instance of <code>OpenRepositoryException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RepositoryAccessException(String msg) {
        super(msg);
    }

    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }    
}
