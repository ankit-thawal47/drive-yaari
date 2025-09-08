package com.drivelah.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "vehicle")
public class Vehicle {
    @Id
    private String id;
    private String licensePlate;
    private String ownerId;
    //epoch in millisecs
    private Long dateOfRegEpoch;

    //isVerified - flag is used to check if the vehicle is verified,
    //when registered the vehicle is not verified
    private boolean isVerified = false;

    //flag is to check where the vehicle is booked or available for renting
    //Enum - REPAIRING, RESTING, FREE, RENTED
    private String status;

    // Vehicle details for better user experience
    private String vehicleType;    // ECONOMY, STANDARD, PREMIUM
    private String make;           // Toyota, Honda, BMW, etc.
    private String model;          // Vios, Civic, 3 Series, etc.
    private Integer year;          // 2020, 2021, etc.
    private String color;          // White, Black, Silver, etc.
    private String transmission;   // AUTO, MANUAL
    private Integer seatingCapacity; // 4, 5, 7, etc.
    
    // Location for vehicle pickup (can be different from owner's address)
    private Location pickupLocation;
    
    // Additional features and pricing
    private String features;       // JSON string or comma-separated: "GPS,Bluetooth,USB"
    private String description;    // Host's description of the vehicle
    private Double customPricePerHour; // Override default pricing if set

    public Vehicle() {
    }

    public Vehicle(String licensePlate, String ownerId, String status) {
        this.licensePlate = licensePlate;
        this.ownerId = ownerId;
        this.dateOfRegEpoch = Instant.now().toEpochMilli();
        this.isVerified = false;
        this.status = status;
        // Set default vehicle type if not specified
        this.vehicleType = "STANDARD";
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @JsonProperty("isVerified")
    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDateOfRegEpoch() {
        return dateOfRegEpoch;
    }

    public void setDateOfRegEpoch(Long dateOfRegEpoch) {
        this.dateOfRegEpoch = dateOfRegEpoch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCustomPricePerHour() {
        return customPricePerHour;
    }

    public void setCustomPricePerHour(Double customPricePerHour) {
        this.customPricePerHour = customPricePerHour;
    }

    public interface FM {
        String IS_VERIFIED = "isVerified";
        String LICENSE_PLATE = "licensePlate";
        String OWNER_ID = "ownerId";
        String VEHICLE_TYPE = "vehicleType";
        String MAKE = "make";
        String MODEL = "model";
    }
}
