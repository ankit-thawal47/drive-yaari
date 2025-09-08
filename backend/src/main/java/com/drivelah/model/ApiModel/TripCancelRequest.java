package com.drivelah.model.ApiModel;

/**
 * Request model for cancelling a trip
 */
public class TripCancelRequest {
    
    private String reason;              // Reason for cancellation
    private String cancelledBy;         // Who cancelled: RENTER, HOST, ADMIN
    private Boolean requestRefund;      // Whether to request refund
    private String additionalNotes;     // Additional cancellation notes
    
    public TripCancelRequest() {}
    
    public TripCancelRequest(String reason, String cancelledBy) {
        this.reason = reason;
        this.cancelledBy = cancelledBy;
        this.requestRefund = true;
    }
    
    // Getters and Setters
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getCancelledBy() {
        return cancelledBy;
    }
    
    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
    
    public Boolean getRequestRefund() {
        return requestRefund != null ? requestRefund : true;
    }
    
    public void setRequestRefund(Boolean requestRefund) {
        this.requestRefund = requestRefund;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}