
package org.eljaiek.jmira.core.scanner;

import java.io.IOException;

/**
 *
 * @author eljaiek
 */
public class InvalidPackagesFileException extends IOException {

    /**
     * Creates a new instance of <code>PackageScannerException</code> without
     * detail message.
     */
    public InvalidPackagesFileException() {
        //default constructor
    }

    /**
     * Constructs an instance of <code>PackageScannerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidPackagesFileException(String msg) {
        super(msg);
    }

    public InvalidPackagesFileException(Throwable cause) {
        super(cause);
    }    
}
