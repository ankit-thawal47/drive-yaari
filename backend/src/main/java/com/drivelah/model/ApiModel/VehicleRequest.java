package com.drivelah.model.ApiModel;

import com.drivelah.model.Location;

public class VehicleRequest {

    private Location pickUpLocation;
    private Location dropLocation;

    public Location getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(Location pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }
}
