package com.drivelah.api;

import com.drivelah.model.ApiModel.*;
import com.drivelah.model.Location;
import com.drivelah.model.Vehicle;
import com.drivelah.model.auth.AuthRequest;
import com.drivelah.model.auth.AuthResponse;
import com.drivelah.model.auth.AuthUser;
import com.drivelah.model.pricing.PricingCalculation;
import com.drivelah.model.pricing.PricingRequest;
import com.drivelah.model.trip.Trip;
import com.drivelah.persistence.TripService;
import com.drivelah.persistence.VehicleService;
import com.drivelah.service.AuthService;
import com.drivelah.service.PricingService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    private static final String NUM = "10";
    private final VehicleService vehicleService;
    private final TripService tripService;
    private final AuthService authService;
    private final PricingService pricingService;

    @Autowired
    public ApiController(VehicleService vehicleService, TripService tripService,
                         AuthService authService, PricingService pricingService) {
        this.vehicleService = vehicleService;
        this.tripService = tripService;
        this.authService = authService;
        this.pricingService = pricingService;
    }

    @GetMapping(value = "/test")
    public String getNum() {
        return "WORKING";
    }

    //Authentication Endpoints

    @PostMapping(value = "/auth/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuthResponse login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);

            if (response.isSuccess()) {
                System.out.println("[SUCCESS] Mock login successful: " + request.getEmail() +
                        " | Role: " + response.getUser().getRole());
            } else {
                System.out.println("[FAILED] Mock login failed: " + request.getEmail() +
                        " | Reason: " + response.getMessage());
            }

            return response;

        } catch (Exception e) {
            System.err.println("Login endpoint error: " + e.getMessage());
            return AuthResponse.failure("Login failed due to server error");
        }
    }

    @PostMapping(value = "/auth/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuthResponse register(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.register(request);

            if (response.isSuccess()) {
                System.out.println("Mock registration successful: " + request.getEmail() +
                        " | Role: " + request.getRole() +
                        " | UserID: " + response.getUser().getUserId());
            } else {
                System.out.println("Mock registration failed: " + request.getEmail() +
                        " | Reason: " + response.getMessage());
            }

            return response;

        } catch (Exception e) {
            System.err.println("Registration endpoint error: " + e.getMessage());
            return AuthResponse.failure("Registration failed due to server error");
        }
    }

    /**
     * me call,
     * this can be used as a heartbeat call also
     *
     * @param authHeader
     * @return
     */
    @GetMapping(value = "/auth/me")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthResponse getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                return AuthResponse.failure("Authorization header missing or invalid");
            }

            AuthUser user = authService.validateToken(token);
            if (user == null) {
                return AuthResponse.failure("Invalid or expired token");
            }

            return AuthResponse.success(token, user);

        } catch (Exception e) {
            System.err.println("Get current user error: " + e.getMessage());
            return AuthResponse.failure("Failed to get user profile");
        }
    }

    //Pricing Endpoints

    @PostMapping(value = "/calculate-pricing")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PricingCalculation calculatePricing(@RequestBody PricingRequest request) {
        try {
            // Validate basic request
            if (request.getVehicleType() == null || request.getVehicleType().trim().isEmpty()) {
                throw new IllegalArgumentException("Vehicle type is required");
            }

            if (request.getPlannedStartTime() == null || request.getPlannedEndTime() == null) {
                throw new IllegalArgumentException("Start and end times are required");
            }

            if (request.getPlannedEndTime() <= request.getPlannedStartTime()) {
                throw new IllegalArgumentException("End time must be after start time");
            }

            // Calculate pricing
            PricingCalculation pricing = pricingService.calculatePricing(request);

            System.out.println("Pricing calculated for " + request.getVehicleType() +
                    " vehicle: SGD$" + pricing.getTotalAmount());

            return pricing;

        } catch (IllegalArgumentException e) {
            System.err.println("Pricing calculation validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during pricing calculation: " + e.getMessage());
            e.printStackTrace(); // for logging
            throw new RuntimeException("Failed to calculate pricing. Please try again.", e);
        }
    }

    @GetMapping(value = "/vehicle-types")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, PricingService.VehicleTypeRates> getVehicleTypes() {
        try {
            return pricingService.getAllRates();
        } catch (Exception e) {
            System.err.println("Error fetching vehicle types: " + e.getMessage());
            throw new RuntimeException("Failed to fetch vehicle types", e);
        }
    }

    //System Endpoints

    @PostMapping(value = "/get-vehicles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public VehicleResponse getVehicles(@RequestBody VehicleRequest request) {
        Location pickUpLocation = request.getPickUpLocation();
        Location dropLocation = request.getDropLocation();

        // Business Logic: List all the vehicles which meet the criteria
        // v1 --> we will consider only pickup location
        // dropLocation will be used to calculate the fare and charges

        try {
            // Step 1: Get all verified and available vehicles
            List<Vehicle> availableVehicles = vehicleService.getVerifiedVehicles()
                    .stream()
                    .filter(vehicle -> "FREE".equals(vehicle.getStatus()))
                    .collect(java.util.stream.Collectors.toList());

            // Step 2: Create response object
            VehicleResponse response = new VehicleResponse();
            response.setVehicles(availableVehicles);
            response.setTotalCount(availableVehicles.size());
            response.setPickupLocation(pickUpLocation);
            response.setDropLocation(dropLocation);
            response.setSuccess(true);
            response.setMessage("Found " + availableVehicles.size() + " available vehicles");

            // Step 3: Future enhancements
            // TODO: Implement geospatial filtering based on pickup location
            // TODO: Add date/time availability checking
            // TODO: Add vehicle type/category filtering
            // TODO: Add price range filtering
            // TODO: Add rating/review based sorting

            return response;

        } catch (Exception e) {
            // Error handling
            VehicleResponse errorResponse = new VehicleResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Error retrieving vehicles: " + e.getMessage());
            errorResponse.setVehicles(new java.util.ArrayList<>());
            errorResponse.setTotalCount(0);
            return errorResponse;
        }
    }


    //Host Endpoints

    @PostMapping(value = "/register-vehicle")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Vehicle registerVehicle(@RequestBody Vehicle vehicle, @RequestHeader("Authorization") String authHeader) {

        try {
            // Step 0: Authentication - Only verified hosts can register vehicles
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            if (!currentUser.isHost()) {
                throw new IllegalArgumentException("Only hosts can register vehicles. Current role: " + currentUser.getRole());
            }

            if (!currentUser.isVerified()) {
                throw new IllegalArgumentException("Host account must be verified before registering vehicles.");
            }

            // Step 1: Validation
            if (vehicle.getLicensePlate() == null || vehicle.getLicensePlate().trim().isEmpty()) {
                throw new IllegalArgumentException("License plate cannot be empty");
            }

            // Step 2: Set owner ID from authenticated user (security measure)
            vehicle.setOwnerId(currentUser.getUserId());

            // Step 3: Set default values for new registration
            vehicle.setVerified(false); // All new vehicles start as unverified
            if (vehicle.getStatus() == null) {
                vehicle.setStatus("RESTING"); // Default status
            }
            if (vehicle.getDateOfRegEpoch() == null) {
                vehicle.setDateOfRegEpoch(java.time.Instant.now().toEpochMilli());
            }
            if (vehicle.getVehicleType() == null || vehicle.getVehicleType().trim().isEmpty()) {
                vehicle.setVehicleType("STANDARD"); // Default vehicle type
            }
            // Validate vehicle type
            if (!isValidVehicleType(vehicle.getVehicleType())) {
                throw new IllegalArgumentException("Invalid vehicle type. Must be: ECONOMY, STANDARD, or PREMIUM");
            }

            // Step 4: Check for duplicate license plates
            List<Vehicle> existingVehicles = vehicleService.findByLicensePlate(vehicle.getLicensePlate());
            if (!existingVehicles.isEmpty()) {
                throw new IllegalArgumentException("Vehicle with license plate " + vehicle.getLicensePlate() + " is already registered");
            }

            // Step 5: Save vehicle
            Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);

            // Step 6: Log successful registration
            System.out.println("Vehicle registered successfully: " + savedVehicle.getLicensePlate() +
                    " by host: " + currentUser.getName() + " (" + currentUser.getUserId() + ")");

            // TODO: Send notification to host about next steps
            // TODO: Trigger vehicle verification workflow
            // TODO: Create audit log entry

            return savedVehicle;

        } catch (IllegalArgumentException e) {
            // Return error response (in production, use proper error handling with ResponseEntity)
            System.err.println("Vehicle registration validation error: " + e.getMessage());
            throw e; // Re-throw for proper HTTP error response

        } catch (Exception e) {
            // Handle unexpected errors
            System.err.println("Unexpected error during vehicle registration: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to register vehicle. Please try again.", e);
        }
    }

    //Renter Endpoints

    @PostMapping(value = "/rent-vehicle/{licensePlate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Vehicle rentVehicle(@PathVariable String licensePlate, @RequestBody Map<String, Object> locationData, @RequestHeader("Authorization") String authHeader) {

        try {
            // Step 0: Authentication - Only verified renters can rent vehicles
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            if (!currentUser.isRenter()) {
                throw new IllegalArgumentException("Only renters can rent vehicles. Current role: " + currentUser.getRole());
            }

            if (!currentUser.isVerified()) {
                throw new IllegalArgumentException("Renter account must be verified before renting vehicles.");
            }

            // Step 1: Validate input
            if (licensePlate == null || licensePlate.trim().isEmpty()) {
                throw new IllegalArgumentException("License plate cannot be empty");
            }

            // Extract and validate location data
            String pickupLocation = (String) locationData.get("pickupLocation");
            String dropLocation = (String) locationData.get("dropLocation");

            if (pickupLocation == null || pickupLocation.trim().isEmpty()) {
                throw new IllegalArgumentException("Pickup location is required");
            }

            if (dropLocation == null || dropLocation.trim().isEmpty()) {
                throw new IllegalArgumentException("Drop location is required");
            }

            // Step 2: Find the vehicle by license plate
            List<Vehicle> vehicles = vehicleService.findByLicensePlate(licensePlate);

            if (vehicles.isEmpty()) {
                throw new IllegalArgumentException("Vehicle not found with license plate: " + licensePlate);
            }

            Vehicle vehicle = vehicles.get(0);

            // Step 3: Validate vehicle availability
            if (!vehicle.isVerified()) {
                throw new IllegalStateException("Vehicle is not verified and cannot be rented");
            }

            if (!"FREE".equals(vehicle.getStatus())) {
                throw new IllegalStateException("Vehicle is not available for rent. Current status: " + vehicle.getStatus());
            }

            // Step 4: Check for existing active trips
            Trip activeTrip = tripService.findActiveTrip(vehicle.getId());
            if (activeTrip != null) {
                throw new IllegalStateException("Vehicle already has an active rental trip");
            }

            // Step 5: Calculate pricing for the rental
            String renterId = currentUser.getUserId();
            Long startTime = java.time.Instant.now().toEpochMilli();
            Long endTime = startTime + (24 * 60 * 60 * 1000); // 24 hours default

            // Create pricing request
            PricingRequest pricingRequest = new PricingRequest();
            pricingRequest.setVehicleId(vehicle.getId());
            pricingRequest.setVehicleType(vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "STANDARD");
            pricingRequest.setPlannedStartTime(startTime);
            pricingRequest.setPlannedEndTime(endTime);

            // Calculate pricing
            PricingCalculation pricing = pricingService.calculatePricing(pricingRequest);

            // Step 6: Create Location objects from location names
            Location pickup = createLocationFromName(pickupLocation);
            Location drop = createLocationFromName(dropLocation);

            // Create trip record with pricing and locations
            Trip trip = tripService.createTrip(renterId, vehicle.getId(), vehicle.getOwnerId(), startTime, endTime);
            trip.setPickUpLocation(pickup);
            trip.setDropLocation(drop);

            // Set pricing details in trip
            trip.setTotalAmount(pricing.getTotalAmount());
            trip.setSecurityDeposit(pricing.getSecurityDeposit());
            trip = tripService.saveTrip(trip);

            // Step 7: Update vehicle status to rented
            vehicle.setStatus("RENTED");
            Vehicle rentedVehicle = vehicleService.saveVehicle(vehicle);

            // Step 8: Log rental transaction with pricing
            System.out.println("Vehicle rented successfully: " + rentedVehicle.getLicensePlate() +
                    " by renter " + currentUser.getName() + " (" + renterId +
                    "). Trip ID: " + trip.getId() +
                    ". Total: SGD$" + pricing.getTotalAmount() +
                    ", Deposit: SGD$" + pricing.getSecurityDeposit());

            // TODO: Process payment integration (Stripe/PayPal)
            // TODO: Send notification to host about rental
            // TODO: Send confirmation to renter
            // TODO: Create insurance policy for the trip

            return rentedVehicle;

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Vehicle rental validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during vehicle rental: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to rent vehicle. Please try again.", e);
        }
    }

    //Trip Management Endpoints

    @GetMapping(value = "/trip/{tripId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Trip getTripById(@PathVariable String tripId) {

        try {
            // Step 1: Validate input
            if (tripId == null || tripId.trim().isEmpty()) {
                throw new IllegalArgumentException("Trip ID cannot be empty");
            }

            // Step 2: Find the trip
            Trip trip = tripService.findById(tripId);

            if (trip == null) {
                throw new IllegalArgumentException("Trip not found with ID: " + tripId);
            }

            // Step 3: Log access for audit purposes
            System.out.println("Trip details accessed: " + tripId +
                    " | Status: " + trip.getStatus() +
                    " | Renter: " + trip.getRenterId());

            // TODO: Add authorization check - ensure user can access this trip
            // TODO: Add trip access logging for security audit
            // TODO: Consider returning different detail levels based on user role

            return trip;

        } catch (IllegalArgumentException e) {
            System.err.println("Trip retrieval validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during trip retrieval: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve trip details. Please try again.", e);
        }
    }

    @PostMapping(value = "/trip/{tripId}/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Trip startTrip(@PathVariable String tripId,
                          @RequestBody TripStartRequest request,
                          @RequestHeader("Authorization") String authHeader) {
        try {
            // Step 0: Authentication
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            // Step 1: Get and validate trip
            Trip trip = tripService.findById(tripId);
            if (trip == null) {
                throw new IllegalArgumentException("Trip not found with ID: " + tripId);
            }

            // Step 2: Authorization - only renter can start their own trip
            if (!trip.getRenterId().equals(currentUser.getUserId())) {
                throw new IllegalArgumentException("You can only start your own trips");
            }

            // Step 3: Validate trip status
            if (!"CONFIRMED".equals(trip.getStatus()) && !"PENDING".equals(trip.getStatus())) {
                throw new IllegalStateException("Trip must be confirmed to start. Current status: " + trip.getStatus());
            }

            // Step 4: Validate odometer reading
            if (request.getStartOdometerReading() == null || request.getStartOdometerReading() <= 0) {
                throw new IllegalArgumentException("Valid starting odometer reading is required");
            }

            // Step 5: Start the trip using service
            Trip startedTrip = tripService.startTrip(tripId, request.getStartOdometerReading());

            // Step 6: Add any additional notes or issue reports
            if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
                startedTrip.setSpecialInstructions(
                        (startedTrip.getSpecialInstructions() != null ? startedTrip.getSpecialInstructions() + "\n" : "") +
                                "Start Notes: " + request.getNotes()
                );
            }

            if (request.getHasVehicleIssues()) {
                String issueNote = "VEHICLE ISSUES REPORTED AT START: " +
                        (request.getIssueDescription() != null ? request.getIssueDescription() : "Not specified");
                startedTrip.setSpecialInstructions(
                        (startedTrip.getSpecialInstructions() != null ? startedTrip.getSpecialInstructions() + "\n" : "") +
                                issueNote
                );
                System.out.println("WARNING: Vehicle issues reported for trip " + tripId + ": " + request.getIssueDescription());
            }

            // Step 7: Save any updates
            Trip finalTrip = tripService.saveTrip(startedTrip);

            // Step 8: Log trip start
            System.out.println("Trip started successfully: " + tripId +
                    " by renter " + currentUser.getName() +
                    " | Odometer: " + request.getStartOdometerReading());

            // TODO: Send notification to host about trip start
            // TODO: Update vehicle location tracking
            // TODO: Start insurance coverage

            return finalTrip;

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Trip start validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during trip start: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to start trip. Please try again.", e);
        }
    }

    @PostMapping(value = "/trip/{tripId}/complete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Trip completeTrip(@PathVariable String tripId,
                             @RequestBody TripCompleteRequest request,
                             @RequestHeader("Authorization") String authHeader) {
        try {
            // Step 0: Authentication
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            // Step 1: Get and validate trip
            Trip trip = tripService.findById(tripId);
            if (trip == null) {
                throw new IllegalArgumentException("Trip not found with ID: " + tripId);
            }

            // Step 2: Authorization - only renter can complete their own trip
            if (!trip.getRenterId().equals(currentUser.getUserId())) {
                throw new IllegalArgumentException("You can only complete your own trips");
            }

            // Step 3: Validate ending odometer reading
            if (request.getEndOdometerReading() == null || request.getEndOdometerReading() <= 0) {
                throw new IllegalArgumentException("Valid ending odometer reading is required");
            }

            if (trip.getStartOdometerReading() != null &&
                    request.getEndOdometerReading() < trip.getStartOdometerReading()) {
                throw new IllegalArgumentException("Ending odometer reading cannot be less than starting reading");
            }

            // Step 4: Complete the trip using service
            Trip completedTrip = tripService.completeTrip(tripId, request.getEndOdometerReading());

            // Step 5: Add completion notes and issue reports
            if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
                completedTrip.setSpecialInstructions(
                        (completedTrip.getSpecialInstructions() != null ? completedTrip.getSpecialInstructions() + "\n" : "") +
                                "Completion Notes: " + request.getNotes()
                );
            }

            if (request.getHasVehicleIssues()) {
                String issueNote = "VEHICLE ISSUES REPORTED AT RETURN: " +
                        (request.getIssueDescription() != null ? request.getIssueDescription() : "Not specified");
                completedTrip.setSpecialInstructions(
                        (completedTrip.getSpecialInstructions() != null ? completedTrip.getSpecialInstructions() + "\n" : "") +
                                issueNote
                );
                System.out.println("WARNING: Vehicle issues reported at trip completion " + tripId + ": " + request.getIssueDescription());
            }

            // Step 6: Record additional completion details
            if (request.getFuelLevel() != null) {
                // In a real system, we'd have a fuel level field in Trip model
                String fuelNote = "Fuel level at return: " + (request.getFuelLevel() * 100) + "%";
                completedTrip.setSpecialInstructions(
                        (completedTrip.getSpecialInstructions() != null ? completedTrip.getSpecialInstructions() + "\n" : "") +
                                fuelNote
                );
            }

            if (request.getRequiresCleaning()) {
                String cleaningNote = "Vehicle requires cleaning - additional fee may apply";
                completedTrip.setSpecialInstructions(
                        (completedTrip.getSpecialInstructions() != null ? completedTrip.getSpecialInstructions() + "\n" : "") +
                                cleaningNote
                );
            }

            // Step 7: Update vehicle status back to available
            List<Vehicle> vehicles = vehicleService.findByLicensePlate(completedTrip.getVehicleId()); // Using workaround
            if (!vehicles.isEmpty()) {
                Vehicle vehicle = vehicles.get(0);
                vehicle.setStatus("FREE");
                vehicleService.saveVehicle(vehicle);
            }

            // Step 8: Save final trip
            Trip finalTrip = tripService.saveTrip(completedTrip);

            // Step 9: Calculate distance traveled
            Long distanceTraveled = finalTrip.getDistanceTraveled();

            // Step 10: Log trip completion
            System.out.println("Trip completed successfully: " + tripId +
                    " by renter " + currentUser.getName() +
                    " | Distance: " + (distanceTraveled != null ? distanceTraveled + "km" : "N/A") +
                    " | Total: SGD$" + finalTrip.getTotalAmount());

            // TODO: Process final payment and release security deposit
            // TODO: Send completion notifications to both parties
            // TODO: Trigger review/rating requests

            return finalTrip;

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Trip completion validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during trip completion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to complete trip. Please try again.", e);
        }
    }

    @PostMapping(value = "/trip/{tripId}/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Trip cancelTrip(@PathVariable String tripId,
                           @RequestBody TripCancelRequest request,
                           @RequestHeader("Authorization") String authHeader) {
        try {
            // Step 0: Authentication
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            // Step 1: Get and validate trip
            Trip trip = tripService.findById(tripId);
            if (trip == null) {
                throw new IllegalArgumentException("Trip not found with ID: " + tripId);
            }

            // Step 2: Authorization check
            boolean canCancel = false;
            String cancellationReason = request.getReason();

            if (currentUser.getUserId().equals(trip.getRenterId())) {
                // Renter can cancel their own trips
                canCancel = true;
            } else if (currentUser.getUserId().equals(trip.getOwnerId())) {
                // Host can cancel in certain circumstances
                canCancel = true;
                cancellationReason = "Cancelled by host: " + (request.getReason() != null ? request.getReason() : "Vehicle unavailable");
            } else if (currentUser.isAdmin()) {
                // Admin can cancel any trip
                canCancel = true;
                cancellationReason = "Cancelled by admin: " + (request.getReason() != null ? request.getReason() : "Policy violation");
            }

            if (!canCancel) {
                throw new IllegalArgumentException("You are not authorized to cancel this trip");
            }

            // Step 3: Validate cancellation reason
            if (cancellationReason == null || cancellationReason.trim().isEmpty()) {
                throw new IllegalArgumentException("Cancellation reason is required");
            }

            // Step 4: Cancel the trip using service
            Trip cancelledTrip = tripService.cancelTrip(tripId, cancellationReason);

            // Step 5: Add additional cancellation details
            if (request.getAdditionalNotes() != null && !request.getAdditionalNotes().trim().isEmpty()) {
                cancelledTrip.setSpecialInstructions(
                        (cancelledTrip.getSpecialInstructions() != null ? cancelledTrip.getSpecialInstructions() + "\n" : "") +
                                "Cancellation Notes: " + request.getAdditionalNotes()
                );
            }

            // Step 6: Update vehicle status back to available if needed
            List<Vehicle> vehicles = vehicleService.findByLicensePlate(trip.getVehicleId());
            if (!vehicles.isEmpty()) {
                Vehicle vehicle = vehicles.get(0);
                if ("RENTED".equals(vehicle.getStatus())) {
                    vehicle.setStatus("FREE");
                    vehicleService.saveVehicle(vehicle);
                }
            }

            // Step 7: Set cancellation metadata
            String cancelledBy = currentUser.isAdmin() ? "ADMIN" :
                    currentUser.getUserId().equals(trip.getRenterId()) ? "RENTER" : "HOST";

            cancelledTrip.setSpecialInstructions(
                    (cancelledTrip.getSpecialInstructions() != null ? cancelledTrip.getSpecialInstructions() + "\n" : "") +
                            "Cancelled by: " + cancelledBy + " (" + currentUser.getName() + ")"
            );

            // Step 8: Save final trip
            Trip finalTrip = tripService.saveTrip(cancelledTrip);

            // Step 9: Log cancellation
            System.out.println("Trip cancelled: " + tripId +
                    " by " + cancelledBy + " (" + currentUser.getName() + ")" +
                    " | Reason: " + cancellationReason);

            // TODO: Calculate and process cancellation fees
            // TODO: Process refunds if applicable
            // TODO: Send cancellation notifications
            // TODO: Update analytics/metrics

            return finalTrip;

        } catch (IllegalArgumentException e) {
            System.err.println("Trip cancellation validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during trip cancellation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to cancel trip. Please try again.", e);
        }
    }

    @PostMapping(value = "/trip/{tripId}/rating")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Trip submitTripRating(@PathVariable String tripId,
                                 @RequestBody TripRatingRequest request,
                                 @RequestHeader("Authorization") String authHeader) {
        try {
            // Step 0: Authentication
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            // Step 1: Get and validate trip
            Trip trip = tripService.findById(tripId);
            if (trip == null) {
                throw new IllegalArgumentException("Trip not found with ID: " + tripId);
            }

            // Step 2: Validate trip is completed
            if (!"COMPLETED".equals(trip.getStatus())) {
                throw new IllegalStateException("Can only rate completed trips. Current status: " + trip.getStatus());
            }

            // Step 3: Determine who is rating and validate authorization
            boolean isRenter = currentUser.getUserId().equals(trip.getRenterId());
            boolean isHost = currentUser.getUserId().equals(trip.getOwnerId());

            if (!isRenter && !isHost) {
                throw new IllegalArgumentException("You can only rate trips you participated in");
            }

            // Step 4: Validate rating values
            Integer rating;
            String comments;

            if (isRenter) {
                // Renter is submitting rating
                if (request.getRenterRating() == null || request.getRenterRating() < 1 || request.getRenterRating() > 5) {
                    throw new IllegalArgumentException("Renter rating must be between 1 and 5 stars");
                }
                rating = request.getRenterRating();
                comments = request.getRenterComments();
            } else {
                // Host is submitting rating
                if (request.getOwnerRating() == null || request.getOwnerRating() < 1 || request.getOwnerRating() > 5) {
                    throw new IllegalArgumentException("Owner rating must be between 1 and 5 stars");
                }
                rating = request.getOwnerRating();
                comments = request.getOwnerComments();
            }

            // Step 5: Submit rating using service
            Trip ratedTrip;
            if (isRenter) {
                // Renter rating the host/vehicle
                ratedTrip = tripService.addRatingsAndReviews(tripId, rating, null, comments, null);
                System.out.println("Renter rating submitted for trip " + tripId + ": " + rating + " stars");
            } else {
                // Host rating the renter
                ratedTrip = tripService.addRatingsAndReviews(tripId, null, rating, null, comments);
                System.out.println("Host rating submitted for trip " + tripId + ": " + rating + " stars");
            }

            // Step 6: Log the rating submission
            String raterType = isRenter ? "RENTER" : "HOST";
            System.out.println("Rating submitted for trip " + tripId +
                    " by " + raterType + " (" + currentUser.getName() + ")" +
                    " | Rating: " + rating + "/5" +
                    (comments != null ? " | Comment: " + comments : ""));

            // TODO: Update user reputation scores
            // TODO: Send notification to the other party
            // TODO: Trigger platform quality metrics update

            return ratedTrip;

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Trip rating validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error during trip rating: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to submit rating. Please try again.", e);
        }
    }

    @GetMapping(value = "/trips/active")
    @Produces(MediaType.APPLICATION_JSON)
    public Trip getActiveTrip(@RequestHeader("Authorization") String authHeader) {
        try {
            // Step 0: Authentication
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            // Step 1: Find active trip for user
            List<Trip> userTrips;
            if (currentUser.isRenter()) {
                userTrips = tripService.findTripsByRenter(currentUser.getUserId());
            } else if (currentUser.isHost()) {
                userTrips = tripService.findTripsByOwner(currentUser.getUserId());
            } else {
                return null; // Admins don't have personal active trips
            }

            // Step 2: Find the active trip (IN_PROGRESS status)
            Trip activeTrip = userTrips.stream()
                    .filter(trip -> "IN_PROGRESS".equals(trip.getStatus()))
                    .findFirst()
                    .orElse(null);

            if (activeTrip != null) {
                System.out.println("Active trip found for user " + currentUser.getName() + ": " + activeTrip.getId());
            }

            return activeTrip;

        } catch (Exception e) {
            System.err.println("Error getting active trip: " + e.getMessage());
            throw new RuntimeException("Failed to get active trip", e);
        }
    }

    @GetMapping(value = "/trips/my-trips")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Trip> getUserTrips(@RequestHeader("Authorization") String authHeader) {
        try {
            // Step 0: Authentication
            AuthUser currentUser = getAuthenticatedUser(authHeader);
            if (currentUser == null) {
                throw new IllegalArgumentException("Authentication required. Please login.");
            }

            // Step 1: Find all trips for user
            List<Trip> userTrips;
            if (currentUser.isRenter()) {
                userTrips = tripService.findTripsByRenter(currentUser.getUserId());
                System.out.println("Found " + userTrips.size() + " trips for renter: " + currentUser.getName());
            } else if (currentUser.isHost()) {
                userTrips = tripService.findTripsByOwner(currentUser.getUserId());
                System.out.println("Found " + userTrips.size() + " trips for host: " + currentUser.getName());
            } else {
                return new ArrayList<>(); // Admins don't have personal trips
            }

            return userTrips;

        } catch (Exception e) {
            System.err.println("Error getting user trips: " + e.getMessage());
            throw new RuntimeException("Failed to get user trips", e);
        }
    }

    // Helper Methods

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring("Bearer ".length());
        }
        return null;
    }

    private AuthUser getAuthenticatedUser(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return null;
        }
        return authService.validateToken(token);
    }

    private boolean isValidVehicleType(String vehicleType) {
        return vehicleType != null &&
                ("ECONOMY".equalsIgnoreCase(vehicleType) ||
                        "STANDARD".equalsIgnoreCase(vehicleType) ||
                        "PREMIUM".equalsIgnoreCase(vehicleType));
    }

    private Location createLocationFromName(String locationName) {
        Location location = new Location();

        switch (locationName) {
            case "Connaught Place":
                location.setLat(28.6315);
                location.setLon(77.2167);
                break;
            case "India Gate":
                location.setLat(28.6129);
                location.setLon(77.2295);
                break;
            case "Red Fort":
                location.setLat(28.6562);
                location.setLon(77.2410);
                break;
            case "Karol Bagh":
                location.setLat(28.6519);
                location.setLon(77.1909);
                break;
            case "Saket":
                location.setLat(28.5245);
                location.setLon(77.2066);
                break;
            case "Gurgaon Cyber City":
                location.setLat(28.4950);
                location.setLon(77.0890);
                break;
            default:
                // Default to Connaught Place if unknown location
                location.setLat(28.6315);
                location.setLon(77.2167);
                break;
        }

        return location;
    }
}
