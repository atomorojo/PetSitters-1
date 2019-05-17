package PetSitters.schemas;

import java.io.Serializable;

public class ModifySchema implements Serializable {

    String toModify;

    public ModifySchema(String name) {
        this.toModify = name;
    }

    public String getToModify() {
        return toModify;
    }

    public void setToModify(String toModify) {
        this.toModify = toModify;
    }
}
