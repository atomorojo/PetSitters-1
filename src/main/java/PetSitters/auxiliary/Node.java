package PetSitters.auxiliary;

public class Node<T> {
    private T elem;
    private Node next;
    private Node ant;

    public Node(T elem, Node next) {
        this.elem = elem;
        this.next = next;
    }

    public T getElem() {
        return elem;
    }

    public void setElem(T elem) {
        this.elem = elem;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getAnt() {
        return ant;
    }

    public void setAnt(Node ant) {
        this.ant = ant;
    }
}
