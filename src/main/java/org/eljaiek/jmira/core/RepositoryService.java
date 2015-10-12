package org.eljaiek.jmira.core;

import java.util.function.Function;
import org.eljaiek.jmira.data.model.Repository;

/**
 *
 * @author eduardo.eljaiek
 */
public interface RepositoryService {

    void open(Repository reposiory);
    
    Repository open(String home);

    void synchronize(Repository repository, Function<Integer, Void> progress);    
}
