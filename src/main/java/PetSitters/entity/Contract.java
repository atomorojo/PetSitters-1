package PetSitters.entity;

import PetSitters.domain.Animal;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class Contract {
    @Id
    @ApiModelProperty(value = "Contract's id", required = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;
    @ApiModelProperty(value = "Username of User A", required = true)
    @Field("usernameA")
    @NotBlank
    String usernameA;
    @ApiModelProperty(value = "Username of User A", required = true)
    @Field("usernameB")
    @NotBlank
    String usernameB;
    @ApiModelProperty(value = "Animals cared for", required = true)
    @Field("animal")
    @NotBlank
    List<Animal> animal;
    @ApiModelProperty(value = "If feedback is wished for", required = true)
    @Field("feedback")
    @NotBlank
    Boolean feedback;
    @ApiModelProperty(value = "When the contract starts", required = true)
    @Field("start")
    @NotBlank
    String start;
    @ApiModelProperty(value = "When the contract ends", required = true)
    @Field("ends")
    @NotBlank
    String end;
    @ApiModelProperty(value = "Whether the contract has been accepted", required = true)
    @Field("accepted")
    @NotBlank
    Boolean accepted;

    public String getUsernameA() {
        return usernameA;
    }

    public void setUsernameA(String usernameA) {
        this.usernameA = usernameA;
    }

    public String getUsernameB() {
        return usernameB;
    }

    public void setUsernameB(String usernameB) {
        this.usernameB = usernameB;
    }

    public List<Animal> getAnimal() {
        return animal;
    }

    public void setAnimal(List<Animal> animal) {
        this.animal = animal;
    }

    public Boolean getFeedback() {
        return feedback;
    }

    public void setFeedback(Boolean feedback) {
        this.feedback = feedback;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
