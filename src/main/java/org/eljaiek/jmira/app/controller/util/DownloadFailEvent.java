package org.eljaiek.jmira.app.controller.util;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Created by shidara on 9/12/15.
 */
public final class DownloadFailEvent extends DownloadEvent {

    static final EventType<DownloadEvent> DOWN_FAIL = new EventType<>(DOWN, "DOWNLOAD_FAIL");

    private Exception error;

    public DownloadFailEvent(EventType<? extends Event> eventType, Exception error) {
        super(eventType);
        this.error = error;
    }

    public Exception getError() {
        return error;
    }
}
