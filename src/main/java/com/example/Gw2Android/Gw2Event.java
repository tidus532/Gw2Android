package com.example.Gw2Android;

/**
 * Represents an event from the api call.
 */
public class Gw2Event {
    private int worldId;
    private int mapId;
    private String eventId;
    private String state;
    private String name;

    public Gw2Event(){}

    public Gw2Event(int worldId, int mapId, String eventId, String state) {
        this.worldId = worldId;
        this.mapId = mapId;
        this.eventId = eventId;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
