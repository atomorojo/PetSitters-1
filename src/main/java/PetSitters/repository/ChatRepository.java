package PetSitters.repository;

import PetSitters.entity.Chat;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "chats")
public interface ChatRepository extends MongoRepository<Chat, String> {
    Chat findByUsernameAAndUsernameB(String usernameA, String usernameB);

    List<Chat> findByUsernameAOrderByLastUseDesc(String usernameA);

    List<Chat> findByUsernameBOrderByLastUseDesc(String usernameB);

    List<Chat> findByUsernameA(String usernameA);

    List<Chat> findByUsernameB(String usernameB);

    void deleteByUsernameAAndUsernameB(String usernameA, String usernameB);
}
