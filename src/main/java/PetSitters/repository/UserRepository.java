package PetSitters.repository;
import java.util.List;
import java.util.function.Function;

import PetSitters.entity.User;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

@Document(collection = "users")
public interface UserRepository extends MongoRepository<User, String> {

        List<User> findByFirstName(String firstName);
        User findByUsername(String username);
        void deleteByUsername(String username);
        boolean existsByUsername(String username);
    }

