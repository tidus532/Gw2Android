package com.example.Gw2Android;

import android.util.Log;

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

    public Gw2Event(int worldId, int mapId, String eventId, String state, String name) {
        this.worldId = worldId;
        this.mapId = mapId;
        this.eventId = eventId;
        this.state = state;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getMapId() {
        return mapId;
    }


    public String getEventId() {
        return eventId;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object obj){
        Log.d("Gw2", "CALL!");
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Gw2Event))
            return false;

        Gw2Event rhs = (Gw2Event) obj;
        if(rhs.eventId.equals(this.eventId) && rhs.worldId == this.worldId && rhs.mapId == this.mapId){
            Log.d("Gw2", "EQUAL!");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode(){
        return this.eventId.hashCode() + worldId + mapId;
    }

}
