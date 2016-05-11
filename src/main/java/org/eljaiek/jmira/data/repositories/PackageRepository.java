
package org.eljaiek.jmira.data.repositories;

import org.eljaiek.jmira.data.model.DebPackage;

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
