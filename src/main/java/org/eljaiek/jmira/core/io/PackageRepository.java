
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
    
    List<DebPackage> findIdles();

    void removeAll();
    
    int count();
    
    int downloads();
    
    long size();
    
    long downloadsSize();
}
