package PetSitters.repository;
import java.util.List;

import PetSitters.entity.User;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Document(collection = "users")
public interface UserRepository extends MongoRepository<User, String> {

        public List<User> findByFirstName(String firstName);
        public User findByUsername(String username);
        public List<User> findByLastName(String lastName);

    }

