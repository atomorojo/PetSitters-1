package PetSitters.entity;

import PetSitters.schemas.MessageSchema;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class Message {
    @Id
    @ApiModelProperty(value = "The message's id", required = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;

    @ApiModelProperty(value = "When the message was sent", required = false)
    @Field("whenSent")
    @NotBlank
    private Date whenSent;

    @ApiModelProperty(value = "Username of the user who sends the message", required = true) // Used to identify the chat
    @Field("userWhoSends")
    @NotBlank
    private String userWhoSends;

    @ApiModelProperty(value = "Username of the user who receives the message", required = true) // Used to identify the chat
    @Field("userWhoReceives")
    @NotBlank
    private String userWhoReceives;

    @ApiModelProperty(value = "Content the message", required = true)
    @Field("content")
    @NotBlank
    private String content;

    @ApiModelProperty(value = "Declares whether the content of the message can be seen by the receiver", required = false)
    @Field("isVisible?")
    @NotBlank
    private Boolean isVisible;

    @ApiModelProperty(value = "Declares whether the content of the message is a multimedia content", required = true)
    @Field("isMultimedia?")
    @NotBlank
    private Boolean isMultimedia;

    public Message() {
    }

    public Message(Date whenSent, String userWhoSends, String userWhoReceives, String content, Boolean isVisible) {
        this.whenSent = whenSent;
        this.userWhoSends = userWhoSends;
        this.userWhoReceives = userWhoReceives;
        this.content = content;
        this.isVisible = isVisible;
    }

    public Message(MessageSchema messageSchema, String userWhoSends, Date timestamp, Boolean isVisible) {
        this.whenSent = timestamp;
        this.userWhoSends = userWhoSends;
        this.userWhoReceives = messageSchema.getUserWhoReceives();
        this.content = messageSchema.getContent();
        this.isVisible = isVisible;
        this.isMultimedia = messageSchema.getIsMultimedia();
    }

    public @NotBlank Date getWhenSent() {
        return whenSent;
    }

    public void setWhenSent(Date whenSent) {
        this.whenSent = whenSent;
    }

    public String getUserWhoSends() {
        return userWhoSends;
    }

    public void setUserWhoSends(String userWhoSends) {
        this.userWhoSends = userWhoSends;
    }

    public String getUserWhoReceives() {
        return userWhoReceives;
    }

    public void setUserWhoReceives(String userWhoReceives) {
        this.userWhoReceives = userWhoReceives;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public boolean isLastSent(Message mReceiver) {
        return whenSent.after(mReceiver.whenSent);
    }

    public Boolean getMultimedia() {
        return isMultimedia;
    }

    public void setMultimedia(Boolean multimedia) {
        isMultimedia = multimedia;
    }
}
