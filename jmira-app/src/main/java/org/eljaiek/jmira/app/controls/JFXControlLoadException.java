
package org.eljaiek.jmira.app.controls;

/**
 *
 * @author eduardo.eljaiek
 */
public class JFXControlLoadException extends RuntimeException {

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
