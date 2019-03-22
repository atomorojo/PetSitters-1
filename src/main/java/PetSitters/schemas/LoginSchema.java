package PetSitters.schemas;

public class LoginSchema {

    String username;
    String password;

    public LoginSchema(String username, String password) {
        this.username=username;
        this.password=password;
    }

    public String getUsername() {
        return username;
    }

    public void setUser(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
