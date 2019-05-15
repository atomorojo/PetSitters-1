package PetSitters.schemas;

public class ChatPreviewSchema {

    String name;
    String profileImage;
    String lastMessage;

    public ChatPreviewSchema(String name, String profileImage, String lastMessage) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
