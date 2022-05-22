package com.core.alertaciudadana.models.location;

public class LocationUpdateEvent {
    private LocationDTO location;

    public LocationUpdateEvent(LocationDTO location) {
        this.location = location;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }
}
