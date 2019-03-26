package PetSitters.entity;

import PetSitters.schemas.RegisterSchema;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ApiModel("User")
@Document
public class UserPetSitters {
    @Id
    @ApiModelProperty(value = "The user's id", required = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;

    @ApiModelProperty(value = "The user's firstname", required = true)
    @NotBlank
    String firstName;

    @ApiModelProperty(value = "The user's lastname", required = true)
    @NotBlank
    String lastName;

    @ApiModelProperty(value = "The user's username", required = true)
    @Indexed(name = "_username", direction = IndexDirection.ASCENDING, unique = true, background = true)
    @NotBlank
    String username;

    @ApiModelProperty(value = "The user's password", required = true)
    @NotBlank
    String password;

    @ApiModelProperty(value = "The user's email", required = true)
    @Indexed(name = "_email", direction = IndexDirection.ASCENDING, unique = true, background = true)
    @NotBlank
    String email;

    @ApiModelProperty(value = "The user's bitrhdate", required = true)
    @NotBlank
    Date birthdate;

    public UserPetSitters() {}

    private String encrypt(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    public UserPetSitters(RegisterSchema R) throws ParseException {
        this.firstName = R.getFirstName();
        this.lastName = R.getLastName();
        this.username = R.getUsername();
        this.password = encrypt(R.getPassword());
        this.email = R.getEmail();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        this.birthdate = format.parse(R.getBirthdate());
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
        this.password = encrypt(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isTheSamePassword(String password) {
        return new BCryptPasswordEncoder().matches(password,getPassword());
    }
}

