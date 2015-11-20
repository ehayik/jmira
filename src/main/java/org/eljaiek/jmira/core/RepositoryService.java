package org.eljaiek.jmira.core;

import java.util.function.LongConsumer;
import org.eljaiek.jmira.data.model.Repository;

/**
 *
 * @author eduardo.eljaiek
 */
public interface RepositoryService {

    void open(Repository reposiory) throws RepositoryAccessException;
    
    Repository open(String home) throws RepositoryAccessException;

    long synchronize(Repository repository, LongConsumer progress);   
}
