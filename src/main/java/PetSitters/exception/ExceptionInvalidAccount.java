package PetSitters.exception;

public class ExceptionInvalidAccount extends Exception {
    public ExceptionInvalidAccount() {
        super("Error: The account is not valid");
    }

    public ExceptionInvalidAccount(String e) {
        super("Error: The account is not valid: " + e);
    }
}
