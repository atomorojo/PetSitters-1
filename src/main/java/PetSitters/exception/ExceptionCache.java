package PetSitters.exception;

public class ExceptionCache  extends Exception {
    public ExceptionCache() {
        super("Error: There is a miss in the cache");
    }

    public ExceptionCache(String e) {
        super("Error: There is a miss in the cache: " + e);
    }
}
