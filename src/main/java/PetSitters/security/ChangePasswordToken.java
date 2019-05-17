package PetSitters.security;

import javax.persistence.Entity;


@Entity
public class ChangePasswordToken extends GenericToken {
    public static final String PASSWORD_NOT_CHANGED = "PENDING";
    public static final String PASSWORD_CHANGED = "CHANGED";

    public ChangePasswordToken() {
        super(PASSWORD_NOT_CHANGED);
    }

    public static String getPasswordNotChanged() {
        return PASSWORD_NOT_CHANGED;
    }

    public static String getPasswordChanged() {
        return PASSWORD_CHANGED;
    }

    public boolean isAlreadyChanged() {
        String superStatus = super.getStatus();
        return superStatus.equals(PASSWORD_CHANGED);
    }
}
