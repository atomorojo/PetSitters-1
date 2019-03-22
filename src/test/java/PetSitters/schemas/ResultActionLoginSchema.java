package PetSitters.schemas;

public class ResultActionLoginSchema {
    String status;
    String message;
    ResultSchema result;

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

    public ResultSchema getResult() {
        return result;
    }

    public void setResult(ResultSchema resultSchema) {
        this.result = resultSchema;
    }
}
