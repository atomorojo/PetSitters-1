package PetSitters.exception;

public class ExceptionServiceError extends Exception {
    public ExceptionServiceError() {
        super("Error: There is an error in the Service");
    }

    public ExceptionServiceError(String e) {
        super("Error: There is an error in the Service: " + e);
    }
}
