
package org.eljaiek.jmira.core;

import java.util.Observer;

/**
 *
 * @author eduardo.eljaiek
 */
public interface Download extends Runnable {
    
    int getSize();
    
    float getProgress();
    
    int getDownloaded();
    
    DownloadStatus getStatus();
    
    void pause();
    
    void resume();
    
    void cancel();
    
    void register(Observer observer);
}
