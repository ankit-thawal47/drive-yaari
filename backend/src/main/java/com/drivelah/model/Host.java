package com.drivelah.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "host")
public class Host extends User {

}
