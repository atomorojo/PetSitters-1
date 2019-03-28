package PetSitters.schemas;

public class ResultActionLoginSchemaTest {
    String status;
    String message;
    ResultSchemaTest result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResultSchemaTest getResult() {
        return result;
    }

    public void setResult(ResultSchemaTest resultSchema) {
        this.result = resultSchema;
    }
}
