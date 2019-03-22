package PetSitters.service;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.LoginSchema;
import PetSitters.schemas.RegisterSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
@Service
@EnableAutoConfiguration
public class PetSittersService {

    @Autowired
    UserRepository UserRep;

    public List<UserPetSitters> login(LoginSchema login) {
        UserPetSitters test = new UserPetSitters(login.getUser(),login.getPassword());
        test.setId("1");
        UserRep.save(test);
        return UserRep.findByFirstName("why");
    }

    public void register(RegisterSchema register) throws ParseException {
        register.validate();
        UserPetSitters newUser = new UserPetSitters(register);
        UserRep.save(newUser);
    }

    public void deleteAccount(DeleteAccountSchema account) throws ExceptionInvalidAccount {
        account.validate();
        String username = account.getUsername();
        String password = account.getPassword();

        UserPetSitters u = UserRep.findByUsername(username);

        if (u == null) {
            throw new ExceptionInvalidAccount("The account with the username '" + username + "' does not exist");
        }

        if (!u.isTheSamePassword(password)) {
            throw new ExceptionInvalidAccount("The username or password provided are incorrect");
        }

        UserRep.deleteByUsername(username);
    }
}
