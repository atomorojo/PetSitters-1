package PetSitters.repository;

import PetSitters.entity.User;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

@Document(collection = "users")
public interface UserRepository extends MongoRepository<User, String> {

        public User findByUsername(String Username);
        public User findByPassword(String Passowrd);

    }

