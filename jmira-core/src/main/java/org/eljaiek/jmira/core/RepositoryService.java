package org.eljaiek.jmira.core;

import java.io.SyncFailedException;
import org.eljaiek.jmira.core.model.Repository;

import java.util.function.LongConsumer;

/**
 *
 * @author eduardo.eljaiek
 */
public interface RepositoryService {

    void save(Repository repository) throws AccessFailedException;
    
    Repository open(String home) throws AccessFailedException;

    Status synchronize(Repository repository, LongConsumer progress) throws SyncFailedException;
    
    Status refresh(Repository repository);
    
    public final class Status {
        
        private int downloads;
        
        private int available;
        
        private int damaged;
        
        private long availableSize;
        
        private long downloadsSize;

        public Status() {
            //default constructor
        }        
        
        public Status(int downloads, int available, int damaged, long availableSize, long downloadsSize) {
            this.downloads = downloads;
            this.available = available;
            this.damaged = damaged;
            this.availableSize = availableSize;
            this.downloadsSize = downloadsSize;
        }     

        public int getDownloads() {
            return downloads;
        }

        public int getAvailable() {
            return available;
        }

        public int getDamaged() {
            return damaged;
        }

        public long getAvailableSize() {
            return availableSize;
        }

        public long getDownloadsSize() {
            return downloadsSize;
        }   
        
        public void addDownloads(int value) {
            downloads += value; 
        }

        public void addAvailable(int value) {
            available += value;
        }

        public void addDamaged(int value) {
            damaged += value;
        }

        public void addAvailableSize(long value) {
            availableSize += value;
        }

        public void addDownloadsSize(long value) {
            downloadsSize += value;
        }     
    } 
}
