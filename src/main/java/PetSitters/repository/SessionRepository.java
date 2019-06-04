package PetSitters.repository;

import PetSitters.entity.Session;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessionRepository extends MongoRepository<Session, String> {

    Session findByUsername(String username);
}
