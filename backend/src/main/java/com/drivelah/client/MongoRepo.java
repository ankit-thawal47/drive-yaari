package com.drivelah.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Generic MongoDB Repository for CRUD operations in Drive-Lah P2P Car Rental Service
 * 
 * This repository provides common database operations for any MongoDB document in the car rental system.
 * It supports operations for vehicles, users, bookings, insurance claims, and other entities.
 * 
 * Key Features:
 * - Generic type support for all entities
 * - Custom query support for complex searches
 * - Optimized for car rental business logic
 * - Thread-safe operations
 * 
 * @param <T> The document type (Vehicle, User, Booking, etc.)
 * 
 * @author Drive-Lah Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public class MongoRepo<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Find all documents of the specified type
     * 
     * Performance Note: Use with caution for large collections.
     * Consider pagination for better performance.
     * 
     * @param entityClass The class type of the document
     * @return List of all documents
     * 
     * @apiNote For large datasets, prefer findWithPagination()
     */
    public List<T> findAll(Class<T> entityClass) {
        return mongoTemplate.findAll(entityClass);
    }

    /**
     * Find documents by a custom MongoDB query
     * 
     * This method supports complex queries including:
     * - Field filtering
     * - Range queries
     * - Geospatial queries for location-based searches
     * - Regex pattern matching
     * 
     * Example Queries for Car Rental:
     * 
     * // Find available verified vehicles
     * Query query = new Query(Criteria.where(Vehicle.FM.STATUS).is("FREE")
     *                                .and(Vehicle.FM.IS_VERIFIED).is(true));
     * 
     * // Find vehicles registered within date range
     * Query dateQuery = new Query(Criteria.where(Vehicle.FM.DATE_OF_REG_EPOCH)
     *                                    .gte(startEpoch).lte(endEpoch));
     * 
     * @param query The MongoDB query with criteria
     * @param entityClass The class type of the document
     * @return List of matching documents
     */
    public List<T> find(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * Save a document to MongoDB
     * 
     * This method handles both insert and update operations:
     * - If document has no ID, it will be inserted
     * - If document has an ID, it will be updated
     * 
     * Business Logic Notes:
     * - Vehicle registration timestamps are auto-generated
     * - Audit fields should be handled at service layer
     * - Validation should occur before saving
     * 
     * @param entity The document to save
     * @return The saved document with generated/updated ID
     * 
     * @throws org.springframework.dao.DuplicateKeyException if unique constraints are violated
     */
    public T save(T entity) {
        return mongoTemplate.save(entity);
    }

    /**
     * Find a document by its unique MongoDB ObjectId
     * 
     * @param id The document ID (MongoDB ObjectId as String)
     * @param entityClass The class type of the document
     * @return The found document or null if not found
     * 
     * @apiNote Returns null if document doesn't exist. Check for null before use.
     */
    public T findById(String id, Class<T> entityClass) {
        return mongoTemplate.findById(id, entityClass);
    }

    /**
     * Delete a document from MongoDB
     * 
     * Business Rules:
     * - Vehicles with active bookings should not be deleted
     * - Soft delete may be preferred for audit trails
     * - Consider cascading deletes for related entities
     * 
     * @param entity The document to delete
     * 
     * @apiNote This performs hard delete. Consider soft delete for business entities.
     */
    public void delete(T entity) {
        mongoTemplate.remove(entity);
    }

    /**
     * Count all documents of the specified type
     * 
     * Useful for:
     * - Dashboard statistics (total vehicles, active users, etc.)
     * - Pagination calculations
     * - Business metrics reporting
     * 
     * @param entityClass The class type of the document
     * @return Total count of documents
     */
    public long count(Class<T> entityClass) {
        return mongoTemplate.count(new Query(), entityClass);
    }

    /**
     * Count documents matching a specific query
     * 
     * Examples for Car Rental Business:
     * 
     * // Count available vehicles
     * Query availableQuery = new Query(Criteria.where(Vehicle.FM.STATUS).is("FREE"));
     * long availableCount = mongoRepo.count(availableQuery, Vehicle.class);
     * 
     * @param query The MongoDB query with criteria
     * @param entityClass The class type of the document
     * @return Count of matching documents
     */
    public long count(Query query, Class<T> entityClass) {
        return mongoTemplate.count(query, entityClass);
    }

    /**
     * Find documents with pagination support
     * 
     * Essential for car rental listings where we need to:
     * - Display vehicles in pages
     * - Implement infinite scroll
     * - Optimize mobile app performance
     * 
     * @param page Page number (0-based)
     * @param size Number of documents per page
     * @param entityClass The class type of the document
     * @return List of documents for the specified page
     */
    public List<T> findWithPagination(int page, int size, Class<T> entityClass) {
        Query query = new Query();
        query.skip((long) page * size).limit(size);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * Find one document matching the query criteria
     * 
     * Useful for:
     * - Finding specific vehicle by license plate
     * - Getting user by email
     * - Fetching active booking by vehicle
     * 
     * @param query The MongoDB query
     * @param entityClass The class type of the document
     * @return The first matching document or null
     */
    public T findOne(Query query, Class<T> entityClass) {
        return mongoTemplate.findOne(query, entityClass);
    }

    /**
     * Check if any documents exist matching the query
     * 
     * Efficient for existence checks without loading full documents:
     * - Check if license plate is already registered
     * - Verify if user has active bookings
     * - Validate if vehicle is available for specific dates
     * 
     * @param query The MongoDB query
     * @param entityClass The class type of the document
     * @return true if at least one document matches, false otherwise
     */
    public boolean exists(Query query, Class<T> entityClass) {
        return mongoTemplate.exists(query, entityClass);
    }
}
