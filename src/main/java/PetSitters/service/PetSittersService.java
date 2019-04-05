package PetSitters.service;

import PetSitters.domain.Availability;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.ChangePasswordSchema;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.schemas.ModifySchema;
import PetSitters.schemas.RegisterSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableAutoConfiguration
public class PetSittersService {

    @Autowired
    UserRepository UserRep;

    public void register(RegisterSchema register) throws ParseException {
        register.validate();
        UserPetSitters newUser = new UserPetSitters(register);
        UserRep.save(newUser);
    }

    public void deleteAccount(DeleteAccountSchema account, String username) throws ExceptionInvalidAccount {
        account.validate();
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

    public void changePassword(ChangePasswordSchema changePassword, String username) throws ExceptionInvalidAccount {
        changePassword.validate();
        String password = changePassword.getOldPassword();
        UserPetSitters u = UserRep.findByUsername(username);
        if (u == null) {
            throw new ExceptionInvalidAccount("The account with the username '" + username + "' does not exist");
        }
        if (!u.isTheSamePassword(password)) {
            throw new ExceptionInvalidAccount("The username or password provided are incorrect");
        }
        u.setPassword(changePassword.getNewPassword());
        UserRep.save(u);
    }

    public void modify(String name, ModifySchema value, String user) {
        switch(name) {
            case "availability":
                modifyAvailability(value,user);
                break;
            case "description":
                modifyDescription(value,user);
                break;
            case "city":
                modifyCity(value,user);
                break;

            case "expert":
                modifyExpert(value,user);
                break;

            case "image":
                modifyImage(value,user);
                break;
        }
    }

    private void modifyDescription(ModifySchema value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setDescription(value.getToModify());
        UserRep.save(person);
    }

    private void modifyCity(ModifySchema value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setCity(value.getToModify());
        UserRep.save(person);
    }

    private void modifyExpert(ModifySchema value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setExpert(experts(value.getToModify()));
        UserRep.save(person);
    }

    private List<String> experts(String toModify) {
        String[] aux=toModify.split(" ");
        ArrayList<String> toret=new ArrayList<String>();
        for (String s:aux) toret.add(s);
        return new ArrayList<String>(toret);
    }

    private void modifyImage(ModifySchema value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setImage(value.getToModify());
        UserRep.save(person);
    }

    private void modifyAvailability(ModifySchema value, String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        Availability ava=new Availability(value.getToModify());
        person.setAvailability(ava);
        UserRep.save(person);
    }
}
