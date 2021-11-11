package com.core.alertaciudadana.models.location;

public class LocationDTO {
    public double latitude;
    public double longitude;
    public double speed;

    @Override
    public String toString() {
        return "lat:"+latitude +"- lng:"+longitude + "- speed:"+speed;
    }
}
