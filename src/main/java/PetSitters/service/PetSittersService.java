package PetSitters.service;

import PetSitters.entity.User;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.LoginSchema;
import PetSitters.schemas.LogoutSchema;
import PetSitters.schemas.RegisterSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@EnableAutoConfiguration
public class PetSittersService {

    @Autowired
    UserRepository UserRep;

    public List<User> login(LoginSchema login) {
        User test=new User(login.getUser(),login.getPassword());
        test.setId("1");
        UserRep.save(test);
        return UserRep.findByFirstName("why");

    }

    public void logout(LogoutSchema logout) {
    }

    public void register(RegisterSchema register) throws ParseException {
        register.validate();
        User newUser = new User(register.getFirstName(), register.getLastName(), register.getUsername(), register.getPassword(), register.getBirthdate());
        UserRep.save(newUser);
    }
}
