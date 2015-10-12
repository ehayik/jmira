package org.eljaiek.jmira.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by shidara on 28/08/15.
 */
@JsonSerialize(using = SourceSerializer.class)
@JsonDeserialize(using = SourceDeserializer.class)
public class Source {

    private static final String DISTS_FOLDER = "dists";
    
    private boolean enabled = true;

    private String uri;

    private String distribution;

    private String components;

    public Source() {
    }

    public Source(boolean enabled, String atpline) {
        this.enabled = enabled;
    }

    public Source(boolean enabled, String uri, String distribution, String components) {
        this.enabled = enabled;
        this.uri = uri;
        this.distribution = distribution;
        this.components = components;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    public String getAtpline() {
        StringBuilder builder = new StringBuilder();
        builder.append(uri).append("/ ").append(distribution);
        builder.append(" ").append(components);
        return builder.toString();
    }

//    public String getDistFolder() {
//        return String.join("/", DISTS_FOLDER, distribution);
//    }
    
    public String[] getComponentsList() {
        return components.split(" ");       
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Source other = (Source) obj;

        return this.getAtpline().trim().equals(other.getAtpline().trim());
    }

    @Override
    public String toString() {
        return getAtpline();
    }  
}
