# MongoDB Setup and Usage Guide

## Overview
This guide explains how to set up MongoDB connection and use the generic repository pattern implemented in the Drive-Lah application.

## Prerequisites
1. MongoDB installed and running on your system
2. Spring Boot application with MongoDB dependencies

## Configuration

### 1. Application Configuration (application.yml)
```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: drivelah
```

For production with authentication:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://username:password@host:port/database
```

### 2. Dependencies (build.gradle)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```

## Architecture

### Generic MongoDB Repository
The `GenericMongoRepository<T>` class provides common database operations:

```java
@Repository
public class GenericMongoRepository<T> {
    // Provides: findAll, find, save, findById, delete, count
}
```

**Benefits:**
- Reusable for all MongoDB documents
- Reduces code duplication
- Consistent API across all services
- Easy to extend and maintain

### Vehicle Service Implementation

```java
@Service
public class VehicleService {
    private final GenericMongoRepository<Vehicle> vehicleRepository;
    
    // Methods available:
    public List<Vehicle> getAllVehicles()           // Get all vehicles
    public List<Vehicle> getVerifiedVehicles()      // Get only verified vehicles
    public Vehicle saveVehicle(Vehicle vehicle)     // Save a vehicle
    public List<Vehicle> findByLicensePlate(String) // Find by license plate
    public List<Vehicle> findByOwnerId(String)      // Find by owner
    public long getVehicleCount()                   // Count vehicles
}
```

## Usage Examples

### 1. Basic Usage in Controller
```java
@RestController
public class VehicleController {
    private final VehicleService vehicleService;
    
    @GetMapping("/vehicles")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }
    
    @GetMapping("/vehicles/verified")
    public List<Vehicle> getVerifiedVehicles() {
        return vehicleService.getVerifiedVehicles();
    }
    
    @PostMapping("/vehicles")
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleService.saveVehicle(vehicle);
    }
}
```

### 2. Custom Queries
```java
// In VehicleService, add custom methods:
public List<Vehicle> findVehiclesByDateRange(String startDate, String endDate) {
    Query query = new Query();
    query.addCriteria(Criteria.where("dateOfReg")
        .gte(startDate)
        .lte(endDate));
    return vehicleRepository.find(query, Vehicle.class);
}
```

### 3. Creating New Services
To create a service for another entity (e.g., User):

```java
@Service
public class UserService {
    private final GenericMongoRepository<User> userRepository;
    
    public UserService(GenericMongoRepository<User> userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll(User.class);
    }
    
    // Add entity-specific methods as needed
}
```

## Vehicle Model Structure
```java
@Document(collection = "vehicles")
public class Vehicle {
    @Id
    private String id;                    // MongoDB document ID
    private String licensePlate;          // Vehicle license plate
    private String ownerId;               // Owner's ID
    private String dateOfReg;             // Registration date
    private boolean isVerified = false;   // Verification status
    
    // Getters and setters...
}
```

## Testing MongoDB Connection

### 1. Start MongoDB
```bash
# On macOS with Homebrew
brew services start mongodb-community

# On Ubuntu/Linux
sudo systemctl start mongod

# Check if running
mongo --eval "db.runCommand({ping: 1})"
```

### 2. Test the Application
```bash
cd backend
./gradlew bootRun

# Test endpoints:
curl http://localhost:8080/api/vehicles
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{"licensePlate":"ABC123","ownerId":"user1","dateOfReg":"2024-01-01"}'
```

## MongoDB Operations

### Database Operations via MongoDB Shell
```javascript
// Connect to database
use drivelah

// View all vehicles
db.vehicles.find()

// Insert test data
db.vehicles.insertOne({
    licensePlate: "TEST123",
    ownerId: "testuser",
    dateOfReg: "2024-01-01",
    isVerified: false
})

// Find verified vehicles
db.vehicles.find({isVerified: true})
```

## Troubleshooting

### Common Issues:
1. **Connection refused**: Ensure MongoDB is running
2. **Authentication failed**: Check MongoDB credentials
3. **Database not found**: Database will be created automatically when first document is inserted

### Debugging:
Add to application.yml for debug logging:
```yaml
logging:
  level:
    org.springframework.data.mongodb: DEBUG
```

## Best Practices

1. **Index your collections** for better performance:
   ```javascript
   db.vehicles.createIndex({licensePlate: 1})
   db.vehicles.createIndex({ownerId: 1})
   ```

2. **Use appropriate data types** in your models
3. **Handle exceptions** in your service methods
4. **Validate input data** before saving to database
5. **Use pagination** for large result sets

## Extending the Generic Repository

To add more generic methods:
```java
public class GenericMongoRepository<T> {
    
    // Add new generic method
    public List<T> findByField(String fieldName, Object value, Class<T> entityClass) {
        Query query = new Query(Criteria.where(fieldName).is(value));
        return mongoTemplate.find(query, entityClass);
    }
    
    // Pagination support
    public List<T> findWithPagination(int page, int size, Class<T> entityClass) {
        Query query = new Query();
        query.skip(page * size).limit(size);
        return mongoTemplate.find(query, entityClass);
    }
}