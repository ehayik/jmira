
package org.eljaiek.jmira.core;

import java.util.List;
import org.eljaiek.jmira.data.model.DebPackage;

/**
 *
 * @author eduardo.eljaiek
 */
public interface PackageService {
    
    List<DebPackage> list(int start, int limit);
    
    long size();
    
    long downloaded();   
    
    List<DebPackage> listNotDownloaded();
}