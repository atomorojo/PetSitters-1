package PetSitters.entity;

import PetSitters.domain.Animal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel("Contract")
@CompoundIndex(name = "usernameA_usernameB_idx", unique = true, def = "{'usernameA' : 1, 'usernameB' : 1}")
@Document
public class Contract {
    @Id
    @ApiModelProperty(value = "Contract id", required = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ApiModelProperty(value = "Username of User A", required = true)
    @Field("usernameA")
    @NotBlank
    private String usernameA;
    @ApiModelProperty(value = "Username of User B", required = true)
    @Field("usernameB")
    @NotBlank
    private String usernameB;
    @ApiModelProperty(value = "When the contract begins", required = true)
    @Field("start")
    @NotBlank
    private String start;
    @ApiModelProperty(value = "When the contract ends", required = true)
    @Field("end")
    @NotBlank
    private String end;
    @ApiModelProperty(value = "The animal/s that are cared for", required = true)
    @Field("animal")
    @NotBlank
    private List<Animal> animal;

    public void setAnimal(List<Animal> animal) {
        this.animal = animal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<Animal> getAnimal() {
        return animal;
    }
}
