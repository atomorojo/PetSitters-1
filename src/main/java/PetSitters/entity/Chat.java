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
import java.util.Date;

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

    @ApiModelProperty(value = "The last time when the chat was used", required = false)
    private Date lastUse;

    @ApiModelProperty(value = "Last message of the chat", required = false)
    @Field("lastMessage")
    @NotBlank
    private String lastMessage;

    public Chat() {
    }

    public Chat(@NotBlank String usernameA, @NotBlank String usernameB, Date lastUse, @NotBlank String lastMessage) {
        this.usernameA = usernameA;
        this.usernameB = usernameB;
        this.lastUse = lastUse;
        this.lastMessage = lastMessage;
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

    public Date getLastUse() {
        return lastUse;
    }

    public void setLastUse(Date lastUse) {
        this.lastUse = lastUse;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isLastUsed(Chat cB) {
        return lastUse.after(cB.lastUse);
    }
}
