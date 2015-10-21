package org.eljaiek.jmira.app.util;

import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import org.eljaiek.jmira.app.model.RepositoryModel;
import org.eljaiek.jmira.app.model.SourceModel;
import org.eljaiek.jmira.data.model.Repository;
import org.eljaiek.jmira.data.model.Source;

/**
 *
 * @author eduardo.eljaiek
 */
public final class ModelMapperHelper {

    private ModelMapperHelper() {
    }

    public static final Repository map(RepositoryModel from) {
        try {
            Repository to = Repository.class.newInstance();
            to.setName(from.getName());
            to.setHome(from.getHome());
            to.setArchitectures(from.getArchitectures().stream().collect(Collectors.toList()));
            to.setSources(from.getSources().stream().map(src -> map(src)).collect(Collectors.toList()));
            return to;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
    
    public static final RepositoryModel map(Repository from) {
        try {
            RepositoryModel model = RepositoryModel.class.newInstance();
            model.setName(from.getName());
            model.setHome(from.getHome());
            model.setArchitectures(FXCollections
                    .observableArrayList(from
                            .getArchitectures()
                            .stream()
                            .collect(Collectors.toList())));
            model.setSources(FXCollections
                    .observableArrayList(from
                            .getSources()
                            .stream()
                            .map(src -> map(src))
                            .collect(Collectors.toList())));            
            return model;
        } catch (InstantiationException | IllegalAccessException ex) {
           throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static final SourceModel map(Source from) {
        try {
            SourceModel model = SourceModel.class.newInstance();
            model.setEnabled(from.isEnabled());
            model.setUri(from.getUri());
            model.setDistribution(from.getDistribution());
            model.setComponents(from.getComponents());
            return model;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    public static final Source map(SourceModel from) {
        try {
            Source src = Source.class.newInstance();
            src.setEnabled(from.isEnabled());
            src.setUri(from.getUri());
            src.setDistribution(from.getDistribution());
            src.setComponents(from.getComponents());
            return src;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
