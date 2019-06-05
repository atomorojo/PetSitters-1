package PetSitters.schemas;

public class TrophiesRankingPreviewSchema {
    String username;
    String fullName;
    String profileImage;
    Integer numberOfTrophies;

    public TrophiesRankingPreviewSchema() {
    }

    public TrophiesRankingPreviewSchema(String username, String fullName, String profileImage, Integer numberOfStars) {
        this.username = username;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.numberOfTrophies = numberOfStars;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Integer getNumberOfTrophies() {
        return numberOfTrophies;
    }

    public void setNumberOfTrophies(Integer numberOfTrophies) {
        this.numberOfTrophies = numberOfTrophies;
    }
}
