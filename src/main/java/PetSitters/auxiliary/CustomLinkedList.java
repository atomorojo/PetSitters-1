package PetSitters.auxiliary;

public class CustomLinkedList<T> {
    Node head;
    Node last;

    public void addLast(T elem) {
        if (head == null) {
            head = new Node(elem, null);
            last = head;
        } else {
            Node tmp = last;
            last.setNext(new Node(elem, null));
            last = last.getNext();
            last.setAnt(tmp);
        }
    }

    public Node getFirstReference() {
        return head;
    }

    public Node getLastReference() {
        return last;
    }

    public void popFirst() {
        removeByReference(head);
    }

    public void removeByReference(Node reference) {
        if (reference == null) return;
        if (head == reference) {
            if (head == last) {
                head = null;
                last = null;
            } else {
                head = head.getNext();
                head.setAnt(null);
            }
        } else if (last == reference) {
            if (head != last) {
                last = last.getAnt();
                last.setNext(null);
            }
        } else {
            reference.getNext().setAnt(reference.getAnt());
            reference.getAnt().setNext(reference.getNext());
        }

    }
}