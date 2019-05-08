package PetSitters.repository;

import PetSitters.entity.Chat;
import PetSitters.entity.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByUsernameA(String usernameA);
    List<Contract> findByUsernameB(String usernameB);
    Contract findByUsernameBAndUsernameA(String usernameB,String usernameA);

}
