package org.eljaiek.jmira.core;

import org.eljaiek.jmira.data.model.Repository;

import java.util.function.LongConsumer;

/**
 *
 * @author eduardo.eljaiek
 */
public interface RepositoryService {

    void save(Repository repository) throws RepositoryAccessException;
    
    Repository open(String home) throws RepositoryAccessException;

    void synchronize(Repository repository, LongConsumer progress);
}
