package PetSitters.repository;

import PetSitters.entity.Chat;
import PetSitters.entity.Contract;
import PetSitters.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByIsVisibleAndUserWhoSendsAndUserWhoReceivesOrderByWhenSentDesc(Boolean b, String usernameWhoSends, String usernameWhoReceives);

    List<Message> findByUserWhoSendsAndUserWhoReceivesOrderByWhenSentDesc(String usernameWhoSends, String usernameWhoReceives);

    List<Message> findByUserWhoSendsAndIsVisible(String usernameWhoSends,Boolean isVisible);

    void deleteByUserWhoSendsAndUserWhoReceives(String usernameWhoSends, String usernameWhoReceives);

    List<Message> findByIsMultimediaAndUserWhoSendsAndUserWhoReceives(Boolean b, String usernameWhoSends, String usernameWhoReceives);

    boolean existsByIsVisibleAndUserWhoSendsAndUserWhoReceives(Boolean b, String usernameWhoSends, String usernameWhoReceives);

    boolean existsByUserWhoSendsAndUserWhoReceives(String username, String usernameA);
}
