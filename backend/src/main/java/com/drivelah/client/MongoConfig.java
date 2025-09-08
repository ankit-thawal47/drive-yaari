package com.drivelah.client;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * MongoDB Configuration
 * 
 * This configuration class sets up MongoDB connection using Spring Boot's auto-configuration.
 * It creates MongoTemplate bean for database operations.
 * 
 * Configuration properties in application.yml:
 * spring:
 *   data:
 *     mongodb:
 *       host: localhost
 *       port: 27017
 *       database: drivelah
 * 
 * For production, you can also use connection string:
 * spring:
 *   data:
 *     mongodb:
 *       uri: mongodb://username:password@host:port/database
 */
@Configuration
public class MongoConfig {
    
    @Value("${spring.data.mongodb.host:localhost}")
    private String mongoHost;
    
    @Value("${spring.data.mongodb.port:27017}")
    private String mongoPort;
    
    @Value("${spring.data.mongodb.database:drivelah}")
    private String mongoDatabase;
    
    /**
     * Creates MongoClient bean
     * Spring Boot auto-configuration will handle this, but you can customize here if needed
     */
    @Bean
    public MongoClient mongoClient() {
        String connectionString = String.format("mongodb://%s:%s/%s", 
            mongoHost, mongoPort, mongoDatabase);
        
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .build();
            
        return com.mongodb.client.MongoClients.create(settings);
    }
    
    /**
     * Creates MongoTemplate bean for database operations
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, mongoDatabase);
    }
}
