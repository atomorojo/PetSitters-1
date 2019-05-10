package PetSitters.entity;

import PetSitters.domain.Availability;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    @ApiModelProperty(value = "Where the user resides")
    private String city;

    @ApiModelProperty(value = "The user's score")
    private Double stars;

    @ApiModelProperty(value = "The user's profile image")
    private String image;

    @ApiModelProperty(value = "The user's description")
    private String description;

    @ApiModelProperty(value = "The user's list of animals that he can care")
    private List<String> expert = new ArrayList<String>();
    @ApiModelProperty(value = "The user's list of favorite users")
    private List<String> favorites = new ArrayList<String>();


    @ApiModelProperty(value = "The user's availabilityy")
    private Availability availability;

    private boolean active;

    public UserPetSitters() {
    }

    public UserPetSitters(RegisterSchema R) throws ParseException {
        this.firstName = R.getFirstName();
        this.lastName = R.getLastName();
        this.username = R.getUsername();
        this.password = encrypt(R.getPassword());
        this.email = R.getEmail();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        this.birthdate = format.parse(R.getBirthdate());
        this.active = false;
    }

    private String encrypt(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getStars() {
        return stars;
    }

    public void setStars(Double stars) {
        this.stars = stars;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getExpert() {
        return expert;
    }

    public void setExpert(List<String> expert) {
        this.expert = expert;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public void addFavorites(String favorites) {
        this.favorites.add(favorites);
    }

    public void removeFavorites(String s) {
        this.favorites.remove(s);
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

    public boolean isTheSamePassword(String password) {
        return new BCryptPasswordEncoder().matches(password, getPassword());
    }


}

