package PetSitters.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ApiModel("User")
@Document
public class User {
    @Id
    @ApiModelProperty(value = "The user's id", required = false)
    String id;

    @ApiModelProperty(value = "The user's firstname", required = true)
    @NotNull
    String firstName;

    @ApiModelProperty(value = "The user's lastname", required = true)
    @NotNull
    String lastName;

    @ApiModelProperty(value = "The user's username", required = true)
    @Indexed(name = "_username", direction = IndexDirection.ASCENDING, unique = true, background = true)
    @NotNull
    String username;

    @ApiModelProperty(value = "The user's password", required = true)
    @NotNull
    String password;

    @ApiModelProperty(value = "The user's bitrhdate", required = true)
    @NotNull
    Date birthdate;

    public User() {}

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String firstName, String lastName, String username, String password, String birthdate) throws ParseException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        this.birthdate = format.parse(birthdate);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

}

