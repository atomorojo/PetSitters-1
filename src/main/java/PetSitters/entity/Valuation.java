package PetSitters.entity;

import PetSitters.domain.Animal;
import PetSitters.schemas.ValuationSchema;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@ApiModel("Report")
@Document
public class Valuation {
    @Id
    @ApiModelProperty(value = "Valuation id", required = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ApiModelProperty(value = "User who values", required = true)
    @NotBlank
    private String userWhoValues;

    @ApiModelProperty(value = "Valued user", required = true)
    @NotBlank
    private String valuedUser;

    @ApiModelProperty(value = "Commentary of the valuation", required = false)
    private String commentary;

    @ApiModelProperty(value = "Date of the valuation", required = true)
    @NotBlank
    private Date date;

    @ApiModelProperty(value = "Stars of the valuation", required = true)
    @NotBlank
    private Integer stars;

    private List<Animal> animals;

    public Valuation() {
    }

    public Valuation(@NotBlank String userWhoValues, @NotBlank String valuedUser, @NotBlank String commentary, @NotBlank Date date, @NotBlank Integer stars) {
        this.userWhoValues = userWhoValues;
        this.valuedUser = valuedUser;
        this.commentary = commentary;
        this.date = date;
        this.stars = stars;
    }

    public Valuation(ValuationSchema valuationSchema, String usernameWhoValues, Date date) {
        this.userWhoValues = usernameWhoValues;
        this.valuedUser = valuationSchema.getValuedUser();
        this.commentary = valuationSchema.getCommentary();
        this.date = date;
        this.stars = valuationSchema.getStars();
    }

    public Valuation(ValuationSchema valuationSchema, String usernameWhoValues, Date date, List<Animal> animal) {
        this.userWhoValues = usernameWhoValues;
        this.valuedUser = valuationSchema.getValuedUser();
        this.commentary = valuationSchema.getCommentary();
        this.date = date;
        this.stars = valuationSchema.getStars();
        this.animals = animal;
    }

    public String getUserWhoValues() {
        return userWhoValues;
    }

    public void setUserWhoValues(String userWhoValues) {
        this.userWhoValues = userWhoValues;
    }

    public String getValuedUser() {
        return valuedUser;
    }

    public void setValuedUser(String valuedUser) {
        this.valuedUser = valuedUser;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getstars() {
        return stars;
    }

    public void setstars(Integer stars) {
        this.stars = stars;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }
}
