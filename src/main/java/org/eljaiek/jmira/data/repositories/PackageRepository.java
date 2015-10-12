
package org.eljaiek.jmira.data.repositories;

import java.util.List;
import org.eljaiek.jmira.data.model.DebPackage;

/**
 *
 * @author eduardo.eljaiek
 */
public interface PackageRepository {
    
    void saveAll(List<DebPackage> packages);    
    
    List<DebPackage> findAll(int start, int limit);
    
    List<DebPackage> findAll(int limit);
    
    int count();
    
    long size();
    
    long downloaded();
}
