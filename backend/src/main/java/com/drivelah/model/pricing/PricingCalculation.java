package com.drivelah.model.pricing;

public class PricingCalculation {

    private String vehicleType;
    private Double baseRate;
    private Double perKmRate;
    private Double plannedHours;
    private Double estimatedKm;

    private Double baseAmount;
    private Double distanceAmount;
    private Double subtotal;
    private Double securityDeposit;
    private Double serviceFee;
    private Double totalAmount;

    private String currency;
    private String breakdown;

    public PricingCalculation() {
        this.currency = "INR";
    }

    public PricingCalculation(String vehicleType, Double baseRate, Double perKmRate,
                              Double plannedHours, Double estimatedKm) {
        this();
        this.vehicleType = vehicleType;
        this.baseRate = baseRate;
        this.perKmRate = perKmRate;
        this.plannedHours = plannedHours;
        this.estimatedKm = estimatedKm;

        calculatePricing();
    }

    public void calculatePricing() {
        this.baseAmount = (plannedHours != null && baseRate != null)
                ? plannedHours * baseRate : 0.0;

        this.distanceAmount = (estimatedKm != null && perKmRate != null)
                ? estimatedKm * perKmRate : 0.0;

        this.subtotal = baseAmount + distanceAmount;

        this.securityDeposit = Math.max(subtotal * 0.20, 50.0);

        this.serviceFee = subtotal * 0.10;

        this.totalAmount = subtotal + serviceFee;
    }

//    private void generateBreakdown() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("Base rental (%.1fh × $%.2f): $%.2f\n",
//                plannedHours, baseRate, baseAmount));
//        sb.append(String.format("Distance charge (%.1fkm × $%.2f): $%.2f\n",
//                estimatedKm, perKmRate, distanceAmount));
//        sb.append(String.format("Service fee (10%%): $%.2f\n", serviceFee));
//        sb.append(String.format("Security deposit (refundable): $%.2f", securityDeposit));
//
//        this.breakdown = sb.toString();
//    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(Double baseRate) {
        this.baseRate = baseRate;
    }

    public Double getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(Double perKmRate) {
        this.perKmRate = perKmRate;
    }

    public Double getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(Double plannedHours) {
        this.plannedHours = plannedHours;
    }

    public Double getEstimatedKm() {
        return estimatedKm;
    }

    public void setEstimatedKm(Double estimatedKm) {
        this.estimatedKm = estimatedKm;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Double getDistanceAmount() {
        return distanceAmount;
    }

    public void setDistanceAmount(Double distanceAmount) {
        this.distanceAmount = distanceAmount;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(Double securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(String breakdown) {
        this.breakdown = breakdown;
    }
}