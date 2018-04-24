package com.y2y.android;

public class ListEvent {

    private String eventName;
    private String eventStartTime;
    private String eventEndTime;
    private String eventDate;

    public ListEvent(String name, String start, String end, String date) {
        this.eventName = name;
        this.eventStartTime = start;
        this.eventEndTime = end;
        this.eventDate = date;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public String getEventDate() {
        return eventDate;
    }
}
