package PetSitters.repository;

import PetSitters.entity.UserPetSitters;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

@Document(collection = "users")
public interface UserRepository extends MongoRepository<UserPetSitters, String> {

        public UserPetSitters findByPassword(String Passowrd);
        public List<UserPetSitters> findByFirstName(String firstName);
        public UserPetSitters findByUsername(String username);
        public List<UserPetSitters> findByLastName(String lastName);
        public void deleteByUsername(String username);
        public boolean existsByUsername(String username);

    }

