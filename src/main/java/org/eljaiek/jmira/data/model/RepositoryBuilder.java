package org.eljaiek.jmira.data.model;

import org.springframework.util.Assert;

/**
 *
 * @author eduardo.eljaiek
 */
public final class RepositoryBuilder {
    
    private final Repository repository = new Repository();
    
    private RepositoryBuilder() {
    }    
    
    public static final RepositoryBuilder build() {
        return new RepositoryBuilder();
    }
    
    public final RepositoryBuilder name(String name) {
        Assert.hasText(name);
        repository.setName(name);
        return this;
    }
    
    public final RepositoryBuilder home(String home) {
        repository.setHome(home);
        return this;
    }
    
    public final RepositoryBuilder architecture(Architecture arch) {
        repository.getArchitectures().add(arch);
        return this;
    }
    
    public final RepositoryBuilder source(String source) {
        Assert.hasText(source);
        repository.getSources()
                .add(SourceBuilder.build().aptLine(source).get());
        return this;
    }
    
    public final Repository get() {
        return repository;
    }
}
