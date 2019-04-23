package PetSitters.service;

import PetSitters.domain.City;
import PetSitters.domain.Coordinates;
import PetSitters.domain.Availability;
import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.repository.ReportRepository;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.*;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.min;

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
        String[] aux=toModify.split("''");
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

    public Coordinates getCoordinates(GetCoordinatesSchema getCoordinatesSchema) throws JSONException, IOException, ExceptionServiceError {
        getCoordinatesSchema.validate();
        City city = new City(getCoordinatesSchema.getCity());
        Coordinates coordinates = city.getCoordinates();
        return coordinates;
    }

    public List<LightUserSchema> getUsersLight(String username) {
       List<UserPetSitters> users=UserRep.findAll();
        UserPetSitters trueUser=UserRep.findByUsername(username);
        List<LightUserSchema> ret=new ArrayList<LightUserSchema>();
       for (UserPetSitters user:users) {
           if (notReported(trueUser.getEmail(),user.getEmail())) assignLightUserSchema(ret, user);
       }
       return ret;
    }

    public FullUserSchema getUserFull(String name) {
        UserPetSitters user=UserRep.findByUsername(name);
        FullUserSchema ret=new FullUserSchema();
        ret.setCommentaries(null);
        ret.setDescription(user.getDescription());
        ret.setName(user.getFirstName()+ " " + user.getLastName());
        if (user.getStars()==null) {
            ret.setStars(0);
        }
        else ret.setStars(user.getStars().intValue());
        if (user.getCity()==null) {
            ret.setLocalization("");
        }
        else ret.setLocalization(user.getCity());
        if (user.getImage()==null) {
            ret.setProfile_image("");
        }
        else ret.setProfile_image(user.getImage());
        ret.setUsername(user.getUsername());
        if (user.getExpert()==null) {
            ret.setExpert(null);
        }
        else ret.setExpert(user.getExpert());
        if (user.getAvailability()==null) {
            ret.setAvailability("None");
        }
        else ret.setAvailability(user.getAvailability().toString());
        return ret;
    }

    public List<LightUserSchema> getUsersExpert(String animal, String username) {
        List<UserPetSitters> users=UserRep.findAll();
        UserPetSitters trueUser=UserRep.findByUsername(username);
        List<LightUserSchema> toret= new ArrayList<LightUserSchema>();
        for (UserPetSitters user:users) {
            if (user.getExpert()!=null) {
                for (String expert : user.getExpert()) {
                    if (distance(animal, expert) <= 0.2 && notReported(trueUser.getEmail(), user.getEmail())) {
                        assignLightUserSchema(toret, user);
                    }
                    break;
                }
            }
        }
        return toret;
    }

    public List<LightUserSchema> getUsersName(String name, String username) {
        List<UserPetSitters> users=UserRep.findAll();
        UserPetSitters trueUser=UserRep.findByUsername(username);
        List<LightUserSchema> toret= new ArrayList<LightUserSchema>();
        for (UserPetSitters user:users) {
            String trueName=user.getFirstName()+" "+user.getLastName();
             if (trueName.toLowerCase().contains(name.toLowerCase()) && notReported(trueUser.getEmail(),user.getEmail())) {
                 assignLightUserSchema(toret, user);
             }
        }
        return toret;
    }

    private boolean notReported(String email1, String email2) {
        Boolean found=true;
        for (Report rep:ReportRep.findByReporter(email1)) {
            if (rep.getReported().equals(email2)) {
                found=false;
                break;
            }
        }
        return found;
    }

    private void assignLightUserSchema(List<LightUserSchema> toret, UserPetSitters user) {
        LightUserSchema us = new LightUserSchema();
        if (user.getStars() == null) {
            us.setStars(0);
        } else us.setStars(user.getStars().intValue());
        us.setName(user.getFirstName() + " " + user.getLastName());
        us.setProfile_pic(user.getImage());
        us.setUsername(user.getUsername());
        toret.add(us);
    }

    private double distance(String a, String b) {
        char[] arrayA=a.toCharArray();
        char[] arrayB=b.toCharArray();
        Integer union=0;
        Integer intersection=0;
        Integer i=0;
        while (i<arrayA.length && i<arrayB.length) {
            if (arrayA[i]==arrayB[i]) {
                intersection++;
            }
            ++union;
            ++i;
        }
        if (i<arrayA.length) {
            union=union+(arrayA.length-i);
        }
        else if (i<arrayB.length) {
            union=union+(arrayB.length-i);
        }
        return 1-((double) intersection / union);
	}
}
