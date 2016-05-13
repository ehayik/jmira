
package org.eljaiek.jmira.core.io;

/**
 *
 * @author eduardo.eljaiek
 */
public class DownloadFailedException extends RuntimeException {

    /**
     * Creates a new instance of <code>DownloadException</code> without detail
     * message.
     */
    public DownloadFailedException() {
    }

    /**
     * Constructs an instance of <code>DownloadException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DownloadFailedException(String msg) {
        super(msg);
    }

    public DownloadFailedException(String message, Throwable cause) {
        super(message, cause);
    } 

    public DownloadFailedException(Throwable cause) {
        super(cause);
    }    
}
