package PetSitters.repository;

import PetSitters.entity.Chat;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "chats")
public interface ChatRepository extends MongoRepository<Chat, String> {
    Chat findByUsernameAAndUsernameB(String usernameA, String usernameB);
}