package org.eljaiek.jmira.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eljaiek.jmira.data.model.Architecture;

/**
 *
 * @author eduardo.eljaiek
 */
public class RepositoryModel {

    private final StringProperty name = new SimpleStringProperty();

    private final StringProperty home = new SimpleStringProperty();

    private final ListProperty<Architecture> archs = new SimpleListProperty<>(FXCollections.emptyObservableList());

    private final ListProperty<SourceModel> sources = new SimpleListProperty<>(FXCollections.emptyObservableList());

    public RepositoryModel() {
        name.set("mirror");
        home.set(System.getProperty("user.home"));
        archs.set(FXCollections.observableArrayList(Architecture.AMD64));
    }

    public RepositoryModel(RepositoryModel model) {
        name.set(model.name.get());
        home.set(model.home.get());
        List<Architecture> list = model.archs.stream().collect(Collectors.toList());
        archs.set(FXCollections.observableArrayList(list));
        List<SourceModel> srcs = model.getSources()
                .stream()
                .map(src -> new SourceModel(src))
                .collect(Collectors.toList());
        this.sources.set(FXCollections.observableArrayList(srcs));
    }

    public ObservableList<Architecture> getArchitectures() {
        return archs.get();
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getHome() {
        return home.get();
    }

    public void setHome(String value) {
        home.set(value);
    }

    public StringProperty homeProperty() {
        return home;
    }

    public void setArchitectures(ObservableList<Architecture> value) {
        archs.set(value);
    }

    public ListProperty architecturesProperty() {
        return archs;
    }

    public List<Architecture> getArchitureList() {
        List<Architecture> list = new ArrayList<>(archs.get().size());
        archs.forEach(list::add);
        return list;
    }

    public ObservableList<SourceModel> getSources() {
        return sources.get();
    }

    public void setSources(ObservableList<SourceModel> value) {
        sources.set(value);
    }

    public ListProperty sourcesProperty() {
        return sources;
    }
}
