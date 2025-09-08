package com.drivelah.model;

public abstract class User {

    private String id;
    private String name;
    private String phoneNumber;
    private Address address;
    //Both  - Host & Renter - needs to be verified and then only are capable to move ahead
    private boolean isVerified;
}
