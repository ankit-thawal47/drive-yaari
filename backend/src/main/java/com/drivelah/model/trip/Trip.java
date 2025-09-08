package com.drivelah.model.trip;

import com.drivelah.model.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "trip")
public class Trip {

    @Id
    private String id;

    private String ownerId;
    private String vehicleId;
    private String renterId;

    private Location pickUpLocation;
    private Location dropLocation;

    private Long dateOfBookingEpoch;
    private Long plannedStartTimeEpoch;
    private Long plannedEndTimeEpoch;
    private Long actualStartTimeEpoch;
    private Long actualEndTimeEpoch;

    private String status;
    private Double totalAmount;
    private Double securityDeposit;
    private String paymentStatus;
    private String specialInstructions;
    private Long startOdometerReading;
    private Long endOdometerReading;
    private Integer renterRating;
    private Integer ownerRating;
    private String renterComments;
    private String ownerComments;
    private boolean hasInsuranceClaim;
    private String insuranceClaimId;

    public Trip() {
        this.dateOfBookingEpoch = Instant.now().toEpochMilli();
        this.status = "PENDING";
        this.paymentStatus = "PENDING";
        this.hasInsuranceClaim = false;
    }

    public Trip(String renterId, String vehicleId, String ownerId, Location pickupLocation, Location dropLocation) {
        this();
        this.renterId = renterId;
        this.vehicleId = vehicleId;
        this.ownerId = ownerId;
        this.pickUpLocation = pickupLocation;
        this.dropLocation = dropLocation;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDateOfBooking() {
        return dateOfBookingEpoch;
    }

    public void setDateOfBooking(Long dateOfBookingEpoch) {
        this.dateOfBookingEpoch = dateOfBookingEpoch;
    }

    public Long getDateOfBookingEpoch() {
        return dateOfBookingEpoch;
    }

    public void setDateOfBookingEpoch(Long dateOfBookingEpoch) {
        this.dateOfBookingEpoch = dateOfBookingEpoch;
    }

    public Long getPlannedStartTimeEpoch() {
        return plannedStartTimeEpoch;
    }

    public void setPlannedStartTimeEpoch(Long plannedStartTimeEpoch) {
        this.plannedStartTimeEpoch = plannedStartTimeEpoch;
    }

    public Long getPlannedEndTimeEpoch() {
        return plannedEndTimeEpoch;
    }

    public void setPlannedEndTimeEpoch(Long plannedEndTimeEpoch) {
        this.plannedEndTimeEpoch = plannedEndTimeEpoch;
    }

    public Long getActualStartTimeEpoch() {
        return actualStartTimeEpoch;
    }

    public void setActualStartTimeEpoch(Long actualStartTimeEpoch) {
        this.actualStartTimeEpoch = actualStartTimeEpoch;
    }

    public Long getActualEndTimeEpoch() {
        return actualEndTimeEpoch;
    }

    public void setActualEndTimeEpoch(Long actualEndTimeEpoch) {
        this.actualEndTimeEpoch = actualEndTimeEpoch;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(Double securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Long getStartOdometerReading() {
        return startOdometerReading;
    }

    public void setStartOdometerReading(Long startOdometerReading) {
        this.startOdometerReading = startOdometerReading;
    }

    public Long getEndOdometerReading() {
        return endOdometerReading;
    }

    public void setEndOdometerReading(Long endOdometerReading) {
        this.endOdometerReading = endOdometerReading;
    }

    public Integer getRenterRating() {
        return renterRating;
    }

    public void setRenterRating(Integer renterRating) {
        this.renterRating = renterRating;
    }

    public Integer getOwnerRating() {
        return ownerRating;
    }

    public void setOwnerRating(Integer ownerRating) {
        this.ownerRating = ownerRating;
    }

    public String getRenterComments() {
        return renterComments;
    }

    public void setRenterComments(String renterComments) {
        this.renterComments = renterComments;
    }

    public String getOwnerComments() {
        return ownerComments;
    }

    public void setOwnerComments(String ownerComments) {
        this.ownerComments = ownerComments;
    }

    public boolean isHasInsuranceClaim() {
        return hasInsuranceClaim;
    }

    public void setHasInsuranceClaim(boolean hasInsuranceClaim) {
        this.hasInsuranceClaim = hasInsuranceClaim;
    }

    public String getInsuranceClaimId() {
        return insuranceClaimId;
    }

    public void setInsuranceClaimId(String insuranceClaimId) {
        this.insuranceClaimId = insuranceClaimId;
        if (insuranceClaimId != null && !insuranceClaimId.isEmpty()) {
            this.hasInsuranceClaim = true;
        }
    }

    public Double getPlannedDurationHours() {
        if (plannedStartTimeEpoch != null && plannedEndTimeEpoch != null) {
            return (plannedEndTimeEpoch - plannedStartTimeEpoch) / (1000.0 * 60.0 * 60.0);
        }
        return null;
    }

    public Double getActualDurationHours() {
        if (actualStartTimeEpoch != null && actualEndTimeEpoch != null) {
            return (actualEndTimeEpoch - actualStartTimeEpoch) / (1000.0 * 60.0 * 60.0);
        }
        return null;
    }

    public Long getDistanceTraveled() {
        if (startOdometerReading != null && endOdometerReading != null) {
            return endOdometerReading - startOdometerReading;
        }
        return null;
    }

    public boolean isActive() {
        return "IN_PROGRESS".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean canBeCancelled() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status);
    }

    public interface FM {
        String OWNER_ID = "ownerId";
        String VEHICLE_ID = "vehicleId";
        String RENTER_ID = "renterId";
        String STATUS = "status";
        String PAYMENT_STATUS = "paymentStatus";
        String DATE_OF_BOOKING_EPOCH = "dateOfBookingEpoch";
        String HAS_INSURANCE_CLAIM = "hasInsuranceClaim";
        String PLANNED_START_TIME_EPOCH = "plannedStartTimeEpoch";
        String PLANNED_END_TIME_EPOCH = "plannedEndTimeEpoch";
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", renterId='" + renterId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}