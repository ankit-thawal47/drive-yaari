package com.drivelah.model.pricing;

import com.drivelah.model.Location;


public class PricingRequest {

    private String vehicleId;
    private String vehicleType;
    private Location pickupLocation;
    private Location dropLocation;
    private Long plannedStartTime;
    private Long plannedEndTime;
    private Double estimatedKm;

    public PricingRequest() {}

    public PricingRequest(String vehicleId, String vehicleType, Location pickupLocation,
                          Location dropLocation, Long plannedStartTime, Long plannedEndTime) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
    }

    public Double getPlannedDurationHours() {
        if (plannedStartTime != null && plannedEndTime != null) {
            return (plannedEndTime - plannedStartTime) / (1000.0 * 60.0 * 60.0);
        }
        return null;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    public Long getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(Long plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public Long getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(Long plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }

    public Double getEstimatedKm() {
        return estimatedKm;
    }

    public void setEstimatedKm(Double estimatedKm) {
        this.estimatedKm = estimatedKm;
    }
}