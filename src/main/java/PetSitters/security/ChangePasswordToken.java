package PetSitters.security;

import PetSitters.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class ChangePasswordToken extends GenericToken {
    public static final String PASSWORD_NOT_CHANGED = "PENDING";
    public static final String PASSWORD_CHANGED = "CHANGED";

    public ChangePasswordToken(){
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
