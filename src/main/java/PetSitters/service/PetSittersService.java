package PetSitters.service;

import PetSitters.auxiliary.PushbackIterator;
import PetSitters.domain.Availability;
import PetSitters.domain.City;
import PetSitters.domain.Coordinates;
import PetSitters.entity.*;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.repository.*;
import PetSitters.schemas.*;
import PetSitters.serviceDTO.DTOTranslationIncoming;
import PetSitters.serviceDTO.DTOTranslationOutgoing;
import PetSitters.serviceLocator.ServiceLocator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@EnableAutoConfiguration
public class PetSittersService {

    @Autowired
    UserRepository UserRep;

    @Autowired
    ReportRepository ReportRep;

    @Autowired
    ChatRepository ChatRep;

    @Autowired
    ContractRepository ContRep;

    @Autowired
    GridFS gridFS;

    @Autowired
    MessageRepository MessageRep;

    @Autowired
    SessionRepository SessionRep;


    @Autowired
    TrophyService Trophy;

    @Autowired
    ValuationRepository ValuationRep;

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
        if (u.getImage()!=null) gridFS.destroyFile(u.getImage());
        deleteAllChats(username);
        killContract(username);
        UserRep.deleteByUsername(username);
    }
    public void deleteAccountAdmin(String username) throws ExceptionInvalidAccount {
        if (UserRep.existsByUsername(username)) {
            UserPetSitters user = UserRep.findByUsername(username);
            if (user.getImage()!=null) gridFS.destroyFile(user.getImage());
            deleteAllChats(username);
            killContract(username);
            UserRep.deleteByUsername(username);
        }
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
        switch (name) {
            case "availability":
                modifyAvailability(value, user);
                break;
            case "description":
                modifyDescription(value, user);
                break;
            case "city":
                modifyCity(value, user);
                break;
            case "expert":
                modifyExpert(value, user);
                break;

            case "image":
                modifyImage(value, user);
                break;
        }
        Trophy.trophy01(UserRep.findByUsername(user));
    }

    private void modifyDescription(String value, String user) {
        UserPetSitters person = UserRep.findByUsername(user);
        person.setDescription(value);
        UserRep.save(person);
    }

    private void modifyCity(String value, String user) {
        UserPetSitters person = UserRep.findByUsername(user);
        person.setCity(value);
        UserRep.save(person);
    }

    private void modifyExpert(String value, String user) {
        UserPetSitters person = UserRep.findByUsername(user);
        person.setExpert(experts(value));
        UserRep.save(person);
    }

    private List<String> experts(String toModify) {
        String[] aux = toModify.split("''");
        ArrayList<String> toret = new ArrayList<String>();
        for (String s : aux) toret.add(s);
        return new ArrayList<String>(toret);
    }

    private void modifyImage(String value, String user) {
        UserPetSitters person = UserRep.findByUsername(user);
        person.setImage(value);
        UserRep.save(person);
    }

    private void modifyAvailability(String value, String user) {
        UserPetSitters person = UserRep.findByUsername(user);
        Availability ava = new Availability(value);
        person.setAvailability(ava);
        UserRep.save(person);

    }

    private Date getTimestamp() {
        Date date = new Date();
        return date;
    }

    public void report(ReportSchema reportSchema, String reporterUsername) throws ExceptionInvalidAccount {
        reportSchema.validate();
        String reportedUsername = reportSchema.getReported();
        if (reporterUsername.equals(reportedUsername)) {
            throw new ExceptionInvalidAccount("A user cannot report himself");
        }

        UserPetSitters reporter = UserRep.findByUsername(reporterUsername);
        checkExistence(reporter, reporterUsername);
        UserPetSitters reported = UserRep.findByUsername(reportedUsername);
        checkExistence(reported, reportedUsername);

        String reporterEmail = reporter.getEmail();
        String reportedEmail = reported.getEmail();

        Report r = new Report(reporterEmail, reportedEmail, reportSchema.getDescription());
        ReportRep.save(r);
    }

    public Coordinates getCoordinates(GetCoordinatesSchema getCoordinatesSchema) throws Exception {
        getCoordinatesSchema.validate();
        City city = new City(getCoordinatesSchema.getCity());
        Coordinates coordinates = city.getCoordinates();
        return coordinates;
    }

    public List<LightUserSchema> getUsersLight(String username) {
        List<UserPetSitters> users = UserRep.findAll();
        UserPetSitters trueUser = UserRep.findByUsername(username);
        List<LightUserSchema> ret = new ArrayList<LightUserSchema>();
        for (UserPetSitters user : users) {
            if (notReported(trueUser.getEmail(), user.getEmail()) && !username.equals(user.getUsername())) assignLightUserSchema(ret, user);
        }
        return ret;
    }

    public FullUserSchema getUserFull(String name) {
        UserPetSitters user = UserRep.findByUsername(name);
        FullUserSchema ret = new FullUserSchema();
        ret.setCommentaries(null);
        ret.setDescription(user.getDescription());
        ret.setName(user.getFirstName() + " " + user.getLastName());
        if (user.getStars() == null) {
            ret.setStars(0);
        } else ret.setStars((int) Math.round(user.getStars()));
        if (user.getCity() == null) {
            ret.setLocalization("");
        } else ret.setLocalization(user.getCity());
        if (user.getImage() == null) {
            ret.setProfile_image("");
        } else ret.setProfile_image(user.getImage());
        ret.setUsername(user.getUsername());
        if (user.getExpert() == null) {
            ret.setExpert(null);
        } else ret.setExpert(user.getExpert());
        if (user.getAvailability() == null) {
            ret.setAvailability("None");
        } else ret.setAvailability(user.getAvailability().toString());
        return ret;
    }

    public List<LightUserSchema> getUsersExpert(String animal, String username) {
        List<UserPetSitters> users = UserRep.findAll();
        UserPetSitters trueUser = UserRep.findByUsername(username);
        List<LightUserSchema> toret = new ArrayList<LightUserSchema>();
        for (UserPetSitters user : users) {
            if (user.getExpert() != null) {
                if (!trueUser.getUsername().equals(user.getUsername()))
                    for (String expert : user.getExpert()) {
                        if (distance(animal, expert) <= 0.2 && notReported(trueUser.getEmail(), user.getEmail())) {
                            assignLightUserSchema(toret, user);
                            break;
                        }
                    }
            }
        }
        return toret;
    }

    public List<LightUserSchema> getUsersDistance(Integer rad, String username) throws Exception {
        List<LightUserSchema> toret = new ArrayList<LightUserSchema>();
        UserPetSitters trueUser = UserRep.findByUsername(username);
        if (trueUser.getCity() != null) {
            List<UserPetSitters> users = UserRep.findAll();
            for (UserPetSitters user : users) {
                if (!trueUser.getUsername().equals(user.getUsername()))
                    if (user.getCity() != null) {
                        City city1 = new City(user.getCity());
                        City city2 = new City(trueUser.getCity());
                        Coordinates coord2 = city1.getCoordinates();
                        Coordinates coord1 = city2.getCoordinates();
                        Double distanceKm = distance(coord1.getLatitude(), coord1.getLongitude(), coord2.getLatitude(), coord2.getLongitude());
                        System.out.println(distanceKm);
                        if (distanceKm <= rad) {
                            assignLightUserSchema(toret, user);
                        }
                    }
            }
        }
        return toret;
    }


    public List<LightUserSchema> getUsersName(String name, String username) {
        List<UserPetSitters> users = UserRep.findAll();
        UserPetSitters trueUser = UserRep.findByUsername(username);
        List<LightUserSchema> toret = new ArrayList<LightUserSchema>();
        for (UserPetSitters user : users) {
            String trueName = user.getFirstName() + " " + user.getLastName();
            if (!trueUser.getUsername().equals(user.getUsername()))
                if (trueName.toLowerCase().contains(name.toLowerCase()) && notReported(trueUser.getEmail(), user.getEmail())) {
                    assignLightUserSchema(toret, user);
                }
        }
        return toret;
    }

    private boolean notReported(String email1, String email2) {
        Boolean found = true;
        for (Report rep : ReportRep.findByReporter(email1)) {
            if (rep.getReported().equals(email2)) {
                found = false;
                break;
            }
        }
        return found;
    }

    private void assignLightUserSchema(List<LightUserSchema> toret, UserPetSitters user) {
        LightUserSchema us = new LightUserSchema();
        if (user.getStars() == null) {
            us.setStars(0);
        } else us.setStars((int) Math.round(user.getStars()));
        us.setName(user.getFirstName() + " " + user.getLastName());
        us.setProfile_pic(user.getImage());
        us.setUsername(user.getUsername());
        toret.add(us);
    }

    private double distance(String a, String b) {
        char[] arrayA = a.toCharArray();
        char[] arrayB = b.toCharArray();
        Integer union = 0;
        Integer intersection = 0;
        Integer i = 0;
        while (i < arrayA.length && i < arrayB.length) {
            if (arrayA[i] == arrayB[i]) {
                intersection++;
            }
            ++union;
            ++i;
        }
        if (i < arrayA.length) {
            union = union + (arrayA.length - i);
        } else if (i < arrayB.length) {
            union = union + (arrayB.length - i);
        }
        System.out.println(1 - ((double) intersection / union));
        return 1 - ((double) intersection / union);
    }

    public void addFavorites(String userList, String usernameFromToken) {
        String[] users = userList.split(",");
        UserPetSitters us = UserRep.findByUsername(usernameFromToken);
        for (String s : users) {
            if (notReported(us.getEmail(), UserRep.findByUsername(s).getEmail())) {
                us.addFavorites(s);
                Trophy.trophy03(UserRep.findByUsername(s));
            }
        }
        Trophy.trophy02(us);
        UserRep.save(us);
    }

    public List<LightUserSchema> getFavorites(String usernameFromToken) {
        UserPetSitters us = UserRep.findByUsername(usernameFromToken);
        List<LightUserSchema> toret = new ArrayList<LightUserSchema>();
        for (String fav : us.getFavorites()) {
            UserPetSitters favorited = UserRep.findByUsername(fav);
            assignLightUserSchema(toret, favorited);
        }
        return toret;
    }

    public void unsetFavorites(String userList, String usernameFromToken) {
        String[] users = userList.split(",");
        UserPetSitters us = UserRep.findByUsername(usernameFromToken);
        for (String s : users) {
            us.removeFavorites(s);
        }
        UserRep.save(us);

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = Math.abs(lon1 - lon2);
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private boolean hasAccess(Chat c, String username) {
        String otherUsername;
        if (username.equals(c.getUsernameB())) otherUsername = c.getUsernameA();
        else otherUsername = c.getUsernameB();
        boolean hasVisibleMessages = MessageRep.existsByIsVisibleAndUserWhoSendsAndUserWhoReceives(true, otherUsername, username);
        hasVisibleMessages = hasVisibleMessages || MessageRep.existsByUserWhoSendsAndUserWhoReceives(username, otherUsername);
        return c.hasAccess(username) && hasVisibleMessages;
    }

    public List<ChatPreviewSchema> getOpenedChats(String username) {
        List<Chat> chatsA = ChatRep.findByUsernameAOrderByLastUseDesc(username);
        List<Chat> chatsB = ChatRep.findByUsernameBOrderByLastUseDesc(username);
        LinkedList<ChatPreviewSchema> array = new LinkedList<>();
        // Merge two sorted lists
        PushbackIterator<Chat> IA = new PushbackIterator<>(chatsA.iterator());
        PushbackIterator<Chat> IB = new PushbackIterator<>(chatsB.iterator());

        while (IA.hasNext() && IB.hasNext()) {
            Chat cA = IA.next();
            Chat cB = IB.next();

            if (cA.isLastUsed(cB)) {
                if (hasAccess(cA, username)) {
                    UserPetSitters userPetSitters = UserRep.findByUsername(cA.getUsernameB());
                    ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getUsername(), userPetSitters.getImage(), cA.getLastMessage());
                    array.add(chatPreviewSchema);
                }
                IB.pushback(cB);
            } else {
                if (hasAccess(cB, username)) {
                    UserPetSitters userPetSitters = UserRep.findByUsername(cB.getUsernameA());
                    ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getUsername(), userPetSitters.getImage(), cB.getLastMessage());
                    array.add(chatPreviewSchema);
                }
                IA.pushback(cA);
            }
        }
        while (IA.hasNext()) {
            Chat cA = IA.next();
            if (hasAccess(cA, username)) {
                UserPetSitters userPetSitters = UserRep.findByUsername(cA.getUsernameB());
                ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getUsername(), userPetSitters.getImage(), cA.getLastMessage());
                array.add(chatPreviewSchema);
            }
        }
        while (IB.hasNext()) {
            Chat cB = IB.next();
            if (hasAccess(cB, username)) {
                UserPetSitters userPetSitters = UserRep.findByUsername(cB.getUsernameA());
                ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getUsername(), userPetSitters.getImage(), cB.getLastMessage());
                array.add(chatPreviewSchema);
            }
        }
        return array;
    }


    // ------------------------------------ DEBUG ---------------------------------------
    private void DEBUGclearAll() {
        UserRep.deleteAll();
        ChatRep.deleteAll();
    }

    public void DEBUGload() throws ParseException {
        DEBUGclearAll();
        register(new RegisterSchema("Alexandra", "Volkova", "alexandra", "ff788efa931cc5b6018695fbb6999911", "Barcelona","juan1@juan.com", "20-10-1998"));
        register(new RegisterSchema("Daniel", "Esquina", "daniel", "ff788efa931cc5b6018695fbb6999911", "Lleida", "juan2@juan.com", "20-10-1998"));
        register(new RegisterSchema("Hector", "Baiges", "hector", "ff788efa931cc5b6018695fbb6999911", "Mataro", "juan3@juan.com", "20-10-1998"));
        register(new RegisterSchema("Ruben", "Gonzalex", "ruben", "ff788efa931cc5b6018695fbb6999911", "Valencia", "juan4@juan.com", "20-10-1998"));
        register(new RegisterSchema("David", "Garcia", "david", "ff788efa931cc5b6018695fbb6999911", "Igualada", "juan5@juan.com", "20-10-1998"));
        register(new RegisterSchema("Pere", "Bruy", "pere", "ff788efa931cc5b6018695fbb6999911", "Palencia", "juan6@juan.com", "20-10-1998"));
        register(new RegisterSchema("George", "Bochileanu", "george", "ff788efa931cc5b6018695fbb6999911", "Madrid", "juan7@juan.com", "20-10-1998"));
        register(new RegisterSchema("Antoni", "Casas", "antoni", "ff788efa931cc5b6018695fbb6999911", "Toledo", "juan8@juan.com", "20-10-1998"));

        UserPetSitters user = UserRep.findByUsername("alexandra");
        user.setActive(true);
        UserRep.save(user);

        user = UserRep.findByUsername("daniel");
        user.setActive(true);
        UserRep.save(user);

        user = UserRep.findByUsername("hector");
        user.setActive(true);
        UserRep.save(user);

        user = UserRep.findByUsername("ruben");
        user.setActive(true);
        UserRep.save(user);

        user = UserRep.findByUsername("david");
        user.setActive(true);
        UserRep.save(user);

        user = UserRep.findByUsername("pere");
        user.setActive(true);
        UserRep.save(user);

        user = UserRep.findByUsername("george");
        user.setActive(true);
        UserRep.save(user);

        user = UserRep.findByUsername("antoni");
        user.setActive(true);
        UserRep.save(user);
    }

    public void DEBUGloadWithChats() throws ParseException, ExceptionInvalidAccount {
        DEBUGclearAll();
        DEBUGload();
        sendMessage(new MessageSchema("daniel", "false", "Hola, qué tal?"), "alexandra");
        sendMessage(new MessageSchema("hector", "false", "Hola, 123"), "daniel");
        sendMessage(new MessageSchema("daniel", "false", "Hola, 123"), "ruben");
        sendMessage(new MessageSchema("daniel", "false", "Hola, 123"), "pere");
        sendMessage(new MessageSchema("pere", "false", "Hola, 4321"), "daniel");

        report(new ReportSchema("antoni", "None"), "daniel");
        report(new ReportSchema("daniel", "None"), "hector");
    }

    public JSONArray DEBUGfindAll() {
        List<UserPetSitters> users = UserRep.findAll();
        JSONArray array = new JSONArray();
        for (UserPetSitters user : users) {
            array.put(user.getUsername());
        }
        return array;
    }

    // -----------------------------------------------------------------------------------

    public void proposeContract(ContractSchema contract, String usernameFromToken) throws Exception {
        UserPetSitters trueUser=UserRep.findByUsername(usernameFromToken);
        UserPetSitters user=UserRep.findByUsername(contract.getUsername());
        if (trueUser == null) {
            throw new ExceptionInvalidAccount("The specified username '" + usernameFromToken + "' does not belong to any user in the system");
        }
        if (user == null) {
            throw new ExceptionInvalidAccount("The specified username '" + contract.getUsername() + "' does not belong to any user in the system");
        }
        Contract c = ContRep.findByUsernameFromAndUsernameTo(usernameFromToken,contract.getUsername());
        if (c != null) {
            ContRep.delete(c);
        }
        Contract cont = new Contract();
        cont.setAnimal(contract.getAnimal());
        cont.setEnd(contract.getEnd());
        cont.setStart(contract.getStart());
        cont.setUsernameTo(contract.getUsername());
        cont.setUsernameFrom(usernameFromToken);
        cont.setFeedback(contract.getFeedback());
        cont.setAccepted(false);
        ContRep.save(cont);
        if (user.getCity() != null && trueUser.getCity()!=null) {
            City city1 = new City(user.getCity());
            City city2 = new City(trueUser.getCity());
            Coordinates coord2 = city1.getCoordinates();
            Coordinates coord1 = city2.getCoordinates();
            Double distanceKm = distance(coord1.getLatitude(), coord1.getLongitude(), coord2.getLatitude(), coord2.getLongitude());
            System.out.println(distanceKm);
            if (distanceKm>=50) {
                Trophy.trophy15(trueUser);
            }
        }
    }

    public void acceptContract(String usernameB, String usernameFromToken) {
        Contract cont = ContRep.findByUsernameFromAndUsernameTo(usernameB, usernameFromToken);
        if (cont != null) {
            cont.setAccepted(true);
            ContRep.save(cont);
        }
    }

    public void rejectContract(String usernameB, String usernameFromToken) {
        Contract cont = ContRep.findByUsernameFromAndUsernameTo(usernameB, usernameFromToken);
        if (cont != null) {
            ContRep.delete(cont);
        }
        cont = ContRep.findByUsernameFromAndUsernameTo(usernameFromToken, usernameB);
        if (cont != null) {
            ContRep.delete(cont);
        }

    }
    public void killContract(String usernameFromToken) {
        List<Contract> cont = ContRep.findByUsernameFrom(usernameFromToken);
        if (cont != null) {
            for (Contract c:cont) {
                ContRep.delete(c);
            }
        }
        cont= ContRep.findByUsernameTo(usernameFromToken);
        if (cont != null) {
            for (Contract c:cont) {
                ContRep.delete(c);
            }        }

    }


    public List<Contract> contractListProposed(String usernameFromToken) {
        List<Contract> cont = ContRep.findByUsernameFrom(usernameFromToken);
        return cont;
    }

    public List<Contract> contractListReceived(String usernameFromToken) {
        List<Contract> cont = ContRep.findByUsernameTo(usernameFromToken);
        return cont;
    }

    public Contract isContracted(String usernameB, String usernameFromToken) {
        Contract cont = ContRep.findByUsernameFromAndUsernameTo(usernameB, usernameFromToken);
        return cont;
    }

    public void setProfileImage(String username, String name) {
        UserPetSitters user = UserRep.findByUsername(username);
        user.setImage(name);
        Trophy.trophy01(user);
        UserRep.save(user);
    }

    public void sendMessage(MessageSchema messageSchema, String usernameWhoSends) throws ExceptionInvalidAccount {
        messageSchema.validate();
        String usernameWhoReceives = messageSchema.getUserWhoReceives();
        if (!UserRep.existsByUsername(usernameWhoReceives)) {
            throw new ExceptionInvalidAccount("The specified username '" + usernameWhoReceives + "' does not belong to any user in the system");
        }
        if (!UserRep.existsByUsername(usernameWhoSends)) {
            throw new ExceptionInvalidAccount("The specified username '" + usernameWhoSends + "' does not belong to any user in the system");
        }
        if (usernameWhoSends.equals(usernameWhoReceives)) {
            throw new ExceptionInvalidAccount("A user cannot start a chat with himself");
        }
        UserPetSitters userWhoSends = UserRep.findByUsername(usernameWhoSends);
        UserPetSitters userWhoReceives = UserRep.findByUsername(usernameWhoReceives);

        boolean blocked = ReportRep.existsByReportedAndReporter(userWhoSends.getEmail(), userWhoReceives.getEmail());
        blocked = blocked || ReportRep.existsByReportedAndReporter(userWhoReceives.getEmail(), userWhoSends.getEmail());

        Date timestamp = getTimestamp();

        String lastMessage = messageSchema.getContent();
        if (messageSchema.getIsMultimedia()) {
            lastMessage = "Multimedia file";
        }
        updateChat(usernameWhoReceives, usernameWhoSends, timestamp, lastMessage);

        Message message = new Message(messageSchema, usernameWhoSends, timestamp, !blocked);

        MessageRep.save(message);
        Trophy.trophy12_14(userWhoSends);
        UserPetSitters us=UserRep.findByUsername(messageSchema.getUserWhoReceives());
        us.setNotificationChat(true);
        UserRep.save(us);
    }

   public LinkedList<Message> getAllMessagesFromChat(Integer threshold, String usernameWhoReceives, String usernameWhoSends) throws ExceptionInvalidAccount {
        if (!UserRep.existsByUsername(usernameWhoSends)) {
           throw new ExceptionInvalidAccount("The specified username '" + usernameWhoSends + "' does not belong to any user in the system");
        }
        if (!UserRep.existsByUsername(usernameWhoReceives)) {
            throw new ExceptionInvalidAccount("The specified username '" + usernameWhoReceives + "' does not belong to any user in the system");
        }

        List<Message> messageListSender = MessageRep.findByUserWhoSendsAndUserWhoReceivesOrderByWhenSentDesc(usernameWhoSends, usernameWhoReceives);
        List<Message> messageListReceiver = MessageRep.findByIsVisibleAndUserWhoSendsAndUserWhoReceivesOrderByWhenSentDesc(true, usernameWhoReceives, usernameWhoSends);

        LinkedList<Message> array = new LinkedList<>();
        // Merge two sorted lists
        PushbackIterator<Message> ISender = new PushbackIterator<>(messageListSender.iterator());
        PushbackIterator<Message> IReceiver = new PushbackIterator<>(messageListReceiver.iterator());

        int i = 0;

        while (ISender.hasNext() && IReceiver.hasNext() && (threshold == null || i < threshold)) {
            Message mSender = ISender.next();
            Message mReceiver = IReceiver.next();

            if (mSender.isLastSent(mReceiver)) {
                array.addFirst(mSender);
                IReceiver.pushback(mReceiver);
            } else {
                array.addFirst(mReceiver);
                ISender.pushback(mSender);
            }
            ++i;
        }
        while (ISender.hasNext() && (threshold == null || i < threshold)) {
            Message mSender = ISender.next();
            array.addFirst(mSender);
            ++i;
        }
        while (IReceiver.hasNext() && (threshold == null || i < threshold)) {
            Message mReceiver = IReceiver.next();
            array.addFirst(mReceiver);
            ++i;
        }
        return array;
    }

    private void updateChat(String otherUser, String userWhoStarts, Date timestamp, String lastMessage) throws ExceptionInvalidAccount {
        // We sort the usernames lexicographically so as to know which one comes first
        String usernameA, usernameB;
        if (userWhoStarts.compareTo(otherUser) < 0) {
            usernameA = userWhoStarts;
            usernameB = otherUser;
        } else {
            usernameA = otherUser;
            usernameB = userWhoStarts;
        }
        Chat chat = new Chat(usernameA, usernameB, timestamp, lastMessage);
        ChatRep.deleteByUsernameAAndUsernameB(usernameA, usernameB);
        ChatRep.save(chat);
    }

    public List<Report> getReports(String reported) {
        String email=UserRep.findByUsername(reported).getEmail();
        List<Report> res =ReportRep.findByReported(email);
        return res;
    }

    public List<GetAllReportsSchema> getAllReportedUsers() {
        List<Report> reps=ReportRep.findAll();
        Set<String> emails=new HashSet<>();
        for (Report r:reps) {
            if (UserRep.existsByEmail(r.getReported()) && !emails.contains(r.getReported())) emails.add(r.getReported());
        }
        List<GetAllReportsSchema> result=new ArrayList<>();
        for (String email:emails) {
            UserPetSitters us=UserRep.findByEmail(email);
            GetAllReportsSchema rep=new GetAllReportsSchema();
            rep.setEmail(email);
            rep.setFirstName(us.getFirstName());
            rep.setLastName(us.getLastName());
            rep.setUsername(us.getUsername());
            rep.setReports(ReportRep.findByReported(email).size());
            result.add(rep);
        }
		return result;
	}

    private void deleteAllMultimedia(List<Message> list) {
        for (Message message: list) {
            if (gridFS.getFile(message.getContent()) != null) {
                System.out.println("Trying to delete..." + message.getContent());
                gridFS.destroyFile(message.getContent());
            }
        }
    }

   public void deleteChat(DeleteChatSchema deleteChatSchema, String usernameWhoDeletes) throws ExceptionInvalidAccount {
       deleteChatSchema.validate();
       String otherUsername = deleteChatSchema.getOtherUsername();
       deleteChat(otherUsername, usernameWhoDeletes);
   }

    private String getOtherUsername(String usernameA, String usernameB, String usernameWhoDeletes) {
        if (usernameWhoDeletes.equals(usernameA)) return usernameB;
        return usernameA;
    }

    public void deleteAllChats(String usernameWhoDeletes) throws ExceptionInvalidAccount {
        List<Chat> listOfChatByA = ChatRep.findByUsernameA(usernameWhoDeletes);
        for (Chat chat: listOfChatByA) {
            String otherUsername = getOtherUsername(chat.getUsernameA(), chat.getUsernameB(), usernameWhoDeletes);
            deleteChat(otherUsername, usernameWhoDeletes);
        }
        List<Chat> listOfChatByB = ChatRep.findByUsernameB(usernameWhoDeletes);
        for (Chat chat: listOfChatByB) {
            String otherUsername = getOtherUsername(chat.getUsernameA(), chat.getUsernameB(), usernameWhoDeletes);
            deleteChat(otherUsername, usernameWhoDeletes);
        }
    }

    private void deleteChat(String otherUsername, String usernameWhoDeletes) throws ExceptionInvalidAccount {
        if (!UserRep.existsByUsername(usernameWhoDeletes)) {
            throw new ExceptionInvalidAccount("The specified username '" + usernameWhoDeletes + "' does not belong to any user in the system");
        }

        String usernameA, usernameB;
        if (usernameWhoDeletes.compareTo(otherUsername) < 0) {
            usernameA = usernameWhoDeletes;
            usernameB = otherUsername;
        } else {
            usernameA = otherUsername;
            usernameB = usernameWhoDeletes;
        }

        Chat chat = ChatRep.findByUsernameAAndUsernameB(usernameA, usernameB);
        if (chat == null) {
            throw new ExceptionInvalidAccount("Chat does not exist");
        }
        String chatUsernameWhoHasNoAccess = chat.getUsernameWhoHasNoAccess();

        if (chatUsernameWhoHasNoAccess == null) {
            chat.setUsernameWhoHasNoAccess(usernameWhoDeletes);
            ChatRep.save(chat);
        } else if (chatUsernameWhoHasNoAccess.equals(usernameWhoDeletes)) {
            throw new ExceptionInvalidAccount("Chat already deleted");
        } else if (chatUsernameWhoHasNoAccess.equals(otherUsername) || !UserRep.existsByUsername(otherUsername)) {
            // Delete media
            List<Message> mediaChatsAB = MessageRep.findByIsMultimediaAndUserWhoSendsAndUserWhoReceives(true, usernameWhoDeletes, otherUsername);
            deleteAllMultimedia(mediaChatsAB);
            List<Message> mediaChatsBA = MessageRep.findByIsMultimediaAndUserWhoSendsAndUserWhoReceives(true, otherUsername, usernameWhoDeletes);
            deleteAllMultimedia(mediaChatsBA);

            MessageRep.deleteByUserWhoSendsAndUserWhoReceives(usernameWhoDeletes, otherUsername);
            MessageRep.deleteByUserWhoSendsAndUserWhoReceives(otherUsername, usernameWhoDeletes);
            ChatRep.deleteByUsernameAAndUsernameB(usernameA, usernameB);
        }
	}

    public List<LightUserSchema> getUsersValoration(Integer upperBound, Integer lowerBound, String usernameFromToken) {
        List<UserPetSitters> users = UserRep.findAll();
        List<LightUserSchema> toret = new ArrayList<LightUserSchema>();
        for (UserPetSitters user : users) {
            if (user.getStars() != null) {
                if (!usernameFromToken.equals(user.getUsername())) {
                    if (user.getStars() >= lowerBound && user.getStars() <= upperBound) {
                        assignLightUserSchema(toret, user);
                    }
                }
            }
        }
        return toret;
    }

    public void saveValuation(ValuationSchema valuationSchema, String usernameWhoValues) throws ExceptionInvalidAccount {
        valuationSchema.validate();
        if (!UserRep.existsByUsername(usernameWhoValues)) {
            throw new ExceptionInvalidAccount("The specified username '" + usernameWhoValues + "' does not belong to any user in the system");
        }
        if (!UserRep.existsByUsername(valuationSchema.getValuedUser())) {
            throw new ExceptionInvalidAccount("The specified username '" + valuationSchema.getValuedUser() + "' does not belong to any user in the system");
        }
        if (usernameWhoValues.equals(valuationSchema.getValuedUser())) {
            throw new ExceptionInvalidAccount("A user cannot value himself");
        }
        Contract contract = ContRep.findByUsernameFromAndUsernameTo(usernameWhoValues, valuationSchema.getValuedUser());
        if (contract == null) {
            throw new ExceptionInvalidAccount("The valuation is not associated to any contract in the system");
        }
        String valuedUsername = valuationSchema.getValuedUser();

        Long countValuations = ValuationRep.countByValuedUser(valuedUsername);

        Valuation valuation = new Valuation(valuationSchema, usernameWhoValues, new Date(), contract.getAnimal());
        ValuationRep.save(valuation);

        UserPetSitters userPetSitters = UserRep.findByUsername(valuedUsername);
        Double average = userPetSitters.getStars();

        Double sum = average * ((double) countValuations);
        Double newAverage = (sum + (double) valuation.getstars()) / ((double) countValuations + 1);
        UserRep.delete(userPetSitters);
        userPetSitters.setStars(newAverage);
        UserRep.save(userPetSitters);

        Trophy.trophy06(userPetSitters);
        Trophy.trophy07(userPetSitters);
        Trophy.trophy08(userPetSitters);
        Trophy.trophy09(userPetSitters);
        Trophy.trophy10(userPetSitters);
        Trophy.trophy11(userPetSitters);
        Trophy.trophy16_45(userPetSitters);
    }

    public LinkedList<ValuationPreviewSchema> getValuations(String username) throws ExceptionInvalidAccount {
        if (!UserRep.existsByUsername(username)) {
            throw new ExceptionInvalidAccount("The specified username '" + username + "' does not belong to any user in the system");
        }
        List<Valuation> valuations = ValuationRep.findByValuedUser(username);
        LinkedList<ValuationPreviewSchema> array = new LinkedList<>();

        for (Valuation valuation : valuations) {
            UserPetSitters userPetSitters = UserRep.findByUsername(valuation.getUserWhoValues());
            ValuationPreviewSchema valuationPreviewSchema = new ValuationPreviewSchema(valuation.getUserWhoValues(), userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), valuation.getDate(), userPetSitters.getImage(), valuation.getStars(), valuation.getCommentary());
            array.addLast(valuationPreviewSchema);
        }

        return array;
    }

    public Boolean[] getTrophies(String usernameFromToken) {
        UserPetSitters us = UserRep.findByUsername(usernameFromToken);
        return us.getTrophy();
    }

    public LinkedList<String> translate(TranslationSchema translationSchema) throws Exception {
        translationSchema.validate();
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        PetSitters.serviceLocator.Service service = serviceLocator.find("Translation");
        LinkedList<String> toList = new LinkedList<>();
        for (String s : translationSchema.getInputInEnglish()) toList.addLast(s);
        DTOTranslationIncoming dtoToTranslate = new DTOTranslationIncoming(toList, translationSchema.getOutputLanguage());
        DTOTranslationOutgoing result = (DTOTranslationOutgoing) service.execute(dtoToTranslate);
        return result.getText();

    }

    @Scheduled(fixedDelay=100000)
    public void contractCheck() throws ParseException {
        List<Contract> contr=ContRep.findAll();
        for (Contract c:contr) {
            //Massive ball of fucking spaghetti code, why is this format used in front-end
            String[] aberration=c.getEnd().split("-");
            String[] worse=aberration[2].split(",");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            String newDate=worse[0]+"-"+aberration[1]+"-"+aberration[0];
            Date dtIn = inFormat.parse(newDate);
            Date today=new Date();
            //Don't touch this or it breaks, its spaghetti
            long diffInMillies = Math.abs(today.getTime() - dtIn.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diff<=0) {
                UserPetSitters u=UserRep.findByUsername(c.getUsernameFrom());
                u.setNotificationValue(true);
                UserRep.save(u);
            }
        }
    }

    public Boolean[] getNotifications(String usernameFromToken) {
        Boolean[] b=new Boolean[3];
        UserPetSitters us=UserRep.findByUsername(usernameFromToken);
        b[0]=us.getNotificationChat();
        b[1]=us.getNotificationTrophy();
        b[2]=us.getNotificationValue();
        return b;
    }
}
