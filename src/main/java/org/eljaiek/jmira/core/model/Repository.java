package org.eljaiek.jmira.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eljaiek
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {

    private String name;

    private String home;

    private List<Architecture> architectures;

    private List<Source> sources;

    private Settings settings;

    public Repository() {
        sources = new ArrayList<>(4);
        architectures = new ArrayList<>(2);
        settings = new Settings();
    }

    public Repository(String name, String home, Settings preferences) {
        this.name = name;
        this.home = home;
        this.architectures = new ArrayList<>(2);
        sources = new ArrayList<>(4);
        this.settings = preferences;
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

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }    

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Repository{");
        sb.append("name='").append(name).append('\'');
        sb.append(", home='").append(home).append('\'');
        sb.append(", architectures=").append(architectures);
        sb.append(", sources=").append(sources);
        sb.append('}');
        return sb.toString();
    }
}
