package PetSitters.repository;

import PetSitters.entity.Report;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Document(collection = "reports")
public interface ReportRepository extends MongoRepository<Report, String> {

    List<Report> findByReporter(String reporter);

    List<Report> findByReported(String reported);

    boolean existsByReportedAndReporter(String reported, String reporter);

}
