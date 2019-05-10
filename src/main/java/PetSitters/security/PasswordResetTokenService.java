package PetSitters.security;

import PetSitters.auxiliary.Pair;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.ResetPasswordTokenRepository;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.ResetPasswordSchema;
import PetSitters.schemas.SetAnotherPasswordSchema;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

import static PetSitters.security.Constants.CHANGE_PASSWORD_WEB_PAGE_PATH;

@Service
public class PasswordResetTokenService {
    private UserRepository userRepository;
    private ResetPasswordTokenRepository resetPasswordTokenRepository;
    private SendingMailService sendingMailService;

    @Autowired
    public PasswordResetTokenService(UserRepository userRepository, ResetPasswordTokenRepository resetPasswordTokenRepository, SendingMailService sendingMailService) {
        this.userRepository = userRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.sendingMailService = sendingMailService;
    }

    public void createRequest(ResetPasswordSchema resetPasswordSchema) throws ExceptionInvalidAccount {
        resetPasswordSchema.validate();
        String email = resetPasswordSchema.getEmail();
        UserPetSitters user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ExceptionInvalidAccount("The email does not belong to any user");
        }

        if (!user.isActive()) {
            throw new ExceptionInvalidAccount("Account not activated");
        }

        ChangePasswordToken changePasswordToken = resetPasswordTokenRepository.findByEmail(email);

        if (changePasswordToken == null) {
            changePasswordToken = new ChangePasswordToken();
            changePasswordToken.setUsername(user.getUsername());
            changePasswordToken.setEmail(email);
            resetPasswordTokenRepository.save(changePasswordToken);
        }
        sendingMailService.sendChangePassword(email, changePasswordToken.getToken());
    }

    private Pair<ResponseEntity<String>, ChangePasswordToken> checkTokenIntegrity(String token) {
        List<ChangePasswordToken> changePasswordTokens = resetPasswordTokenRepository.findByToken(token);
        if (changePasswordTokens.isEmpty()) {
            return new Pair(ResponseEntity.badRequest().body("Invalid token."), null);
        }

        ChangePasswordToken changePasswordToken = changePasswordTokens.get(0);
        if (changePasswordToken.getExpiredDateTime().isBefore(LocalDateTime.now())) {
            return new Pair(ResponseEntity.unprocessableEntity().body("Expired token."), changePasswordToken);
        }

        if (changePasswordToken.isAlreadyChanged()) {
            return new Pair(ResponseEntity.unprocessableEntity().body("The token has already been used."), changePasswordToken);
        }
        return new Pair(ResponseEntity.ok(""), changePasswordToken);
    }

    public ResponseEntity<String> sendFormPasswordReset(String token) throws IOException, TemplateException {
        Pair<ResponseEntity<String>, ChangePasswordToken> p = checkTokenIntegrity(token);
        ResponseEntity re = p.getFirst();
        if (re.getStatusCode().is2xxSuccessful()) {
            String path = new File(".").getCanonicalPath();
            String htmlPage = new String(Files.readAllBytes(Paths.get(path + CHANGE_PASSWORD_WEB_PAGE_PATH)));
            return ResponseEntity.ok(htmlPage);
        }
        return re;
    }


    public ResponseEntity<String> setAnotherPassword(String token, SetAnotherPasswordSchema setAnotherPasswordSchema) throws NoSuchAlgorithmException {
        setAnotherPasswordSchema.validate();
        Pair<ResponseEntity<String>, ChangePasswordToken> p = checkTokenIntegrity(token);
        ResponseEntity re = p.getFirst();
        if (re.getStatusCode().is2xxSuccessful()) {
            ChangePasswordToken changePasswordToken = p.getSecond();
            changePasswordToken.setConfirmedDateTime(LocalDateTime.now());
            changePasswordToken.setStatus(ChangePasswordToken.PASSWORD_CHANGED);
            UserPetSitters user = userRepository.findByUsername(changePasswordToken.getUsername());

            MessageDigest md = MessageDigest.getInstance("MD5");
            String inputHash = "petsitterplot420 " + setAnotherPasswordSchema.getNewPassword();
            md.update(inputHash.getBytes());
            byte[] digest = md.digest();
            String hash = DatatypeConverter.printHexBinary(digest).toLowerCase();
            user.setPassword(hash);

            userRepository.save(user);
            resetPasswordTokenRepository.delete(changePasswordToken);
            return ResponseEntity.ok("You have successfully reset your password.");
        }
        return re;
    }
}