package PetSitters.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;

@ApiModel("Chat")
@CompoundIndex(name = "usernameA_usernameB_idx", unique = true, def = "{'usernameA' : 1, 'usernameB' : 1}")
@Document
public class Chat {
    @Id
    @ApiModelProperty(value = "Chat's id", required = false)
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

    public Chat(@NotBlank String usernameA, @NotBlank String usernameB) {
        this.usernameA = usernameA;
        this.usernameB = usernameB;
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
}
