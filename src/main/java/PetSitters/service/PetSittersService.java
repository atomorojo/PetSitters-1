package PetSitters.service;

import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.entity.User;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.LoginSchema;
import PetSitters.schemas.LogoutSchema;
import PetSitters.schemas.RegisterSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
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
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        String password=bCryptPasswordEncoder.encode(register.getPassword());
        User newUser = new User(register.getFirstName(), register.getLastName(), register.getUsername(), password, register.getBirthdate());
        UserRep.save(newUser);
    }

    public void deleteAccount(DeleteAccountSchema account) throws ExceptionInvalidAccount {
        // Needs to have an JWT
        account.validate();
        String username = account.getUsername();

        User u = UserRep.findByUsername(username);
        if (u == null) {
            throw new ExceptionInvalidAccount("The account with the username '" + username + "' does not exist");
        }
        UserRep.deleteByUsername(username);
    }
}
