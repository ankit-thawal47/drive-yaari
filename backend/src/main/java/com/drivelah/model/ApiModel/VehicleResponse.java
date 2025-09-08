package com.drivelah.model.ApiModel;

import com.drivelah.model.Location;
import com.drivelah.model.Vehicle;

import java.util.List;

public class VehicleResponse {

    private List<Vehicle> vehicleList;

    private List<Double> fare;


    private Location pickUpLocation;


    private Location dropLocation;

    private int totalCount;

    private boolean success;

    private String message;

    private int page;

    private int pageSize;

    public VehicleResponse() {
        this.success = true;
        this.totalCount = 0;
        this.page = 0;
        this.pageSize = 10;
    }

    // Existing getters and setters
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicleList = vehicles;
    }

    public List<Vehicle> getVehicles() {
        return this.vehicleList;
    }

    public List<Double> getFare() {
        return fare;
    }

    public void setFare(List<Double> fare) {
        this.fare = fare;
    }

    public Location getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(Location pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickUpLocation = pickupLocation;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    // New getters and setters
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean hasVehicles() {
        return vehicleList != null && !vehicleList.isEmpty();
    }

    public int getActualCount() {
        return vehicleList != null ? vehicleList.size() : 0;
    }

    public int getTotalPages() {
        if (pageSize <= 0) return 0;
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    @Override
    public String toString() {
        return "VehicleResponse{" +
                "vehicleCount=" + getActualCount() +
                ", totalCount=" + totalCount +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
