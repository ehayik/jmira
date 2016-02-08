package org.eljaiek.jmira.app.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eljaiek.jmira.data.model.Architecture;
import org.eljaiek.jmira.data.model.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author eduardo.eljaiek
 */
public class RepositoryModel {

    private final StringProperty name = new SimpleStringProperty();

    private final StringProperty home = new SimpleStringProperty();

    private final IntegerProperty packagesCount = new SimpleIntegerProperty(0);

    private final IntegerProperty downloadedCount = new SimpleIntegerProperty(0);

    private final LongProperty downloaded = new SimpleLongProperty(0);

    private final LongProperty size = new SimpleLongProperty(0);

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

    public int getPackagesCount() {
        return packagesCount.get();
    }

    public void setPackagesCount(int value) {
        packagesCount.set(value);
    }

    public IntegerProperty packagesCountProperty() {
        return packagesCount;
    }

    public int getDownloadedCount() {
        return downloadedCount.get();
    }

    public void setDownloadedCount(int value) {
        downloadedCount.set(value);
    }

    public IntegerProperty downloadedCountProperty() {
        return downloadedCount;
    }

    public long getDownloaded() {
        return downloaded.get();
    }

    public LongProperty downloadedProperty() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded.set(downloaded);
    }

    public long getSize() {
        return size.get();
    }

    public LongProperty sizeProperty() {
        return size;
    }

    public void setSize(long size) {
        this.size.set(size);
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
        to.setPackagesCount(getPackagesCount());
        to.setDownloadedCount(getDownloadedCount());
        to.setSize(getSize());
        to.setDownloadedSize(getDownloaded());
        to.setArchitectures(getArchitectures().stream().collect(Collectors.toList()));
        to.setSources(getSources().stream().map(src -> src.getSource()).collect(Collectors.toList()));
        return to;
    }

    public static RepositoryModel create(Repository repository) {
        RepositoryModel model = new RepositoryModel();
        model.setName(repository.getName());
        model.setHome(repository.getHome());
        model.setPackagesCount(repository.getPackagesCount());
        model.setDownloadedCount(repository.getDownloadedCount());
        model.setSize(repository.getSize());
        model.setDownloaded(repository.getDownloadedSize());
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
