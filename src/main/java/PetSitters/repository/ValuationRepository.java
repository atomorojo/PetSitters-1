package PetSitters.repository;

import PetSitters.entity.Valuation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "users")
public interface ValuationRepository extends MongoRepository<Valuation, String> {
    Long countByValuedUser(String valuedUsername);

    List<Valuation> findByUserWhoValuesAndValuedUser(String userWhoValues, String valuedUser);
}

