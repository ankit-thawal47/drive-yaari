package com.drivelah.model;

public class Address {

    private String buildingNumber;
    private String pinCode;

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    private String getFullAddress() {
        return this.buildingNumber + " , " + this.pinCode;
    }

}
