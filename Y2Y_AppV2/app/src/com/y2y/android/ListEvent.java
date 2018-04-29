package com.y2y.android;

/*

This class defines what the ListEvent type would contain.
If you want to pull something else from Event then you have to add that field below.

*/

public class ListEvent {

    private String eventId;
    private String eventName;
    private String eventStartTime;
    private String eventEndTime;
    private String eventDate;
    private String eventDescription;
    private String eventLocation;

    public ListEvent(String id, String name, String start, String end, String date, String description, String location) {
        this.eventId = id;
        this.eventName = name;
        this.eventStartTime = start;
        this.eventEndTime = end;
        this.eventDate = date;
        this.eventDescription = description;
        this.eventLocation = location;
    }

    public String getEventId() {
        return eventId;
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

    public String getEventDescription(){
        return eventDescription;
    }

    public String getEventLocation(){
        return eventLocation;
    }
}
