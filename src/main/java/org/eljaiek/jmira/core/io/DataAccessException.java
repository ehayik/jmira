
package org.eljaiek.jmira.core.io;

/**
 *
 * @author eduardo.eljaiek
 */
public class DataAccessException extends RuntimeException {

    /**
     * Creates a new instance of <code>DataAccessException</code> without detail
     * message.
     */
    public DataAccessException() {
    }

    /**
     * Constructs an instance of <code>DataAccessException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }    

    public DataAccessException(Throwable cause) {
        super(cause);
    }    
}
