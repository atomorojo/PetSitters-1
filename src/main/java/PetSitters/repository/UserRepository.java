package PetSitters.repository;

import PetSitters.entity.User;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

@Document(collection = "users")
public interface UserRepository extends MongoRepository<User, String> {

        public User findByPassword(String Passowrd);
        public List<User> findByFirstName(String firstName);
        public User findByUsername(String username);
        public List<User> findByLastName(String lastName);
        public void deleteByUsername(String username);
        public boolean existsByUsername(String username);

    }

