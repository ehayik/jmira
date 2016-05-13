package org.eljaiek.jmira.app.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eljaiek.jmira.core.model.Architecture;
import org.eljaiek.jmira.core.model.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author eduardo.eljaiek
 */
class RepositoryModel {

    private final StringProperty name = new SimpleStringProperty();

    private final StringProperty home = new SimpleStringProperty();

    private final IntegerProperty available = new SimpleIntegerProperty(0);

    private final IntegerProperty downloads = new SimpleIntegerProperty(0);

    private final LongProperty downloadsSize = new SimpleLongProperty(0);

    private final LongProperty availableSize = new SimpleLongProperty(0);

    private final ListProperty<Architecture> architectures = new SimpleListProperty<>(FXCollections.emptyObservableList());

    private final ListProperty<SourceModel> sources = new SimpleListProperty<>(FXCollections.emptyObservableList());

    public RepositoryModel() {
        name.set("mirror");
        home.set(System.getProperty("user.home"));
        architectures.set(FXCollections.observableArrayList(Architecture.AMD64));
        sources.set(FXCollections.observableArrayList(new ArrayList<SourceModel>()));
    }

    public RepositoryModel(RepositoryModel model) {
        name.set(model.name.get());
        home.set(model.home.get());
        List<Architecture> list = model.architectures.stream().collect(Collectors.toList());
        architectures.set(FXCollections.observableArrayList(list));
        List<SourceModel> srcs = model.getSources()
                .stream()
                .map(src -> new SourceModel(src))
                .collect(Collectors.toList());
        this.sources.set(FXCollections.observableArrayList(srcs));
    }

    public ObservableList<Architecture> getArchitectures() {
        return architectures.get();
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

    public int getAvailable() {
        return available.get();
    }

    public void setAvailable(int value) {
        available.set(value);
    }

    public IntegerProperty availableProperty() {
        return available;
    }

    public int getDownloads() {
        return downloads.get();
    }

    public void setDownloads(int value) {
        downloads.set(value);
    }

    public IntegerProperty downloadsProperty() {
        return downloads;
    }

    public long getDownloadsSize() {
        return downloadsSize.get();
    }

    public LongProperty downloadsSizeProperty() {
        return downloadsSize;
    }

    public void setDownloadsSize(long downloaded) {
        this.downloadsSize.set(downloaded);
    }

    public long getAvailableSize() {
        return availableSize.get();
    }

    public LongProperty availableSizeProperty() {
        return availableSize;
    }

    public void setAvailableSize(long size) {
        this.availableSize.set(size);
    }

    public void setArchitectures(ObservableList<Architecture> value) {
        architectures.set(value);
    }

    public ListProperty architecturesProperty() {
        return architectures;
    }

    public List<Architecture> getArchitureList() {
        List<Architecture> list = new ArrayList<>(architectures.get().size());
        architectures.forEach(list::add);
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

    public Repository getRepository() {
        Repository to = new Repository();
        to.setName(getName());
        to.setHome(getHome()); 
        to.setArchitectures(getArchitectures().stream().collect(Collectors.toList()));
        to.setSources(getSources().stream().map(src -> src.getSource()).collect(Collectors.toList()));
        return to;
    }

    public static RepositoryModel create(Repository repository) {
        RepositoryModel model = new RepositoryModel();
        model.setName(repository.getName());
        model.setHome(repository.getHome());
        model.setArchitectures(FXCollections
                .observableArrayList(repository
                        .getArchitectures()
                        .stream()
                        .collect(Collectors.toList())));
        model.setSources(FXCollections
                .observableArrayList(repository
                        .getSources()
                        .stream()
                        .map(SourceModel::create)
                        .collect(Collectors.toList())));
        return model;
    }
}
