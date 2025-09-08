package com.drivelah.persistence;

import com.drivelah.client.MongoRepo;
import com.drivelah.model.Host;
import com.drivelah.model.Renter;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final MongoRepo<Host> hostService;
    private final MongoRepo<Renter> renterMongoRepo;

    public UserService(MongoRepo<Host> hostService, MongoRepo<Renter> renterMongoRepo) {
        this.hostService = hostService;
        this.renterMongoRepo = renterMongoRepo;
    }



}
