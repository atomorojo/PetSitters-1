package PetSitters.serviceDTO;

import java.util.LinkedList;

public class DTOTranslationIncoming implements DTO {
    LinkedList<String> text;
    String targetLanguage;

    public DTOTranslationIncoming() {
        text = new LinkedList<>();
    }

    public DTOTranslationIncoming(LinkedList<String> text, String targetLanguage) {
        this.text = text;
        this.targetLanguage = targetLanguage;
    }

    public LinkedList<String> getText() {
        return text;
    }

    public void setText(LinkedList<String> text) {
        this.text = text;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public LinkedList<DTOTranslationIncoming> split() {
        LinkedList<DTOTranslationIncoming> list = new LinkedList<>();
        for (String element: text) {
            LinkedList<String> elementList = new LinkedList<>();
            elementList.addLast(element);
            list.addLast(new DTOTranslationIncoming(elementList, targetLanguage));
        }
        return list;
    }

    // PRE: all the target languages must be the same
    public void join(LinkedList<DTOTranslationIncoming> list) {
        text.clear();
        for (DTOTranslationIncoming element: list) {
            text.addAll(element.text);
            targetLanguage = element.getTargetLanguage();
        }
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (String s: text) {
            int elem = 0;
            for (char c : s.toCharArray()) {
                elem = elem * 256 + c;
            }
            result += elem;
        }
        for (char c : targetLanguage.toCharArray()) {
            result = result * 256 + c;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        DTOTranslationIncoming dtoTranslationIncoming = (DTOTranslationIncoming) o;
        return text.equals(dtoTranslationIncoming.text) && targetLanguage.equals(dtoTranslationIncoming.targetLanguage);
    }
}
