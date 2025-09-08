package com.drivelah.model.ApiModel;

public class TripStartRequest {
    
    private Long startOdometerReading;  // Odometer reading at trip start
    private String notes;               // Optional notes from renter
    private Boolean hasVehicleIssues;   // Any issues found with vehicle
    private String issueDescription;    // Description of issues if any
    
    public TripStartRequest() {}
    
    public TripStartRequest(Long startOdometerReading) {
        this.startOdometerReading = startOdometerReading;
        this.hasVehicleIssues = false;
    }
    
    // Getters and Setters
    public Long getStartOdometerReading() {
        return startOdometerReading;
    }
    
    public void setStartOdometerReading(Long startOdometerReading) {
        this.startOdometerReading = startOdometerReading;
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
}