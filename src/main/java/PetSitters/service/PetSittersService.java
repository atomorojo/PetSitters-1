package PetSitters.service;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.RegisterSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
@Service
@EnableAutoConfiguration
public class PetSittersService {

    @Autowired
    UserRepository UserRep;

    public void register(RegisterSchema register) throws ParseException {
        register.validate();
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        String password=bCryptPasswordEncoder.encode(register.getPassword());
        UserPetSitters newUser = new UserPetSitters(register.getFirstName(), register.getLastName(), register.getUsername(), password, register.getBirthdate());
        UserRep.save(newUser);
    }

    public void deleteAccount(DeleteAccountSchema account) throws ExceptionInvalidAccount {
        account.validate();
        String password = account.getPassword();
        String username=account.getUsername();
        UserPetSitters u = UserRep.findByUsername(username);
        if (u == null) {
            throw new ExceptionInvalidAccount("The account with the username '" + username + "' does not exist");
        }
        if (!new BCryptPasswordEncoder().matches(password,u.getPassword())) {
            throw new ExceptionInvalidAccount("The username or password provided are incorrect");
        }

        UserRep.deleteByUsername(username);
    }
}
