package PetSitters.repository;

import PetSitters.security.ChangePasswordToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResetPasswordTokenRepository extends MongoRepository<ChangePasswordToken, String> {
    ChangePasswordToken findByEmail(String email);
    List<ChangePasswordToken> findByToken(String token);
}
