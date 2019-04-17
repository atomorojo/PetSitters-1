package PetSitters.schemas;

import java.io.Serializable;

public class FullUserSchema implements Serializable {

    private String profile_image;
    private Integer stars;
    private String name;
    private String localization;
    private String description;
    private String commentaries;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommentaries() {
        return commentaries;
    }

    public void setCommentaries(String commentaries) {
        this.commentaries = commentaries;
    }
}
