package PetSitters.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotBlank;

@ApiModel("Report")
@Document
public class Report {
    @Id
    @ApiModelProperty(value = "Report's id", required = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ApiModelProperty(value = "Reporter's email", required = true)
    @NotBlank
    private String reporter; //email

    @ApiModelProperty(value = "Reported email", required = true)
    @NotBlank
    private String reported; //email

    @ApiModelProperty(value = "Description of the report", required = true)
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
