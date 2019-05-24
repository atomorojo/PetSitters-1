package PetSitters.repository;

import PetSitters.entity.Commentary;
import PetSitters.entity.Contract;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "contracts")
public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByUsernameFrom(String usernameA);

    List<Contract> findByUsernameTo(String usernameB);

    Contract findByUsernameFromAndUsernameTo(String usernameFrom, String usernameTo);

}
