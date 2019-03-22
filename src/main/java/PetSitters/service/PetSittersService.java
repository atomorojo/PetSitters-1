package PetSitters.service;

import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.entity.UserPetSitters;
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

    public List<UserPetSitters> login(LoginSchema login) {
        UserPetSitters test=new UserPetSitters(login.getUsername(),login.getPassword());
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
        UserPetSitters newUser = new UserPetSitters(register.getFirstName(), register.getLastName(), register.getUsername(), password, register.getBirthdate());
        UserRep.save(newUser);
    }

    public void deleteAccount(DeleteAccountSchema account) throws ExceptionInvalidAccount {
        // Needs to have an JWT
        String username=account.getUsername();
        UserPetSitters u = UserRep.findByUsername(username);
        if (u == null) {
            throw new ExceptionInvalidAccount("The account with the username '" + username + "' does not exist");
        }
        UserRep.deleteByUsername(username);
    }
}
