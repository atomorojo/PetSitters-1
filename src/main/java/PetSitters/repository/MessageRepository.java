package PetSitters.repository;

import PetSitters.entity.Contract;
import PetSitters.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByIsVisibleAndUserWhoSendsAndUserWhoReceivesOrderByWhenSentDesc(Boolean b, String usernameWhoSends, String usernameWhoReceives);

    List<Message> findByUserWhoSendsAndUserWhoReceivesOrderByWhenSentDesc(String usernameWhoSends, String usernameWhoReceives);
}
