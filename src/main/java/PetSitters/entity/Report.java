package PetSitters.entity;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;

public class Report {
    @Id
    @ApiModelProperty(value = "The report's id", required = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ApiModelProperty(value = "The reporter's email", required = true)
    @NotBlank
    private String reporter; //email

    @ApiModelProperty(value = "The reported email", required = true)
    @NotBlank
    private String reported; //email

    @ApiModelProperty(value = "The description of the report", required = true)
    @NotBlank
    private String description;

    public Report() {
    }

    public Report(@NotBlank String reporter, @NotBlank String reported, @NotBlank String description) {
        this.reporter = reporter;
        this.reported = reported;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
