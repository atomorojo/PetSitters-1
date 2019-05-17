package PetSitters.auxiliary;

public class Pair<F, S> {
    F First;
    S Second;

    public Pair(F first, S second) {
        First = first;
        Second = second;
    }

    public F getFirst() {
        return First;
    }

    public void setFirst(F first) {
        First = first;
    }

    public S getSecond() {
        return Second;
    }

    public void setSecond(S second) {
        Second = second;
    }
}
