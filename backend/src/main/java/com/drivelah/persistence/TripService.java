package com.drivelah.persistence;

import com.drivelah.client.MongoRepo;
import com.drivelah.model.trip.Trip;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TripService {

    private final MongoRepo<Trip> tripMongoRepo;

    public TripService(MongoRepo<Trip> mongoRepo) {
        this.tripMongoRepo = mongoRepo;
    }

    public Trip saveTrip(Trip trip) {
        // Validation checks
        validateTripData(trip);
        
        // For new trips, check for conflicts
        if (trip.getId() == null) {
            validateNoConflictingTrips(trip);
            // Set booking timestamp if not set
            if (trip.getDateOfBookingEpoch() == null) {
                trip.setDateOfBookingEpoch(Instant.now().toEpochMilli());
            }
        }
        
        return tripMongoRepo.save(trip);
    }
    

    public Trip createTrip(String renterId, String vehicleId, String ownerId,
                          Long plannedStartTime, Long plannedEndTime) {
        
        // Create new trip
        Trip trip = new Trip(renterId, vehicleId, ownerId, null, null);
        trip.setPlannedStartTimeEpoch(plannedStartTime);
        trip.setPlannedEndTimeEpoch(plannedEndTime);
        trip.setStatus("PENDING");
        trip.setPaymentStatus("PENDING");
        
        // TODO: Calculate pricing based on duration and vehicle rates
        // TODO: Integrate with payment system
        // TODO: Send notifications to owner and renter
        
        return saveTrip(trip);
    }
    
    /**
     * Find trip by ID
     * 
     * @param tripId Trip ID to search for
     * @return Trip if found, null otherwise
     */
    public Trip findById(String tripId) {
        return tripMongoRepo.findById(tripId, Trip.class);
    }
    
    /**
     * Find all trips for a specific renter
     * Useful for renter's trip history
     * 
     * @param renterId Renter's user ID
     * @return List of trips for the renter
     */
    public List<Trip> findTripsByRenter(String renterId) {
        Query query = new Query(Criteria.where(Trip.FM.RENTER_ID).is(renterId));
        return tripMongoRepo.find(query, Trip.class);
    }
    
    /**
     * Find all trips for vehicles owned by a specific owner
     * Useful for owner's rental history and earnings
     * 
     * @param ownerId Owner's user ID
     * @return List of trips for owner's vehicles
     */
    public List<Trip> findTripsByOwner(String ownerId) {
        Query query = new Query(Criteria.where(Trip.FM.OWNER_ID).is(ownerId));
        return tripMongoRepo.find(query, Trip.class);
    }
    
    /**
     * Find all trips for a specific vehicle
     * Useful for vehicle history and maintenance scheduling
     * 
     * @param vehicleId Vehicle ID
     * @return List of trips for the vehicle
     */
    public List<Trip> findTripsByVehicle(String vehicleId) {
        Query query = new Query(Criteria.where(Trip.FM.VEHICLE_ID).is(vehicleId));
        return tripMongoRepo.find(query, Trip.class);
    }
    
    /**
     * Find active trip for a vehicle
     * Business Rule: Only one active trip per vehicle
     * 
     * @param vehicleId Vehicle ID
     * @return Active trip if exists, null otherwise
     */
    public Trip findActiveTrip(String vehicleId) {
        Query query = new Query(Criteria.where(Trip.FM.VEHICLE_ID).is(vehicleId)
                                       .and(Trip.FM.STATUS).is("IN_PROGRESS"));
        return tripMongoRepo.findOne(query, Trip.class);
    }
    
    /**
     * Find trips by status
     * Useful for admin dashboard and monitoring
     * 
     * @param status Trip status to filter by
     * @return List of trips with the specified status
     */
    public List<Trip> findTripsByStatus(String status) {
        Query query = new Query(Criteria.where(Trip.FM.STATUS).is(status));
        return tripMongoRepo.find(query, Trip.class);
    }
    
    /**
     * Find trips with insurance claims
     * Useful for insurance reporting and analytics
     * 
     * @return List of trips that have insurance claims
     */
    public List<Trip> findTripsWithInsuranceClaims() {
        Query query = new Query(Criteria.where(Trip.FM.HAS_INSURANCE_CLAIM).is(true));
        return tripMongoRepo.find(query, Trip.class);
    }
    
    /**
     * Find trips within a date range
     * Useful for reporting and analytics
     * 
     * @param startEpoch Start timestamp (epoch milliseconds)
     * @param endEpoch End timestamp (epoch milliseconds)
     * @return List of trips within the date range
     */
    public List<Trip> findTripsByDateRange(Long startEpoch, Long endEpoch) {
        Query query = new Query(Criteria.where(Trip.FM.DATE_OF_BOOKING_EPOCH)
                                       .gte(startEpoch).lte(endEpoch));
        return tripMongoRepo.find(query, Trip.class);
    }
    
    /**
     * Start a trip (vehicle pickup)
     * 
     * Business Process:
     * 1. Validate trip is in CONFIRMED status
     * 2. Record actual start time
     * 3. Update trip status to IN_PROGRESS
     * 4. Record starting odometer reading
     * 
     * @param tripId Trip ID to start
     * @param startOdometer Starting odometer reading
     * @return Updated trip
     */
    public Trip startTrip(String tripId, Long startOdometer) {
        Trip trip = findById(tripId);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        
        if (!"CONFIRMED".equals(trip.getStatus()) && !"PENDING".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip must be confirmed or pending to start. Current status: " + trip.getStatus());
        }
        
        trip.setActualStartTimeEpoch(Instant.now().toEpochMilli());
        trip.setStatus("IN_PROGRESS");
        trip.setStartOdometerReading(startOdometer);
        
        return saveTrip(trip);
    }
    
    /**
     * Complete a trip (vehicle return)
     * 
     * Business Process:
     * 1. Validate trip is IN_PROGRESS
     * 2. Record actual end time
     * 3. Update trip status to COMPLETED
     * 4. Record ending odometer reading
     * 5. Calculate final charges
     * 
     * @param tripId Trip ID to complete
     * @param endOdometer Ending odometer reading
     * @return Updated trip
     */
    public Trip completeTrip(String tripId, Long endOdometer) {
        Trip trip = findById(tripId);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        
        if (!"IN_PROGRESS".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip must be in progress to complete. Current status: " + trip.getStatus());
        }
        
        trip.setActualEndTimeEpoch(Instant.now().toEpochMilli());
        trip.setStatus("COMPLETED");
        trip.setEndOdometerReading(endOdometer);
        
        // TODO: Calculate final charges based on actual duration
        // TODO: Process payment and release security deposit
        // TODO: Update vehicle status to available
        // TODO: Send completion notifications
        
        return saveTrip(trip);
    }
    
    /**
     * Cancel a trip
     * 
     * Business Rules:
     * - Can only cancel PENDING or CONFIRMED trips
     * - Cancellation fees may apply based on timing
     * 
     * @param tripId Trip ID to cancel
     * @param reason Cancellation reason
     * @return Updated trip
     */
    public Trip cancelTrip(String tripId, String reason) {
        Trip trip = findById(tripId);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        
        if (!trip.canBeCancelled()) {
            throw new IllegalStateException("Trip cannot be cancelled. Current status: " + trip.getStatus());
        }
        
        trip.setStatus("CANCELLED");
        trip.setSpecialInstructions("Cancelled: " + reason);
        
        // TODO: Calculate cancellation fees
        // TODO: Process refunds
        // TODO: Update vehicle availability
        // TODO: Send cancellation notifications
        
        return saveTrip(trip);
    }
    
    /**
     * Add rating and review for a completed trip
     * 
     * @param tripId Trip ID
     * @param renterRating Rating from renter (1-5)
     * @param ownerRating Rating from owner (1-5)
     * @param renterComments Comments from renter
     * @param ownerComments Comments from owner
     * @return Updated trip
     */
    public Trip addRatingsAndReviews(String tripId, Integer renterRating, Integer ownerRating,
                                   String renterComments, String ownerComments) {
        Trip trip = findById(tripId);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        
        if (!"COMPLETED".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip must be completed to add ratings");
        }
        
        if (renterRating != null) {
            validateRating(renterRating);
            trip.setRenterRating(renterRating);
        }
        
        if (ownerRating != null) {
            validateRating(ownerRating);
            trip.setOwnerRating(ownerRating);
        }
        
        trip.setRenterComments(renterComments);
        trip.setOwnerComments(ownerComments);
        
        return saveTrip(trip);
    }
    
    /**
     * Link insurance claim to trip
     * 
     * @param tripId Trip ID
     * @param claimId Insurance claim ID
     * @return Updated trip
     */
    public Trip linkInsuranceClaim(String tripId, String claimId) {
        Trip trip = findById(tripId);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        
        trip.setInsuranceClaimId(claimId);
        // This automatically sets hasInsuranceClaim to true
        
        return saveTrip(trip);
    }
    
    /**
     * Get total count of trips
     * 
     * @return Total number of trips
     */
    public long getTripCount() {
        return tripMongoRepo.count(Trip.class);
    }
    
    // Private validation methods
    
    private void validateTripData(Trip trip) {
        if (trip.getRenterId() == null || trip.getRenterId().trim().isEmpty()) {
            throw new IllegalArgumentException("Renter ID is required");
        }
        
        if (trip.getVehicleId() == null || trip.getVehicleId().trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        
        if (trip.getOwnerId() == null || trip.getOwnerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Owner ID is required");
        }
        
        if (trip.getPlannedStartTimeEpoch() != null && trip.getPlannedEndTimeEpoch() != null) {
            if (trip.getPlannedEndTimeEpoch() <= trip.getPlannedStartTimeEpoch()) {
                throw new IllegalArgumentException("End time must be after start time");
            }
        }
    }
    
    private void validateNoConflictingTrips(Trip newTrip) {
        // Check if vehicle has active or confirmed trips that would conflict
        Query query = new Query(Criteria.where(Trip.FM.VEHICLE_ID).is(newTrip.getVehicleId())
                                       .and(Trip.FM.STATUS).in("CONFIRMED", "IN_PROGRESS"));
        
        List<Trip> conflictingTrips = tripMongoRepo.find(query, Trip.class);
        
        if (!conflictingTrips.isEmpty()) {
            throw new IllegalStateException("Vehicle is not available for the requested time period");
        }
        
        // TODO: Add more sophisticated overlap checking based on planned times
    }
    
    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
