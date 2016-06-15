package org.eljaiek.jmira.core.io;

import java.util.Observer;

/**
 *
 * @author eljaiek
 */
public interface DownloadBuilder {
    
    DownloadBuilder url(String url);
    
    DownloadBuilder localFolder(String localFolder);
    
    DownloadBuilder checksum(String checksum);
    
    DownloadBuilder observer(Observer observer);
    
    Download get();
}
