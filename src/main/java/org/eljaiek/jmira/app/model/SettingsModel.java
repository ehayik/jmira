package org.eljaiek.jmira.app.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.eljaiek.jmira.core.model.Settings;

/**
 *
 * @author eljaiek
 */
public class SettingsModel implements Cloneable {

    private final BooleanProperty checksum;

    private final IntegerProperty downloadThreads;

    public SettingsModel() {
        checksum = new SimpleBooleanProperty();
        downloadThreads = new SimpleIntegerProperty(Runtime.getRuntime().availableProcessors());
    }

    public SettingsModel(boolean checksum, int downloadThreads) {
        this.checksum = new SimpleBooleanProperty(checksum);
        this.downloadThreads = new SimpleIntegerProperty(downloadThreads);
    }

    public SettingsModel(SettingsModel settingsModel) {
        checksum = settingsModel.checksum;
        downloadThreads = settingsModel.downloadThreads;
    }

    public boolean isChecksum() {
        return checksum.get();
    }

    public void setChecksum(boolean value) {
        checksum.set(value);
    }

    public BooleanProperty checksumProperty() {
        return checksum;
    }

    public int getDownloadThreads() {
        return downloadThreads.get();
    }

    public void setDownloadThreads(int value) {
        downloadThreads.set(value);
    }

    public IntegerProperty downloadThreadsProperty() {
        return downloadThreads;
    }

    public Settings getSettings() {
        return new Settings(checksum.get(), downloadThreads.get());
    }

    public static SettingsModel create(Settings settings) {
        return new SettingsModel(settings.isChecksum(), settings.getDownloadThreads());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
}
