
package org.eljaiek.jmira.core;

import org.eljaiek.jmira.core.model.DebPackage;

import java.util.List;

/**
 *
 * @author eduardo.eljaiek
 */
public interface PackageService {
    
    List<DebPackage> list(int start, int limit);
    
    List<DebPackage> listNotDownloaded();
}