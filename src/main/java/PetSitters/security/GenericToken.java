package PetSitters.security;

import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDateTime;
import java.util.UUID;

public class GenericToken {
    protected String token;
    protected LocalDateTime expiredDateTime;
    protected LocalDateTime issuedDateTime;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private LocalDateTime confirmedDateTime;
    private String status;
    private String username;
    private String email;

    public GenericToken() {
        initialize();
    }

    public GenericToken(String status) {
        initialize();
        this.status = status;
    }

    private void initialize() {
        this.token = UUID.randomUUID().toString();
        this.issuedDateTime = LocalDateTime.now();
        this.expiredDateTime = this.issuedDateTime.plusDays(1);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
