package PetSitters.repository;

import PetSitters.security.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    VerificationToken findByEmail(String email);
    List<VerificationToken> findByToken(String token);
}