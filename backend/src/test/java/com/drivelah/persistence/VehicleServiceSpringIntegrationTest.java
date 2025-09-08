package com.drivelah.persistence;

import com.drivelah.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ComponentScan(basePackages = {"com.drivelah.persistence", "com.drivelah.client"})
public class VehicleServiceSpringIntegrationTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setUp() {
        // Clean up database before each test
//        mongoTemplate.dropCollection(Vehicle.class);

        // Setup test environment
        System.setProperty("spring.data.mongodb.host", "localhost");
        System.setProperty("spring.data.mongodb.port", "27017");
        System.setProperty("spring.data.mongodb.database", "drivelah_test");
    }

    @Test
    public void testCreateAndRetrieveSampleVehicles() {
        // Given - Create sample vehicles
        Vehicle vehicle1 = createSampleVehicle("ABC123", "owner1", false);
        Vehicle vehicle2 = createSampleVehicle("XYZ789", "owner2", true);
        Vehicle vehicle3 = createSampleVehicle("DEF456", "owner1", true);

        // When - Save vehicles using the service
        Vehicle savedVehicle1 = vehicleService.saveVehicle(vehicle1);
        Vehicle savedVehicle2 = vehicleService.saveVehicle(vehicle2);
        Vehicle savedVehicle3 = vehicleService.saveVehicle(vehicle3);

        // Then - Verify vehicles are saved with IDs
        assertNotNull(savedVehicle1.getId(), "Vehicle 1 should have an ID after saving");
        assertNotNull(savedVehicle2.getId(), "Vehicle 2 should have an ID after saving");
        assertNotNull(savedVehicle3.getId(), "Vehicle 3 should have an ID after saving");

        // And - Verify all vehicles can be retrieved
        List<Vehicle> allVehicles = vehicleService.getAllVehicles();
        assertEquals(3, allVehicles.size(), "Should retrieve all 3 saved vehicles");

        // Verify vehicle details are preserved
        assertTrue(containsLicensePlate(allVehicles, "ABC123"), "Should contain vehicle ABC123");
        assertTrue(containsLicensePlate(allVehicles, "XYZ789"), "Should contain vehicle XYZ789");
        assertTrue(containsLicensePlate(allVehicles, "DEF456"), "Should contain vehicle DEF456");
    }

    @Test
    public void testGetVerifiedVehiclesOnly() {
        // Given - Create mix of verified and unverified vehicles
        Vehicle unverifiedVehicle = createSampleVehicle("UNVER123", "owner1", false);
        Vehicle verifiedVehicle1 = createSampleVehicle("VER456", "owner2", true);
        Vehicle verifiedVehicle2 = createSampleVehicle("VER789", "owner3", true);

        vehicleService.saveVehicle(unverifiedVehicle);
        vehicleService.saveVehicle(verifiedVehicle1);
        vehicleService.saveVehicle(verifiedVehicle2);

        // When - Get only verified vehicles
        List<Vehicle> verifiedVehicles = vehicleService.getVerifiedVehicles();

        // Then - Should return only verified vehicles
        assertEquals(2, verifiedVehicles.size(), "Should return exactly 2 verified vehicles");

        for (Vehicle vehicle : verifiedVehicles) {
            assertTrue(vehicle.isVerified(), "All returned vehicles should be verified");
        }

        // Verify specific vehicles are included
        assertTrue(containsLicensePlate(verifiedVehicles, "VER456"), "Should contain verified vehicle VER456");
        assertTrue(containsLicensePlate(verifiedVehicles, "VER789"), "Should contain verified vehicle VER789");

        // Verify unverified vehicle is not included
        assertFalse(containsLicensePlate(verifiedVehicles, "UNVER123"), "Should not contain unverified vehicle UNVER123");
    }

    @Test
    public void testFindVehicleByLicensePlate() {
        // Given - Create sample vehicle with unique license plate
        String uniqueLicensePlate = "UNIQUE123";
        Vehicle vehicle = createSampleVehicle(uniqueLicensePlate, "owner1", true);
        vehicleService.saveVehicle(vehicle);

        // When - Search by license plate
        List<Vehicle> foundVehicles = vehicleService.findByLicensePlate(uniqueLicensePlate);

        // Then - Should find exactly one vehicle
        assertEquals(1, foundVehicles.size(), "Should find exactly one vehicle with unique license plate");
        assertEquals(uniqueLicensePlate, foundVehicles.get(0).getLicensePlate(), "Should match the searched license plate");
        assertEquals("owner1", foundVehicles.get(0).getOwnerId(), "Should preserve owner information");
        assertTrue(foundVehicles.get(0).isVerified(), "Should preserve verification status");
    }

    @Test
    public void testFindVehiclesByOwnerId() {
        // Given - Create multiple vehicles for same owner
        String ownerId = "multiOwner";
        Vehicle vehicle1 = createSampleVehicle("OWNER1", ownerId, true);
        Vehicle vehicle2 = createSampleVehicle("OWNER2", ownerId, false);
        Vehicle vehicle3 = createSampleVehicle("OTHER1", "differentOwner", true);

        vehicleService.saveVehicle(vehicle1);
        vehicleService.saveVehicle(vehicle2);
        vehicleService.saveVehicle(vehicle3);

        // When - Search by owner ID
        List<Vehicle> ownerVehicles = vehicleService.findByOwnerId(ownerId);

        // Then - Should find vehicles for that owner only
        assertEquals(2, ownerVehicles.size(), "Should find exactly 2 vehicles for the owner");

        for (Vehicle vehicle : ownerVehicles) {
            assertEquals(ownerId, vehicle.getOwnerId(), "All returned vehicles should belong to the specified owner");
        }

        // Verify both vehicles are included
        assertTrue(containsLicensePlate(ownerVehicles, "OWNER1"), "Should contain vehicle OWNER1");
        assertTrue(containsLicensePlate(ownerVehicles, "OWNER2"), "Should contain vehicle OWNER2");
    }

    @Test
    public void testVehicleCount() {
        // Given - Start with empty database
        assertEquals(0, vehicleService.getVehicleCount(), "Should start with empty database");

        // When - Add sample vehicles
        vehicleService.saveVehicle(createSampleVehicle("COUNT1", "owner1", true));
        vehicleService.saveVehicle(createSampleVehicle("COUNT2", "owner2", false));
        vehicleService.saveVehicle(createSampleVehicle("COUNT3", "owner3", true));

        // Then - Count should be correct
        assertEquals(3, vehicleService.getVehicleCount(), "Count should match number of saved vehicles");
    }

    @Test
    public void testEmptyDatabaseOperations() {
        // Given - Empty database (cleaned in setUp)

        // When - Try to get all vehicles
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        // Then - Should return empty list
        assertNotNull(vehicles, "Should return a list, not null");
        assertTrue(vehicles.isEmpty(), "Should return empty list for empty database");
        assertEquals(0, vehicleService.getVehicleCount(), "Count should be zero for empty database");
    }

    @Test
    public void testSearchNonExistentVehicle() {
        // Given - Empty database with one vehicle
        vehicleService.saveVehicle(createSampleVehicle("EXISTS", "owner1", true));

        // When - Search for non-existent license plate
        List<Vehicle> foundVehicles = vehicleService.findByLicensePlate("DOESNOTEXIST");

        // Then - Should return empty list
        assertNotNull(foundVehicles, "Should return a list, not null");
        assertTrue(foundVehicles.isEmpty(), "Should return empty list for non-existent vehicle");
    }

    /**
     * Helper method to create sample vehicle with test data
     *
     * @param licensePlate The license plate for the vehicle
     * @param ownerId      The owner ID
     * @param isVerified   Whether the vehicle is verified
     * @return A new Vehicle instance with sample data
     */
    /**
     * Helper method to create sample vehicle with test data
     * Updated to match new Vehicle model structure with epoch timestamp and status
     * 
     * @param licensePlate The license plate for the vehicle
     * @param ownerId      The owner ID
     * @param isVerified   Whether the vehicle is verified
     * @return A new Vehicle instance with sample data
     */
    private Vehicle createSampleVehicle(String licensePlate, String ownerId, boolean isVerified) {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setOwnerId(ownerId);
        vehicle.setDateOfRegEpoch(Instant.now().toEpochMilli()); // Updated to use epoch timestamp
        vehicle.setVerified(isVerified);
        vehicle.setStatus(isVerified ? "FREE" : "RESTING"); // Set appropriate status
        return vehicle;
    }

    /**
     * Helper method to check if a list contains a vehicle with specific license plate
     */
    private boolean containsLicensePlate(List<Vehicle> vehicles, String licensePlate) {
        return vehicles.stream().anyMatch(v -> licensePlate.equals(v.getLicensePlate()));
    }
}