package PetSitters.repository;

import PetSitters.entity.Commentary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "commentaries")
public interface CommentaryRepository extends MongoRepository<Commentary, String> {

    List<Commentary> findByCommenter(String commenter);

    List<Commentary> findByCommentedTo(String commentedTo);


}
