
package org.eljaiek.jmira.app.controls;

/**
 *
 * @author eduardo.eljaiek
 */
public class JFXControlLoadException extends RuntimeException {

    /**
     * Creates a new instance of <code>JFXControlException</code> without detail
     * message.
     */
    public JFXControlLoadException() {
    }

    /**
     * Constructs an instance of <code>JFXControlException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public JFXControlLoadException(String msg) {
        super(msg);
    }

    public JFXControlLoadException(Throwable cause) {
        super(cause);
    }   
}
