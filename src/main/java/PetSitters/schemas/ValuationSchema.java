package PetSitters.schemas;

import org.springframework.batch.item.validator.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

public class ValuationSchema implements Serializable {

    @NotBlank
    private String valuedUser;

    private String commentary;

    @NotNull
    private Integer stars;

    public ValuationSchema() {
    }

    public ValuationSchema(@NotBlank String valuedUser, String commentary, @NotNull Integer stars) {
        this.valuedUser = valuedUser;
        this.commentary = commentary;
        this.stars = stars;
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

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();     // Validation of the schema
        Set<ConstraintViolation<ValuationSchema>> violations = validator.validate(this);
        if (!violations.isEmpty())
            throw new ValidationException("There are blank fields");
        if (stars < 0)
            throw new ValidationException("The number of stars cannot be negative");
    }
}