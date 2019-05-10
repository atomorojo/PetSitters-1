package PetSitters.repository;

import PetSitters.entity.Chat;
import PetSitters.entity.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByUsernameFrom(String usernameA);
    List<Contract> findByUsernameTo(String usernameB);
    Contract findByUsernameToAndUsernameFrom(String usernameTo,String usernameFrom);

}
