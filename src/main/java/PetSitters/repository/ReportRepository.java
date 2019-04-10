package PetSitters.repository;

import PetSitters.entity.Commentary;
import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "reports")
public interface ReportRepository extends MongoRepository<Report, String> {

    List<Report> findByReporter(String reporter);
    List<Report> findByReported(String reported);

}
