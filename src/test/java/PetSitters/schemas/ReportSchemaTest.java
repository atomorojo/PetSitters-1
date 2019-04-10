package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class ReportSchemaTest {

    ReportSchema reportSchema;

    @Before
    public void setUp() {
        reportSchema = new ReportSchema();
    }

    @After
    public void tearDown() {
        reportSchema = null;
    }

    void fillReportSchema() {
        reportSchema = new ReportSchema("dummy", "No reason");
    }

    @Test
    public void validateAllIsCorrect() {
        fillReportSchema();
        reportSchema.validate();
        assertEquals("The reported username should be 'dummy'", reportSchema.getReported(), "dummy");
        assertEquals("The description should be 'No reason'", reportSchema.getDescription(), "No reason");
    }


    @Test(expected = ValidationException.class)
    public void validateReportedUserIsBlank() {
        fillReportSchema();
        reportSchema.setReported("");
        reportSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateReportedUserIsNull() {
        fillReportSchema();
        reportSchema.setReported(null);
        reportSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateDescriptionIsBlank() {
        fillReportSchema();
        reportSchema.setDescription("");
        reportSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateDescriptionIsNull() {
        fillReportSchema();
        reportSchema.setDescription(null);
        reportSchema.validate();
    }
}