
package org.eljaiek.jmira.core;

/**
 *
 * @author eduardo.eljaiek
 */
public interface Download extends Runnable{
    
    int getSize();
    
    float getProgress();
    
    int getDownloaded();
    
    DownloadStatus getStatus();
    
    void pause();
    
    void resume();
    
    void cancel();
}
