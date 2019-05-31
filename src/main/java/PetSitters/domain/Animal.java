package PetSitters.domain;

public class Animal {

    String name;
    String tipus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Animal))return false;
        Animal otherMyClass = (Animal) other;
        return name.equals(otherMyClass.name) && tipus.equals(otherMyClass.tipus);
    }
}
