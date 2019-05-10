package PetSitters.domain;

public class Availability {

    String whatIsThis;

    public Availability(String name) {
        this.whatIsThis = name;
    }

    public Availability() {
    }

    public String getWhatIsThis() {
        return whatIsThis;
    }

    public void setWhatIsThis(String whatIsThis) {
        this.whatIsThis = whatIsThis;
    }

    public String toString() {
        return whatIsThis;
    }
}
