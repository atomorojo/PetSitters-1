package PetSitters.service;

import PetSitters.domain.Availability;
import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.repository.ReportRepository;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.ChangePasswordSchema;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.schemas.ModifySchema;
import PetSitters.schemas.RegisterSchema;
import PetSitters.schemas.ReportSchema;
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

    @Autowired
    ReportRepository ReportRep;

    private void checkExistence(UserPetSitters u, String username) throws ExceptionInvalidAccount {
        if (u == null) {
            throw new ExceptionInvalidAccount("The account with the username '" + username + "' does not exist");
        }
    }

    public void register(RegisterSchema register) throws ParseException {
        register.validate();
        UserPetSitters newUser = new UserPetSitters(register);
        UserRep.save(newUser);
    }

    public void deleteAccount(DeleteAccountSchema account, String username) throws ExceptionInvalidAccount {
        account.validate();
        String password = account.getPassword();
        UserPetSitters u = UserRep.findByUsername(username);
        checkExistence(u, username);
        if (!u.isTheSamePassword(password)) {
            throw new ExceptionInvalidAccount("The username or password provided are incorrect");
        }
        UserRep.deleteByUsername(username);
    }

    public void changePassword(ChangePasswordSchema changePassword, String username) throws ExceptionInvalidAccount {
        changePassword.validate();
        String password = changePassword.getOldPassword();
        UserPetSitters u = UserRep.findByUsername(username);
        checkExistence(u, username);
        if (!u.isTheSamePassword(password)) {
            throw new ExceptionInvalidAccount("The username or password provided are incorrect");
        }
        u.setPassword(changePassword.getNewPassword());
        UserRep.save(u);
    }
    public void modify(String name, String value, String user) {
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

    private void modifyDescription(String value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setDescription(value);
        UserRep.save(person);
    }

    private void modifyCity(String value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setCity(value);
        UserRep.save(person);
    }

    private void modifyExpert(String value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setExpert(experts(value));
        UserRep.save(person);
    }

    private List<String> experts(String toModify) {
        String[] aux=toModify.split(" ");
        ArrayList<String> toret=new ArrayList<String>();
        for (String s:aux) toret.add(s);
        return new ArrayList<String>(toret);
    }

    private void modifyImage(String value,String user) {
        UserPetSitters person=UserRep.findByUsername(user);
        person.setImage(value);
        UserRep.save(person);
    }

    private void modifyAvailability(String value, String user) {
        UserPetSitters person = UserRep.findByUsername(user);
        Availability ava = new Availability(value);
        person.setAvailability(ava);
        UserRep.save(person);

    }
    public void report(ReportSchema reportSchema, String reporterUsername) throws ExceptionInvalidAccount {
        reportSchema.validate();
        String reportedUsername = reportSchema.getReported();
        UserPetSitters reporter = UserRep.findByUsername(reporterUsername);
        checkExistence(reporter, reporterUsername);
        UserPetSitters reported = UserRep.findByUsername(reportedUsername);
        checkExistence(reported, reportedUsername);

        String reporterEmail = reporter.getEmail();
        String reportedEmail = reported.getEmail();

        Report r = new Report(reporterEmail, reportedEmail, reportSchema.getDescription());
        ReportRep.save(r);
    }
}
