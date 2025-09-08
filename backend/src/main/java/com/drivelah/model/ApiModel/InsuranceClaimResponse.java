package com.drivelah.model.ApiModel;

/**
 * Insurance Claim Response Model for Drive-Lah P2P Car Rental Service
 * <p>
 * This class represents the response structure for insurance claim operations.
 * It contains all the necessary information about an insurance claim that is
 * returned to clients after claim processing.
 * <p>
 * Business Context:
 * - Used when renters report accidents or damages during rental period
 * - Provides claim status and tracking information
 * - Contains financial information about coverage and deductibles
 * - Links to trip and vehicle information for claim validation
 * <p>
 * API Usage:
 * - Returned by /claim-insurance endpoint
 * - Used in claim status tracking APIs
 * - Included in renter and host notification payloads
 *
 * @author Drive-Lah Development Team
 * @version 1.0
 * @since 2024-01-01
 */
public class InsuranceClaimResponse {

    /**
     * Unique identifier for the insurance claim
     * Generated when claim is first submitted
     */
    private String claimId;

    /**
     * ID of the trip during which the incident occurred
     * Links claim to specific rental transaction
     */
    private String tripId;

    /**
     * ID of the vehicle involved in the claim
     * Used for vehicle damage tracking and history
     */
    private String vehicleId;

    /**
     * License plate of the vehicle for easy identification
     */
    private String vehicleLicensePlate;

    /**
     * Current status of the insurance claim
     * <p>
     * Possible values:
     * - SUBMITTED: Claim has been filed
     * - UNDER_REVIEW: Insurance company is investigating
     * - APPROVED: Claim approved for payout
     * - REJECTED: Claim denied
     * - PAID: Settlement has been processed
     * - CLOSED: Claim process completed
     */
    private String claimStatus;

    /**
     * Total amount claimed by the renter/host
     * Represents the estimated cost of damages
     */
    private Double claimAmount;

    /**
     * Amount approved by insurance company
     * May differ from claimed amount based on assessment
     */
    private Double approvedAmount;

    /**
     * Deductible amount that claimant must pay
     * Defined by insurance policy terms
     */
    private Double deductibleAmount;

    /**
     * Net amount to be paid to claimant
     * Calculated as: approvedAmount - deductibleAmount
     */
    private Double payoutAmount;

    /**
     * Timestamp when claim was initially submitted
     * Stored as epoch milliseconds for precision
     */
    private Long claimSubmissionEpoch;

    /**
     * Timestamp when claim was last updated
     * Updated whenever status or amounts change
     */
    private Long lastUpdatedEpoch;

    /**
     * Expected timeline for claim resolution
     * Helps set user expectations
     */
    private String estimatedResolutionTime;

    /**
     * Reference number provided by insurance company
     * Used for tracking with external insurance provider
     */
    private String insuranceReferenceNumber;

    /**
     * Brief description of the incident
     * Summarizes what happened during the trip
     */
    private String incidentDescription;

    /**
     * Contact information for claim inquiries
     * Insurance company or Drive-Lah support contact
     */
    private String contactInfo;

    /**
     * Additional notes or instructions for the claimant
     * May include next steps or required documentation
     */
    private String additionalNotes;

    /**
     * Indicates if any action is requed from the claimant
     * True if documents or information are needed
     */
    private boolean actionRequired;

    /**
     * Default constructor
     */

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public Double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(Double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Double getDeductibleAmount() {
        return deductibleAmount;
    }

    public void setDeductibleAmount(Double deductibleAmount) {
        this.deductibleAmount = deductibleAmount;
    }

    public Double getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(Double payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public Long getClaimSubmissionEpoch() {
        return claimSubmissionEpoch;
    }

    public void setClaimSubmissionEpoch(Long claimSubmissionEpoch) {
        this.claimSubmissionEpoch = claimSubmissionEpoch;
    }

    public Long getLastUpdatedEpoch() {
        return lastUpdatedEpoch;
    }

    public void setLastUpdatedEpoch(Long lastUpdatedEpoch) {
        this.lastUpdatedEpoch = lastUpdatedEpoch;
    }

    public String getEstimatedResolutionTime() {
        return estimatedResolutionTime;
    }

    public void setEstimatedResolutionTime(String estimatedResolutionTime) {
        this.estimatedResolutionTime = estimatedResolutionTime;
    }

    public String getInsuranceReferenceNumber() {
        return insuranceReferenceNumber;
    }

    public void setInsuranceReferenceNumber(String insuranceReferenceNumber) {
        this.insuranceReferenceNumber = insuranceReferenceNumber;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public boolean isActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(boolean actionRequired) {
        this.actionRequired = actionRequired;
    }
}