package PetSitters.security;

import javax.persistence.Entity;


@Entity
public class VerificationToken extends GenericToken {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";

    public VerificationToken() {
        super(STATUS_PENDING);
    }

    public static String getStatusPending() {
        return STATUS_PENDING;
    }

    public static String getStatusVerified() {
        return STATUS_VERIFIED;
    }

}
