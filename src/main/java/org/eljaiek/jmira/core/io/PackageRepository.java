
package org.eljaiek.jmira.core.io;

import org.eljaiek.jmira.core.model.DebPackage;

import java.util.List;

/**
 *
 * @author eduardo.eljaiek
 */
public interface PackageRepository {
    
    void saveAll(List<DebPackage> packages);    
    
    List<DebPackage> findAll(int start, int limit);
    
    List<DebPackage> findIdles(boolean checksum);

    void removeAll();
    
    int count();
    
    int downloads(boolean checksum);
    
    long size();
    
    long downloadsSize(boolean checksum);
}
