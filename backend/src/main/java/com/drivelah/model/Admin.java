package com.drivelah.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "admin")
public class Admin extends User{
}
