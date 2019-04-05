package PetSitters.security;

import PetSitters.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class VerificationToken extends GenericToken {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";

    public VerificationToken(){
        super(STATUS_PENDING);
    }

    public static String getStatusPending() {
        return STATUS_PENDING;
    }

    public static String getStatusVerified() {
        return STATUS_VERIFIED;
    }

}
