package PetSitters.security;

import PetSitters.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class VerificationToken {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String token;
    private String status;
    private LocalDateTime expiredDateTime;
    private LocalDateTime issuedDateTime;
    private LocalDateTime confirmedDateTime;
    private String username;
    private String email;

    public VerificationToken(){
        this.token = UUID.randomUUID().toString();
        this.issuedDateTime = LocalDateTime.now();
        this.expiredDateTime = this.issuedDateTime.plusDays(1);
        this.status = STATUS_PENDING;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getStatusPending() {
        return STATUS_PENDING;
    }

    public static String getStatusVerified() {
        return STATUS_VERIFIED;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpiredDateTime() {
        return expiredDateTime;
    }

    public void setExpiredDateTime(LocalDateTime expiredDateTime) {
        this.expiredDateTime = expiredDateTime;
    }

    public LocalDateTime getIssuedDateTime() {
        return issuedDateTime;
    }

    public void setIssuedDateTime(LocalDateTime issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }

    public LocalDateTime getConfirmedDateTime() {
        return confirmedDateTime;
    }

    public void setConfirmedDateTime(LocalDateTime confirmedDateTime) {
        this.confirmedDateTime = confirmedDateTime;
    }
}
