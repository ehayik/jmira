
package org.eljaiek.jmira.core;

/**
 *
 * @author eduardo.eljaiek
 */
public class DownloadException extends RuntimeException {

    /**
     * Creates a new instance of <code>DownloadException</code> without detail
     * message.
     */
    public DownloadException() {
    }

    /**
     * Constructs an instance of <code>DownloadException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DownloadException(String msg) {
        super(msg);
    }

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }    
}
