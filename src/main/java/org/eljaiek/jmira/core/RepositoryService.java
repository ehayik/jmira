package org.eljaiek.jmira.core;

import java.util.function.Function;
import org.eljaiek.jmira.data.model.Repository;

/**
 *
 * @author eduardo.eljaiek
 */
public interface RepositoryService {

    void open(Repository reposiory) throws RepositoryAccessException;
    
    Repository open(String home) throws RepositoryAccessException;

    void synchronize(Repository repository, Function<Integer, Void> progress);    
}
