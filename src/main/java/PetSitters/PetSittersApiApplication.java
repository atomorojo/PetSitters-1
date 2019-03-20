package PetSitters;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
public class PetSittersApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetSittersApiApplication.class, args);
	}


}
