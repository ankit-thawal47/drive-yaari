package com.drivelah.service;

import com.drivelah.model.pricing.PricingCalculation;
import com.drivelah.model.pricing.PricingRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PricingService {

    private final Map<String, VehicleTypeRates> pricingRates;

    public PricingService() {
        pricingRates = new HashMap<>();
        initializePricingRates();
    }

    public PricingCalculation calculatePricing(PricingRequest request) {
        try {
            VehicleTypeRates rates = pricingRates.get(request.getVehicleType().toUpperCase());
            if (rates == null) {
                rates = pricingRates.get("STANDARD");
                System.out.println("Vehicle type " + request.getVehicleType() +
                        " not found, using STANDARD rates");
            }

            Double plannedHours = request.getPlannedDurationHours();
            if (plannedHours == null || plannedHours <= 0) {
                throw new IllegalArgumentException("Invalid rental duration");
            }

            Double estimatedKm = request.getEstimatedKm();
            if (estimatedKm == null || estimatedKm <= 0) {
                estimatedKm = Math.min(plannedHours * 25.0, 200.0);
            }

            PricingCalculation calculation = new PricingCalculation(
                    request.getVehicleType(),
                    rates.baseRatePerHour,
                    rates.perKmRate,
                    plannedHours,
                    estimatedKm
            );

            System.out.println("Pricing calculated for " + request.getVehicleType() +
                    ": " + plannedHours + "h, " + estimatedKm + "km = SGD$" +
                    calculation.getTotalAmount());

            return calculation;

        } catch (Exception e) {
            System.err.println("Pricing calculation error: " + e.getMessage());
            throw new RuntimeException("Failed to calculate pricing: " + e.getMessage(), e);
        }
    }

    public VehicleTypeRates getRatesForVehicleType(String vehicleType) {
        return pricingRates.get(vehicleType.toUpperCase());
    }

    public Map<String, VehicleTypeRates> getAllRates() {
        return new HashMap<>(pricingRates);
    }

    public Double getEstimatePrice(String vehicleType, Double hours) {
        VehicleTypeRates rates = pricingRates.get(vehicleType.toUpperCase());
        if (rates == null) {
            rates = pricingRates.get("STANDARD");
        }

        Double baseAmount = hours * rates.baseRatePerHour;
        Double distanceAmount = (hours * 25.0) * rates.perKmRate;
        Double subtotal = baseAmount + distanceAmount;
        Double serviceFee = subtotal * 0.10;

        return subtotal + serviceFee;
    }

    private void initializePricingRates() {
        pricingRates.put("ECONOMY", new VehicleTypeRates(
                "ECONOMY",
                8.0,
                0.30,
                "Budget-friendly cars perfect for city driving. Includes Toyota Vios, Honda City, Nissan Almera."
        ));

        pricingRates.put("STANDARD", new VehicleTypeRates(
                "STANDARD",
                12.0,
                0.45,
                "Comfortable mid-range vehicles. Includes Toyota Altis, Honda Civic, Mazda 3."
        ));

        pricingRates.put("PREMIUM", new VehicleTypeRates(
                "PREMIUM",
                25.0,
                0.80,
                "Luxury vehicles for special occasions. Includes BMW 3 Series, Mercedes C-Class, Audi A4."
        ));

        System.out.println("Initialized pricing rates for " + pricingRates.size() + " vehicle types");
    }

    public static class VehicleTypeRates {
        public final String vehicleType;
        public final Double baseRatePerHour;
        public final Double perKmRate;
        public final String description;

        public VehicleTypeRates(String vehicleType, Double baseRatePerHour,
                                Double perKmRate, String description) {
            this.vehicleType = vehicleType;
            this.baseRatePerHour = baseRatePerHour;
            this.perKmRate = perKmRate;
            this.description = description;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public Double getBaseRatePerHour() {
            return baseRatePerHour;
        }

        public Double getPerKmRate() {
            return perKmRate;
        }

        public String getDescription() {
            return description;
        }
    }
}