package PetSitters.security;

import PetSitters.auxiliary.ReadWebPage;
import PetSitters.entity.UserPetSitters;
import PetSitters.repository.UserRepository;
import PetSitters.repository.VerificationTokenRepository;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static PetSitters.security.Constants.*;

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

    public ResponseEntity<String> verifyEmail(String token) throws IOException {
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByToken(token);
        if (verificationTokens.isEmpty()) {
            ReadWebPage read = new ReadWebPage();
            Map<String, String> data = new HashMap<>();
            data.put("error", "Invalid token.");
            String formattedString = read.getProcessedText(ERROR_EMAIL_CONFIRMATION_WEB_PAGE_PATH, data);
            return ResponseEntity.badRequest().body(formattedString);
        }

        VerificationToken verificationToken = verificationTokens.get(0);
        if (verificationToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            ReadWebPage read = new ReadWebPage();
            Map<String, String> data = new HashMap<>();
            data.put("error", "Expired token.");
            String formattedString = read.getProcessedText(ERROR_EMAIL_CONFIRMATION_WEB_PAGE_PATH, data);
            return ResponseEntity.badRequest().body(formattedString);
        }

        verificationToken.setConfirmedDateTime(LocalDateTime.now());
        verificationToken.setStatus(VerificationToken.STATUS_VERIFIED);
        UserPetSitters user = userRepository.findByUsername(verificationToken.getUsername());
        user.setActive(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        ReadWebPage read = new ReadWebPage();
        String htmlPage = read.getText(OK_EMAIL_CONFIRMATION_WEB_PAGE_PATH);
        return ResponseEntity.ok(htmlPage);
    }
}