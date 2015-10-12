package org.eljaiek.jmira.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shidara
 */
public class Repository {

    private String name;

    private String home;

    private List<Architecture> architectures;

    private final List<Source> sources;

    public Repository() {
        sources = new ArrayList<>(4);
        architectures = new ArrayList<>(2);
    }

    public Repository(int id, String name, String home) {
        this.name = name;
        this.home = home;
        this.architectures = new ArrayList<>(2);
        sources = new ArrayList<>(4);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public List<Architecture> getArchitectures() {
        return architectures;
    }

    public void setArchitectures(List<Architecture> architectures) {
        this.architectures = architectures;
    }

    public List<Source> getSources() {
        return sources;
    }    

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append("Repository{name=").append(name)
                .append(", home=").append(home).append(", architectures=")
                .append(architectures).append(", sources=").append(sources).toString();     
    }    
}
