package com.drivelah.model.ApiModel;

public class TripRatingRequest {

    private Integer renterRating;

    private Integer ownerRating;

    private String renterComments;

    private String ownerComments;

    private String submittedBy;

    private String category;

    private boolean isPublic = true;


    public TripRatingRequest() {
    }

    public TripRatingRequest(Integer renterRating, String renterComments) {
        this.renterRating = renterRating;
        this.renterComments = renterComments;
        this.submittedBy = "renter";
    }

    public TripRatingRequest(Integer ownerRating, String ownerComments, boolean isOwner) {
        this.ownerRating = ownerRating;
        this.ownerComments = ownerComments;
        this.submittedBy = "owner";
    }

    // Getters and Setters

    public Integer getRenterRating() {
        return renterRating;
    }

    public void setRenterRating(Integer renterRating) {
        this.renterRating = renterRating;
    }

    public Integer getOwnerRating() {
        return ownerRating;
    }

    public void setOwnerRating(Integer ownerRating) {
        this.ownerRating = ownerRating;
    }

    public String getRenterComments() {
        return renterComments;
    }

    public void setRenterComments(String renterComments) {
        this.renterComments = renterComments;
    }

    public String getOwnerComments() {
        return ownerComments;
    }

    public void setOwnerComments(String ownerComments) {
        this.ownerComments = ownerComments;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isValid() {
        // At least one rating should be provided
        if (renterRating == null && ownerRating == null) {
            throw new IllegalArgumentException("At least one rating must be provided");
        }

        // Validate rating range
        if (renterRating != null && (renterRating < 1 || renterRating > 5)) {
            throw new IllegalArgumentException("Renter rating must be between 1 and 5");
        }

        if (ownerRating != null && (ownerRating < 1 || ownerRating > 5)) {
            throw new IllegalArgumentException("Owner rating must be between 1 and 5");
        }

        return true;
    }

    public boolean isRenterRating() {
        return "renter".equals(submittedBy) || renterRating != null;
    }

    public boolean isOwnerRating() {
        return "owner".equals(submittedBy) || ownerRating != null;
    }

    @Override
    public String toString() {
        return "TripRatingRequest{" +
                "renterRating=" + renterRating +
                ", ownerRating=" + ownerRating +
                ", submittedBy='" + submittedBy + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }
}