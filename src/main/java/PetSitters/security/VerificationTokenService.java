package PetSitters.security;

import PetSitters.entity.UserPetSitters;
import PetSitters.repository.UserRepository;
import PetSitters.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationTokenService {
    private UserRepository userRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private SendingMailService sendingMailService;

    @Autowired
    public VerificationTokenService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, SendingMailService sendingMailService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.sendingMailService = sendingMailService;
    }

    public void createVerification(String email) {
        UserPetSitters user = userRepository.findByEmail(email);
        VerificationToken verificationToken = verificationTokenRepository.findByEmail(email);
        if (verificationToken == null) {
            verificationToken = new VerificationToken();
            verificationToken.setUsername(user.getUsername());
            verificationToken.setEmail(email);
            verificationTokenRepository.save(verificationToken);
        }
        sendingMailService.sendVerificationMail(email, verificationToken.getToken());
    }

    public ResponseEntity<String> verifyEmail(String token) {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByToken(token);
        if (verificationTokens.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token.");
        }

        VerificationToken verificationToken = verificationTokens.get(0);
        if (verificationToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.unprocessableEntity().body("Expired token.");
        }

        verificationToken.setConfirmedDateTime(LocalDateTime.now());
        verificationToken.setStatus(VerificationToken.STATUS_VERIFIED);
        UserPetSitters user = userRepository.findByUsername(verificationToken.getUsername());
        user.setActive(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok("You have successfully verified your email address.");
    }
}