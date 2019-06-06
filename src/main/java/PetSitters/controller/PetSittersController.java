package PetSitters.controller;

import PetSitters.domain.Coordinates;
import PetSitters.entity.Contract;
import PetSitters.entity.Message;
import PetSitters.entity.Report;
import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.exception.ExceptionServiceError;
import PetSitters.repository.UserRepository;
import PetSitters.schemas.*;
import PetSitters.security.*;
import PetSitters.service.GridFS;
import PetSitters.service.PetSittersService;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("ALL")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/petsitters")
@Api(value = "PetSittersApi", produces = MediaType.APPLICATION_JSON_VALUE)
public class PetSittersController {
    @Autowired
    PetSittersService petSittersService;
    @Autowired
    VerificationTokenService verificationTokenService;
    @Autowired
    PasswordResetTokenService passwordResetTokenRepository;
    @Autowired
    UserRepository userRep;
    @Autowired
    GridFS gridFS;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserServiceImpl userService;

    @PostMapping(value = "/setProfileImage")
    @ApiOperation(value = "Sets the profile image.")
    public ResponseEntity setProfileImage(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length()));
        String name = gridFS.saveFile(file, username);
        petSittersService.setProfileImage(username, name);
        return new ResponseEntity(name, HttpStatus.OK);
    }

    @PostMapping(value = "/modify/{name}")
    @ApiOperation(value = "Modify the camp specified.")
    public ResponseEntity modify(@PathVariable String name, @RequestBody StringAux toModify, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        petSittersService.modify(name, toModify.getThing(), jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(name, HttpStatus.OK);
    }

    @PostMapping(value = "/addFavorites")
    @ApiOperation(value = "Add the users specified in the param, separated by a \",\" to the list of favorites of that user.")
    public ResponseEntity addFavorites(@RequestParam String userList, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        petSittersService.addFavorites(userList, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/unsetFavorites")
    @ApiOperation(value = "Add the users specified in the param, separated by a \",\" to the list of favorites of that user.")
    public ResponseEntity unsetFavorites(@RequestParam String userList, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        petSittersService.unsetFavorites(userList, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping(value = "/getFavorites")
    @ApiOperation(value = "Retrieve all the favorite users of this user.")
    public ResponseEntity getFavorites(@RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> favs = petSittersService.getFavorites(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(favs, HttpStatus.OK);
    }


    @GetMapping(value = "/users")
    @ApiOperation(value = "Retrieve all users.")
    public ResponseEntity getUsers(@RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> users = petSittersService.getUsersLight(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping(value = "/user/filterExpert")
    @ApiOperation(value = "Retrieve all users that are expert in that animal.")
    public ResponseEntity getUsersExpert(@RequestParam String animal, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> users = petSittersService.getUsersExpert(animal, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping(value = "/user/filterValoration")
    @ApiOperation(value = "Retrieve all users that are between the upper and lower bounds in their valoration score.")
    public ResponseEntity getUsersExpert(@RequestParam Integer upperBound,@RequestParam Integer lowerBound, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> users = petSittersService.getUsersValoration(upperBound,lowerBound, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping(value = "/user/filterDistance")
    @ApiOperation(value = "Retrieve all users that are in a radius equal to the paramater's value in km.")
    public ResponseEntity getUsersExpert(@RequestHeader("Authorization") String token, @RequestParam Integer rad) throws Exception {
        List<LightUserSchema> users = petSittersService.getUsersDistance(rad, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping(value = "/user/filterName")
    @ApiOperation(value = "Retrieve all users that have that string as a subset of their name.")
    public ResponseEntity getUsersName(@RequestParam String name, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> users = petSittersService.getUsersName(name, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{name}")
    @ApiOperation(value = "Retrieve all information of a single user.")
    public ResponseEntity getSingleUser(@PathVariable String name) throws ParseException, IOException {
        FullUserSchema answer = petSittersService.getUserFull(name);
        return new ResponseEntity(answer, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/store", consumes = {"multipart/form-data"}, produces = "application/json")
    @ApiOperation(value = "Store a file.")
    public ResponseEntity store(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length()));
        String name = gridFS.saveFile(file, username);
        return new ResponseEntity(name, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/get/{name}")
    @ApiOperation(value = "Retrieve a file.")
    public ResponseEntity retrieve(@PathVariable String name) throws ParseException, IOException {
        GridFsResource file = gridFS.getFile(name);
        HttpHeaders headers = new HttpHeaders();
        if (file != null) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getContentType())).contentLength(file.contentLength()).body(file);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @PostMapping(value = "/changePassword", headers = "Accept=application/json")
    @ApiOperation(value = "Change the password of a user.")
    public ResponseEntity changePassword(@RequestBody ChangePasswordSchema changePassword, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.changePassword(changePassword, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    @ResponseBody
    public String verifyEmail(@RequestParam String code) throws IOException {
        return verificationTokenService.verifyEmail(code).getBody();
    }

    @GetMapping("/resetPassword")
    @ApiOperation(value = "Checks the token sent by email and allows the user to reset his/her password. Afterwards, it shows a form which allow the user to introduce a new password.")
    @ResponseBody
    public String resetPassword(@RequestParam String code) throws ExceptionInvalidAccount, IOException, TemplateException {
        return passwordResetTokenRepository.sendFormPasswordReset(code).getBody();
    }

    @PostMapping(value = "/resetPassword", headers = "Accept=application/json")
    @ApiOperation(value = "Checks the token sent by email and allows the user to reset his/her password. It must be sent together with the new password.")
    @ResponseBody
    public String setAnotherPassword(@RequestParam String code, @RequestBody SetAnotherPasswordSchema setAnotherPasswordSchema) throws ExceptionInvalidAccount, IOException, TemplateException, NoSuchAlgorithmException {
        return passwordResetTokenRepository.setAnotherPassword(code, setAnotherPasswordSchema).getBody();
    }

    @PostMapping(value = "/requestResetPassword", headers = "Accept=application/json")
    @ApiOperation(value = "Sends an email with a link to a web page which allow a user to change his/her passoword.")
    public ResponseEntity requestResetPassword(@RequestBody ResetPasswordSchema resetPasswordSchema) throws ExceptionInvalidAccount {
        passwordResetTokenRepository.createRequest(resetPasswordSchema);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/login", headers = "Accept=application/json")
    @ApiOperation(value = "Login process.")
    public ApiResponse<AuthToken> register(@RequestBody LoginSchema loginUser) throws AuthenticationException {
        loginUser.validate();
        final UserPetSitters user = userService.findOne(loginUser.getUsername());
        if (user.isActive()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
            final String token = jwtTokenUtil.generateToken(user);
            return new ApiResponse<>(200, "success", new AuthToken(token, user.getUsername()));
        } else return new ApiResponse<>(401, "Account not activated", null);
    }

    @PostMapping(value = "/register", headers = "Accept=application/json")
    @ApiOperation(value = "Register process.")
    public ResponseEntity register(@RequestBody RegisterSchema register) throws ParseException {
        try {
            petSittersService.register(register);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Username and/or email already exists");
        }
        verificationTokenService.createVerification(register.getEmail());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/sendEmail", headers = "Accept=application/json")
    @ApiOperation(value = "Send again an account confirmation email.")
    public ResponseEntity resendEmail(@RequestParam String username) throws ParseException {
        verificationTokenService.createVerification(userRep.findByUsername(username).getEmail());
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping(value = "/registerNoMail", headers = "Accept=application/json")
    @ApiOperation(value = "Register process.")
    public ResponseEntity registerNoMail(@RequestBody RegisterSchema register) throws ParseException {
        try {
            petSittersService.register(register);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Username and/or email already exists");
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping(value = "/deleteAccount", headers = "Accept=application/json")
    @ApiOperation(value = "Deletes an existent account.")
    public ResponseEntity deleteAccount(@RequestBody DeleteAccountSchema account, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.deleteAccount(account, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/report", headers = "Accept=application/json")
    @ApiOperation(value = "Reports a user using its username.")
    @ResponseBody
    public ResponseEntity report(@RequestBody ReportSchema reportSchema, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.report(reportSchema, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/getCoordinates", headers = "Accept=application/json")
    @ApiOperation(value = "Given the name of a city, returns the geographical position (latitude and longitude)")
    @ResponseBody
    public ResponseEntity getCoordinates(@RequestBody GetCoordinatesSchema getCoordinatesSchema) throws Exception {
        Coordinates coordinates = petSittersService.getCoordinates(getCoordinatesSchema);
        return new ResponseEntity(coordinates, HttpStatus.OK);
    }

    @GetMapping(value = "/getOpenedChats")
    @ApiOperation(value = "Returns all the opened chats of a user. Specifically, it returns usernames who have started previously a chat with this person. The returned array starts with the oldest chat.")
    public ResponseEntity getOpenedChats(@RequestHeader("Authorization") String token) throws ExceptionInvalidAccount, JSONException {
        List<ChatPreviewSchema> response = petSittersService.getOpenedChats(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(value = "/DEBUGloadDefault")
    @ApiOperation(value = "Loads 4 users in the database")
    public ResponseEntity loadDefault() throws ParseException {
        petSittersService.DEBUGload();
        return new ResponseEntity("The context has been successfully loaded!", HttpStatus.OK);
    }

    @GetMapping(value = "/DEBUGloadWithChats")
    @ApiOperation(value = "Loads 4 users in the database and starts some chats without messages")
    public ResponseEntity loadWithChats() throws ParseException, ExceptionInvalidAccount {
        petSittersService.DEBUGloadWithChats();
        return new ResponseEntity("The context has been successfully loaded!", HttpStatus.OK);
    }

    @GetMapping(value = "/DEBUGgetAllUsers")
    @ApiOperation(value = "Gets all the usernames in the System")
    public ResponseEntity getAllUsernames() throws ParseException, ExceptionInvalidAccount {
        JSONArray array = petSittersService.DEBUGfindAll();
        return new ResponseEntity(array.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/proposeContract", headers = "Accept=application/json")
    @ApiOperation(value = "Given the username of another user, and the contract information, begins a contract but doen't accept it.")
    public ResponseEntity proposeContract(@RequestBody ContractSchema contract, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        try {
            petSittersService.proposeContract(contract, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Contract already exists");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExceptionServiceError exceptionServiceError) {
            exceptionServiceError.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/acceptContract", headers = "Accept=application/json")
    @ApiOperation(value = "Given the username of another user, accepts the contract proposed.")
    public ResponseEntity acceptContract(@RequestParam String contract, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.acceptContract(contract, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/rejectContract", headers = "Accept=application/json")
    @ApiOperation(value = "Given the username of another user, rejects the proposed contract.")
    public ResponseEntity rejectContract(@RequestParam String contract, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.rejectContract(contract, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/getProposedContracts", headers = "Accept=application/json")
    @ApiOperation(value = "Gets all the contracts that this user proposed.")
    public ResponseEntity contractListProposed(@RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        List<Contract> res = petSittersService.contractListProposed(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(res, HttpStatus.OK);
    }

    @GetMapping(value = "/getReceivedContracts", headers = "Accept=application/json")
    @ApiOperation(value = "Gets all the contracts that this user received.")
    public ResponseEntity contractListReceived(@RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        List<Contract> res = petSittersService.contractListReceived(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(res, HttpStatus.OK);
    }

    @GetMapping(value = "/isContracted", headers = "Accept=application/json")
    @ApiOperation(value = "Returns the contract that has been set between the 2 users, if it exists.")
    public ResponseEntity isContracted(@RequestParam String contract, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        Contract res = petSittersService.isContracted(contract, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(res, HttpStatus.OK);
    }
    @DeleteMapping(value = "/deleteUserAccount", headers = "Accept=application/json")
    @ApiOperation(value = "Deletes the account, only admins can execute this action.")
    public ResponseEntity deleteAccountAdmin(@RequestParam String adminToken, @RequestParam String toDelete) throws ExceptionInvalidAccount {
        if (adminToken.equals("111122223333444455556666")) {
            petSittersService.deleteAccountAdmin(toDelete);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/getUserReports", headers = "Accept=application/json")
    @ApiOperation(value = "Gets the reports of this user, only admins can execute this action.")
    public ResponseEntity getUserReports(@RequestParam String adminToken, @RequestParam String reported) throws ExceptionInvalidAccount {
        if (adminToken.equals("111122223333444455556666")) {
            List<Report> reportList=petSittersService.getReports(reported);
            return new ResponseEntity(reportList,HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @GetMapping(value = "/getAllReportedUsers", headers = "Accept=application/json")
    @ApiOperation(value = "Gets all users that have been reported, only admins can execute this action.")
    public ResponseEntity getAllReportedUsers(@RequestParam String adminToken) throws ExceptionInvalidAccount {
        if (adminToken.equals("111122223333444455556666")) {
            List<GetAllReportsSchema> reportList=petSittersService.getAllReportedUsers();
            return new ResponseEntity(reportList,HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/delete/{name}", headers = "Accept=application/json")
    @ApiOperation(value = "Deletes the account, only admins can execute this action.")
    public ResponseEntity getUserReports(@PathVariable String name) throws ExceptionInvalidAccount {
        if (gridFS.getFile(name)!=null) {
            gridFS.destroyFile(name);
            return new ResponseEntity(HttpStatus.OK);
        }
        else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/sendMessage", headers = "Accept=application/json")
    @ApiOperation(value = "Sends a message to a user.")
    public ResponseEntity sendMessage(@RequestBody MessageSchema messageSchema, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.sendMessage(messageSchema, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/getMessagesFromChat")
    @ApiOperation(value = "Gets all messages from a chat. Limit indicates the quantity of messages to return, if it is not declared, it returns all the messages.")
    public ResponseEntity getMessagesFromChat(@RequestParam String userWhoReceives, @RequestParam(value = "limit", required = false) Integer limit, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        LinkedList<Message> result = petSittersService.getAllMessagesFromChat(limit, userWhoReceives, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteChat")
    @ApiOperation(value = "Deletes a chat between two users. If both users delete the chat, this is erased for good. ")
    public ResponseEntity deleteChat(@RequestBody DeleteChatSchema deleteChatSchema, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.deleteChat(deleteChatSchema, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/saveValuation", headers = "Accept=application/json")
    @ApiOperation(value = "Saves a valuation. ")
    public ResponseEntity saveValuation(@RequestBody ValuationSchema valuationSchema, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.saveValuation(valuationSchema, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/getValuations")
    @ApiOperation(value = "Gets all valuations of the logged user ")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Success", response = ValuationPreviewSchema.class, responseContainer = "List")})
    public ResponseEntity getValuations(@RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        LinkedList<ValuationPreviewSchema> array = petSittersService.getValuations(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(array, HttpStatus.OK);
    }

    @GetMapping(value = "/getTrophies")
    @ApiOperation(value = "Gets all trophies of the logged user ")
    public ResponseEntity getTrophies(@RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        Boolean[] ret = petSittersService.getTrophies(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(ret, HttpStatus.OK);
    }
    @GetMapping(value = "/hasContracted", headers = "Accept=application/json")
    @ApiOperation(value = "Returns the contract that has been set between the 2 users, if it exists.")
    public ResponseEntity hasContracted(@RequestParam String contract, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        Contract res = petSittersService.isContracted(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())),contract);
        return new ResponseEntity(res, HttpStatus.OK);
    }

    @PostMapping(value = "/translate", headers = "Accept=application/json")
    @ApiOperation(value = "Translates the given text into another language. See the attached document in Drive: 'DocumentacioMultiidioma.pdf' in order to know how to pass the language. ")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Success", response = String.class, responseContainer = "List")})
    public ResponseEntity translate(@RequestBody TranslationSchema translationSchema) throws Exception {
        LinkedList<String> array = petSittersService.translate(translationSchema);
        return new ResponseEntity(array, HttpStatus.OK);
    }
    @GetMapping(value = "/getNotifications", headers = "Accept=application/json")
    @ApiOperation(value = "Gets notifications. Position 1 is chat, position 2 is trophy, position 3 is valuation, 4 is needing to give feedback")
    public ResponseEntity hasContracted(@RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        Boolean[] nots=petSittersService.getNotifications(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(nots,HttpStatus.OK);
    }
    @GetMapping(value = "/nullifyNotifications", headers = "Accept=application/json")
    @ApiOperation(value = "Kills notifications")
    public ResponseEntity nullifyNotifications(@RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.nullifyNotifications(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping(value = "/getValuationsFromUser")
    @ApiOperation(value = "Gets all valuations of a given user. ")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Success", response = ValuationPreviewSchema.class, responseContainer = "List")})
    public ResponseEntity getValuationsFromUser(@RequestParam String user) throws ExceptionInvalidAccount {
        petSittersService.nullifyNotifications(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/getTrophiesRanking")
    @ApiOperation(value = "Gets all users ordered by number of trophies. From highest to lowest. ")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Success", response = TrophiesRankingPreviewSchema.class, responseContainer = "List")})
    public ResponseEntity getTrophiesRanking() throws ExceptionInvalidAccount {
        LinkedList<TrophiesRankingPreviewSchema> array = petSittersService.getTrophiesRanking();
        return new ResponseEntity(array, HttpStatus.OK);
    }
}
