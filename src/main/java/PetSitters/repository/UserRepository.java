package PetSitters.repository;

import PetSitters.entity.UserPetSitters;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "users")
public interface UserRepository extends MongoRepository<UserPetSitters, String> {

        List<UserPetSitters> findByFirstName(String firstName);
        UserPetSitters findByUsername(String username);
        boolean existsByUsername(String username);
        void deleteByUsername(String username);
        UserPetSitters findByEmail(String email);

        List<UserPetSitters> findAll();
}


