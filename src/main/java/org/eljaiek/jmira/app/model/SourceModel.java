package org.eljaiek.jmira.app.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author eduardo.eljaiek
 */
public class SourceModel {

    private final BooleanProperty enabled = new SimpleBooleanProperty(true);

    private final StringProperty uri = new SimpleStringProperty();

    private final StringProperty distribution = new SimpleStringProperty();

    private final StringProperty components = new SimpleStringProperty();

    public SourceModel() {
    }

    public SourceModel(boolean enabled, String uri, String distribution, String components) {
        this.enabled.set(enabled);
        this.uri.set(uri);
        this.distribution.set(distribution);
        this.components.set(components);
    }    

    public SourceModel(SourceModel model) {
        this.enabled.set(model.enabled.get());
        this.uri.set(model.uri.get());
        this.distribution.set(model.distribution.get());
        this.components.set(model.components.get());
    }  
    
    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean value) {
        enabled.set(value);
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public String getUri() {
        return uri.get();
    }

    public void setUri(String value) {
        uri.set(value);
    }

    public StringProperty uriProperty() {
        return uri;
    }

    public String getDistribution() {
        return distribution.get();
    }

    public void setDistribution(String value) {
        distribution.set(value);
    }

    public StringProperty distributionProperty() {
        return distribution;
    }

    public String getComponents() {
        return components.get();
    }

    public void setComponents(String value) {
        components.set(value);
    }

    public StringProperty componentsProperty() {
        return components;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(uri.get()).append("/ ").append(distribution.get());
        builder.append(" ").append(components.get());
        return builder.toString();
    }   
}
