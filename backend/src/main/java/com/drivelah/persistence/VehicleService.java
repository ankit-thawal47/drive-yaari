package com.drivelah.persistence;

import com.drivelah.client.MongoRepo;
import com.drivelah.model.Vehicle;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final MongoRepo<Vehicle> vehicleRepository;

    public VehicleService(MongoRepo<Vehicle> vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll(Vehicle.class);
    }

    public List<Vehicle> getVerifiedVehicles() {
        Query query = new Query(Criteria.where(Vehicle.FM.IS_VERIFIED).is(true));
        return vehicleRepository.find(query, Vehicle.class);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        //add checks and validation
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> findByLicensePlate(String licensePlate) {
        Query query = new Query(Criteria.where(Vehicle.FM.LICENSE_PLATE).is(licensePlate));
        return vehicleRepository.find(query, Vehicle.class);
    }

    public List<Vehicle> findByOwnerId(String ownerId) {
        Query query = new Query(Criteria.where(Vehicle.FM.OWNER_ID).is(ownerId));
        return vehicleRepository.find(query, Vehicle.class);
    }

    public long getVehicleCount() {
        return vehicleRepository.count(Vehicle.class);
    }
}
