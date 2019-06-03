package PetSitters.schemas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;

import static org.junit.Assert.*;

public class ValuationSchemaTest {

    ValuationSchema valuationSchema;

    @Before
    public void setUp() throws Exception {
        valuationSchema = new ValuationSchema();
    }

    @After
    public void tearDown() throws Exception {
        valuationSchema = null;
    }

    void fillValuationSchema() {
        valuationSchema = new ValuationSchema("user123", "Comment", 5);
    }

    @Test
    public void validateAllIsCorrect() {
        fillValuationSchema();
        valuationSchema.validate();
        assertEquals("Username should be 'user123'", valuationSchema.getValuedUser(), "user123");
        assertEquals("Commentary should be 'Comment'", valuationSchema.getCommentary(), "Comment");
        assertEquals("Stars should be '5'", valuationSchema.getStars(), new Integer(5));
    }


    @Test(expected = ValidationException.class)
    public void validateValuedUserIsBlank() {
        fillValuationSchema();
        valuationSchema.setValuedUser("");
        valuationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateValuedUserIsNull() {
        fillValuationSchema();
        valuationSchema.setValuedUser(null);
        valuationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateStarsIsNull() {
        fillValuationSchema();
        valuationSchema.setStars(null);
        valuationSchema.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateStarsIsNegative() {
        fillValuationSchema();
        valuationSchema.setStars(-1);
        valuationSchema.validate();
    }
}