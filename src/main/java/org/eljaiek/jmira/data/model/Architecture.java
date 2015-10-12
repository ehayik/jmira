
package org.eljaiek.jmira.data.model;

/**
 *
 * @author shidara
 */
public enum Architecture {
    AMD64("binary-amd64"), I386("binary-i386");
    
    private final String folder;
    
   private Architecture(String folder) {
       this.folder = folder;
   } 

    public String getFolder() {
        return folder;
    }
}
