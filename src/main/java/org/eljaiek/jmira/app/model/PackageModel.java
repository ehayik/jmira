package org.eljaiek.jmira.app.model;

import java.io.File;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.eljaiek.jmira.core.model.DebPackage;

/**
 *
 * @author eduardo.eljaiek
 */
public class PackageModel {

    public enum Status {
        AVAILABLE, DOWNLOADED
    }

    private final StringProperty name = new SimpleStringProperty();

    private final LongProperty size = new SimpleLongProperty(0);

    private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.AVAILABLE);

    public PackageModel() {
    }

    public PackageModel(String name, long size, Status status) {
        this.name.set(name);
        this.size.set(size);
        this.status.set(status);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty getNameProperty() {
        return name;
    }

    public long getSize() {
        return size.get();
    }

    public void setSize(long size) {
        this.size.set(size);
    }

    public LongProperty getSizeProperty() {
        return size;
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public ObjectProperty<Status> getStatusProperty() {
        return status;
    }

    public static final PackageModel create(DebPackage debPackage) {
        Status status = Status.AVAILABLE;

        File file = new File(debPackage.getLocalUrl());

        if (file.exists()) {
            status = Status.DOWNLOADED;
        }

        return new PackageModel(debPackage.getName(), debPackage.getLength(), status);
    }
}
