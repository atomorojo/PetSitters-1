package PetSitters.schemas;

import java.util.Date;

public class ValuationPreviewSchema {

    String usernameWhoValues;
    String NameOfUserWhoValues;
    Date whenValued;
    String profileImage;
    Integer stars;
    String comment;

    public ValuationPreviewSchema() {
    }

    public ValuationPreviewSchema(String usernameWhoValues, String nameOfUserWhoValues, Date whenValued, String profileImage, Integer stars, String comment) {
        this.usernameWhoValues = usernameWhoValues;
        NameOfUserWhoValues = nameOfUserWhoValues;
        this.whenValued = whenValued;
        this.profileImage = profileImage;
        this.stars = stars;
        this.comment = comment;
    }

    public String getUsernameWhoValues() {
        return usernameWhoValues;
    }

    public void setUsernameWhoValues(String usernameWhoValues) {
        this.usernameWhoValues = usernameWhoValues;
    }

    public String getNameOfUserWhoValues() {
        return NameOfUserWhoValues;
    }

    public void setNameOfUserWhoValues(String nameOfUserWhoValues) {
        NameOfUserWhoValues = nameOfUserWhoValues;
    }

    public Date getWhenValued() {
        return whenValued;
    }

    public void setWhenValued(Date whenValued) {
        this.whenValued = whenValued;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
