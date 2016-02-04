package org.eljaiek.jmira.app.controller.util;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Created by shidara on 9/12/15.
 */
public class DownloadEvent extends Event {

    static final EventType<DownloadEvent> DOWN = new EventType<>("DOWNLOAD");

    static final EventType<DownloadEvent> DOWN_DONE = new EventType<>(DOWN, "DOWNLOAD_DONE");

    static final EventType<DownloadEvent> DOWN_SEARCH_DONE = new EventType<>(DOWN, "DOWN_SEARCH_DONE");

    static final EventType<DownloadEvent> DOWN_CANCELLED = new EventType<>(DOWN, "DOWNLOAD_CANCELLED");

    public DownloadEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
