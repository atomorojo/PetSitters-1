package PetSitters.serviceDTO;

import java.util.Iterator;
import java.util.LinkedList;

public class DTOTranslationOutgoing implements DTO {
    LinkedList<String> text;
    Boolean fullyResolved;

    public DTOTranslationOutgoing(LinkedList<String> text, Boolean fullyResolved) {
        this.text = text;
        this.fullyResolved = fullyResolved;
    }

    public DTOTranslationOutgoing() {
        text = new LinkedList<>();
        fullyResolved = false;
    }

    public DTOTranslationOutgoing(LinkedList<String> text) {
        this.text = text;
    }

    public LinkedList<String> getText() {
        return text;
    }

    public void setText(LinkedList<String> text) {
        this.text = text;
    }

    public Boolean isFullyResolved() {
        return fullyResolved;
    }

    public void setFullyResolved(Boolean fullyResolved) {
        this.fullyResolved = fullyResolved;
    }

    public LinkedList<DTOTranslationOutgoing> split() {
        LinkedList<DTOTranslationOutgoing> list = new LinkedList<>();
        for (String element: text) {
            LinkedList<String> elementList = new LinkedList<>();
            elementList.addLast(element);
            list.addLast(new DTOTranslationOutgoing(elementList, element != null));
        }
        return list;
    }

    // PRE: all the target languages must be the same
    public void join(LinkedList<DTOTranslationOutgoing> list) {
        text = new LinkedList<>();
        fullyResolved = true;
        for (DTOTranslationOutgoing element: list) {
            text.addAll(element.text);
            fullyResolved = fullyResolved && element.isFullyResolved();
        }
    }

    public void merge(DTOTranslationOutgoing resultMerge) throws Exception {
        Iterator<String> iteratorOriginal = text.iterator();
        Iterator<String> iteratorInput = resultMerge.text.iterator();
        LinkedList<String> newText = new LinkedList<>();
        while (iteratorOriginal.hasNext()) {
            String add = iteratorOriginal.next();
            if (add == null) {
                if (iteratorInput.hasNext()) {
                    add = iteratorInput.next();
                } else {
                    throw new Exception("Error in the merge: Not enough parameters to merge");
                }
            }
            newText.addLast(add);
        }
        if (iteratorInput.hasNext()) {
            throw new Exception("Error in the merge: Too many parameters to merge");
        }
        text = newText;
        fullyResolved = true;
    }
}
