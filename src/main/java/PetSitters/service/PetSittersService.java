package PetSitters.service;

import PetSitters.auxiliary.Pair;
import PetSitters.auxiliary.PushbackIterator;
import PetSitters.domain.Availability;
import PetSitters.domain.City;
import PetSitters.domain.Coordinates;
import PetSitters.entity.*;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.repository.*;
import PetSitters.schemas.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

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
        UserRep.deleteByUsername(username);
    }
    public void deleteAccountAdmin(String username) throws ExceptionInvalidAccount {
        if (UserRep.existsByUsername(username)) {
            UserPetSitters user = UserRep.findByUsername(username);
            if (user.getImage()!=null) gridFS.destroyFile(user.getImage());
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

    public Coordinates getCoordinates(GetCoordinatesSchema getCoordinatesSchema) throws JSONException, IOException, ExceptionServiceError {
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
            if (notReported(trueUser.getEmail(), user.getEmail())) assignLightUserSchema(ret, user);
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
        } else ret.setStars(user.getStars().intValue());
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

    public List<LightUserSchema> getUsersDistance(Integer rad, String username) throws JSONException, IOException, ExceptionServiceError {
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
        } else us.setStars(user.getStars().intValue());
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
            if (notReported(us.getEmail(), UserRep.findByUsername(s).getEmail())) us.addFavorites(s);
        }
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
                UserPetSitters userPetSitters = UserRep.findByUsername(cA.getUsernameB());
                ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getImage(), cA.getLastMessage());
                array.add(chatPreviewSchema);
                IB.pushback(cB);
            } else {
                UserPetSitters userPetSitters = UserRep.findByUsername(cB.getUsernameA());
                ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getImage(), cB.getLastMessage());
                array.add(chatPreviewSchema);
                IA.pushback(cA);
            }
        }
        while (IA.hasNext()) {
            Chat cA = IA.next();
            UserPetSitters userPetSitters = UserRep.findByUsername(cA.getUsernameB());
            ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getImage(), cA.getLastMessage());
            array.add(chatPreviewSchema);
        }
        while (IB.hasNext()) {
            Chat cB = IB.next();
            UserPetSitters userPetSitters = UserRep.findByUsername(cB.getUsernameA());
            ChatPreviewSchema chatPreviewSchema = new ChatPreviewSchema(userPetSitters.getFirstName() + " " + userPetSitters.getLastName(), userPetSitters.getImage(), cB.getLastMessage());
            array.add(chatPreviewSchema);
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

    public void proposeContract(ContractSchema contract, String usernameFromToken) {
        Contract c = ContRep.findByUsernameFromAndUsernameTo(usernameFromToken,contract.getUsername());
        if (c != null) {
            ContRep.delete(c);
        }
        Contract cont = new Contract();
        cont.setAnimal(contract.getAnimal());
        cont.setEnd(contract.getEnd());
        cont.setUsernameTo(contract.getUsername());
        cont.setUsernameFrom(usernameFromToken);
        cont.setFeedback(contract.getFeedback());
        cont.setAccepted(false);
        ContRep.save(cont);
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
        List<Report> res =ReportRep.findByReported(reported);
        return res;
    }
}
