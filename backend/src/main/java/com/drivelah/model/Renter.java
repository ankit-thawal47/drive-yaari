package com.drivelah.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "renter")
public class Renter extends User {

}
