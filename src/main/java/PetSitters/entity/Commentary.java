package PetSitters.entity;

import java.io.Serializable;
import java.util.Date;

public class Commentary implements Serializable {

    private String commenter;
    private String commentedTo;
    private String description;
    private Integer stars;
    private Date date;

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getCommentedTo() {
        return commentedTo;
    }

    public void setCommentedTo(String commentedTo) {
        this.commentedTo = commentedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
