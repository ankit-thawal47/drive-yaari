package com.drivelah.model.ApiModel;

public class TripCompleteRequest {

    private Long endOdometerReading;    // Odometer reading at trip end
    private String notes;               // Optional notes from renter
    private Boolean hasVehicleIssues;
    private String issueDescription;
    private Double fuelLevel;
    private Boolean requiresCleaning;

    public TripCompleteRequest() {
    }

    public TripCompleteRequest(Long endOdometerReading) {
        this.endOdometerReading = endOdometerReading;
        this.hasVehicleIssues = false;
        this.requiresCleaning = false;
    }

    // Getters and Setters
    public Long getEndOdometerReading() {
        return endOdometerReading;
    }

    public void setEndOdometerReading(Long endOdometerReading) {
        this.endOdometerReading = endOdometerReading;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getHasVehicleIssues() {
        return hasVehicleIssues != null ? hasVehicleIssues : false;
    }

    public void setHasVehicleIssues(Boolean hasVehicleIssues) {
        this.hasVehicleIssues = hasVehicleIssues;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public Double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public Boolean getRequiresCleaning() {
        return requiresCleaning != null ? requiresCleaning : false;
    }

    public void setRequiresCleaning(Boolean requiresCleaning) {
        this.requiresCleaning = requiresCleaning;
    }
}