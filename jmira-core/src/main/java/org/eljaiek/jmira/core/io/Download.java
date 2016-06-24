
package org.eljaiek.jmira.core.io;

import java.io.File;
import java.util.Observer;

/**
 *
 * @author eduardo.eljaiek
 */
public interface Download extends Runnable {
    
    float getProgress();
    
    int getSize();    
    
    int getDownloaded();
    
    String getLocalUrl();
    
    DownloadStatus getStatus();
    
    void start();
    
    void pause();
    
    void resume();
    
    void cancel();   
    
    void register(Observer observer);
    
    default void clean() {
        new File(getLocalUrl()).delete();
    } 
 
    @Override
    default  void run() {
        start();
    }
}
