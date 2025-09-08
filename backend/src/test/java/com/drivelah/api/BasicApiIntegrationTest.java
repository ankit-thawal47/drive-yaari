package com.drivelah.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String createUrl(String path) {
        return "http://localhost:" + port + "/api" + path;
    }

    @Test
    public void testGetVehiclesEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(createUrl("/get-vehicles"), String.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUserRegistration() {
        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("name", "Test User");
        registerRequest.put("email", "testuser@example.com");
        registerRequest.put("password", "password123");
        registerRequest.put("userType", "RENTER");

        ResponseEntity<String> response = restTemplate.postForEntity(
                createUrl("/auth/register"), registerRequest, String.class);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("success"));
    }

    @Test
    public void testUserLogin() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "testlogin@example.com");
        loginRequest.put("password", "password123");

        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("name", "Test Login User");
        registerRequest.put("email", "testlogin@example.com");
        registerRequest.put("password", "password123");
        registerRequest.put("userType", "HOST");

        restTemplate.postForEntity(createUrl("/auth/register"), registerRequest, String.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
                createUrl("/auth/login"), loginRequest, String.class);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("token"));
    }

    @Test
    public void testVehicleRegistration() {
        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("name", "Test Host");
        registerRequest.put("email", "testhost@example.com");
        registerRequest.put("password", "password123");
        registerRequest.put("userType", "HOST");

        restTemplate.postForEntity(createUrl("/auth/register"), registerRequest, String.class);

        Map<String, Object> vehicleRequest = new HashMap<>();
        vehicleRequest.put("licensePlate", "TEST123");
        vehicleRequest.put("make", "Toyota");
        vehicleRequest.put("model", "Camry");
        vehicleRequest.put("year", 2020);
        vehicleRequest.put("color", "White");
        vehicleRequest.put("vehicleType", "STANDARD");
        vehicleRequest.put("transmission", "AUTO");
        vehicleRequest.put("seatingCapacity", 5);
        vehicleRequest.put("features", "GPS, Bluetooth");
        vehicleRequest.put("description", "Test vehicle");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mock-jwt-token-testhost@example.com");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(vehicleRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createUrl("/register-vehicle"), HttpMethod.POST, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("success"));
    }

    @Test
    public void testPricingCalculation() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                createUrl("/pricing/STANDARD/2/50"), String.class);
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("totalAmount"));
        assertTrue(response.getBody().contains("46.5"));
    }

    @Test
    public void testRentVehicleFlow() {
        Map<String, Object> hostRegister = new HashMap<>();
        hostRegister.put("name", "Test Host");
        hostRegister.put("email", "renthost@example.com");
        hostRegister.put("password", "password123");
        hostRegister.put("userType", "HOST");

        restTemplate.postForEntity(createUrl("/auth/register"), hostRegister, String.class);

        Map<String, Object> renterRegister = new HashMap<>();
        renterRegister.put("name", "Test Renter");
        renterRegister.put("email", "renter@example.com");
        renterRegister.put("password", "password123");
        renterRegister.put("userType", "RENTER");

        restTemplate.postForEntity(createUrl("/auth/register"), renterRegister, String.class);

        Map<String, Object> vehicleRequest = new HashMap<>();
        vehicleRequest.put("licensePlate", "RENT123");
        vehicleRequest.put("make", "Honda");
        vehicleRequest.put("model", "Civic");
        vehicleRequest.put("year", 2021);
        vehicleRequest.put("color", "Blue");
        vehicleRequest.put("vehicleType", "ECONOMY");
        vehicleRequest.put("transmission", "AUTO");
        vehicleRequest.put("seatingCapacity", 5);

        HttpHeaders hostHeaders = new HttpHeaders();
        hostHeaders.set("Authorization", "Bearer mock-jwt-token-renthost@example.com");
        HttpEntity<Map<String, Object>> vehicleEntity = new HttpEntity<>(vehicleRequest, hostHeaders);

        restTemplate.exchange(createUrl("/register-vehicle"), HttpMethod.POST, vehicleEntity, String.class);

        HttpHeaders renterHeaders = new HttpHeaders();
        renterHeaders.set("Authorization", "Bearer mock-jwt-token-renter@example.com");
        HttpEntity<String> renterEntity = new HttpEntity<>("", renterHeaders);

        ResponseEntity<String> rentResponse = restTemplate.exchange(
                createUrl("/rent-vehicle/RENT123"), HttpMethod.POST, renterEntity, String.class);

        assertTrue(rentResponse.getStatusCodeValue() == 200 || rentResponse.getStatusCodeValue() == 400);
        assertNotNull(rentResponse.getBody());
    }

    @Test
    public void testGetActiveTrip() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mock-jwt-token-testuser@example.com");
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createUrl("/trips/active"), HttpMethod.GET, entity, String.class);

        assertTrue(response.getStatusCodeValue() == 200 || response.getStatusCodeValue() == 404);
        assertNotNull(response.getBody());
    }
}