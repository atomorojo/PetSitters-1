package PetSitters.controller;

import PetSitters.domain.Coordinates;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

@SuppressWarnings("ALL")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/petsitters")
@Api(value = "PetSittersApi", produces = MediaType.APPLICATION_JSON_VALUE )
public class PetSittersController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserServiceImpl userService;

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


    @PostMapping(value = "modify/{name}")
    @ApiOperation(value = "Modify the camp specified.")
    public ResponseEntity modify(@PathVariable String name,@RequestBody String toModify,@RequestHeader("Authorization") String token) throws ParseException, IOException {
        System.out.println(name);
        petSittersService.modify(name, toModify, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(name,HttpStatus.OK);
    }

    @GetMapping(value = "users")
    @ApiOperation(value = "Retrieve all users.")
    public ResponseEntity getUsers(@RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> users= petSittersService.getUsersLight(jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users,HttpStatus.OK);
    }

    @GetMapping(value = "user/filterExpert")
    @ApiOperation(value = "Retrieve all users that are expert in that animal.")
    public ResponseEntity getUsersExpert(@RequestParam String animal,@RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> users= petSittersService.getUsersExpert(animal,jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users,HttpStatus.OK);
    }

    @GetMapping(value = "user/filterDistance")
    @ApiOperation(value = "Retrieve all users that are in a radius equal to the paramater's value in km.")
    public ResponseEntity getUsersExpert(@RequestHeader("Authorization") String token,@RequestParam Integer rad) throws ParseException, IOException, JSONException, ExceptionServiceError {
        List<LightUserSchema> users= petSittersService.getUsersDistance(rad,jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users,HttpStatus.OK);
    }

    @GetMapping(value = "user/filterName")
    @ApiOperation(value = "Retrieve all users that have that string as a subset of their name.")
    public ResponseEntity getUsersName(@RequestParam String name,@RequestHeader("Authorization") String token) throws ParseException, IOException {
        List<LightUserSchema> users= petSittersService.getUsersName(name,jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(users,HttpStatus.OK);
    }

    @GetMapping(value = "/user/{name}")
    @ApiOperation(value = "Retrieve all information of a single user.")
    public ResponseEntity getSingleUser(@PathVariable String name) throws ParseException, IOException {
        FullUserSchema answer= petSittersService.getUserFull(name);
        return new ResponseEntity(answer,HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "store", consumes = {"multipart/form-data"}, produces = "application/json")
    @ApiOperation(value = "Store a file.")
    public ResponseEntity store(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        String username=jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length()));
        String name=gridFS.saveFile(file,username);
        return new ResponseEntity(name,HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "get/{name}")
    @ApiOperation(value = "Retrieve a file.")
    public ResponseEntity retrieve(@PathVariable String name) throws ParseException, IOException {
        GridFsResource file=gridFS.getFile(name);
        HttpHeaders headers = new HttpHeaders();
        if (file!=null) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getContentType())).contentLength(file.contentLength()).body(file);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @PostMapping(value="changePassword", headers="Accept=application/json")
    @ApiOperation(value ="Change the password of a user.")
    public ResponseEntity changePassword(@RequestBody ChangePasswordSchema changePassword, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.changePassword(changePassword, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    @ResponseBody
    public String verifyEmail(@RequestParam String code) {
        return verificationTokenService.verifyEmail(code).getBody();
    }

    @GetMapping("/resetPassword")
    @ApiOperation(value ="Checks the token sent by email and allows the user to reset his/her password. Afterwards, it shows a form which allow the user to introduce a new password.")
    @ResponseBody
    public String resetPassword(@RequestParam String code) throws ExceptionInvalidAccount, IOException, TemplateException {
        return passwordResetTokenRepository.sendFormPasswordReset(code).getBody();
    }

    @PostMapping(value="/resetPassword", headers="Accept=application/json")
    @ApiOperation(value ="Checks the token sent by email and allows the user to reset his/her password. It must be sent together with the new password.")
    @ResponseBody
    public String setAnotherPassword(@RequestParam String code, @RequestBody SetAnotherPasswordSchema setAnotherPasswordSchema) throws ExceptionInvalidAccount, IOException, TemplateException, NoSuchAlgorithmException {
        return passwordResetTokenRepository.setAnotherPassword(code, setAnotherPasswordSchema).getBody();
    }

    @PostMapping(value="/requestResetPassword", headers="Accept=application/json")
    @ApiOperation(value ="Sends an email with a link to a web page which allow a user to change his/her passoword.")
    public ResponseEntity requestResetPassword(@RequestBody ResetPasswordSchema resetPasswordSchema) throws ExceptionInvalidAccount {
        passwordResetTokenRepository.createRequest(resetPasswordSchema);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value="login", headers="Accept=application/json")
    @ApiOperation(value = "Login process.")
    public ApiResponse<AuthToken> register(@RequestBody LoginSchema loginUser) throws AuthenticationException {
        loginUser.validate();
        final UserPetSitters user = userService.findOne(loginUser.getUsername());
        if (user.isActive()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
            final String token = jwtTokenUtil.generateToken(user);
            return new ApiResponse<>(200, "success", new AuthToken(token, user.getUsername()));
        }
        else return new ApiResponse<>(401, "Account not activated",null);
    }

    @PostMapping(value = "register",headers="Accept=application/json")
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

    @PostMapping(value = "sendEmail",headers="Accept=application/json")
    @ApiOperation(value = "Send again an account confirmation email.")
    public ResponseEntity resendEmail(@RequestParam String username) throws ParseException {
        verificationTokenService.createVerification(userRep.findByUsername(username).getEmail());
        return new ResponseEntity(HttpStatus.OK);
    }



    @PostMapping(value = "registerNoMail",headers="Accept=application/json")
    @ApiOperation(value = "Register process.")
    public ResponseEntity registerNoMail(@RequestBody RegisterSchema register) throws ParseException {
        try {
            petSittersService.register(register);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Username and/or email already exists");
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping(value = "deleteAccount",headers="Accept=application/json")
    @ApiOperation(value = "Deletes an existent account.")
    public ResponseEntity deleteAccount(@RequestBody DeleteAccountSchema account, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        petSittersService.deleteAccount(account, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value="/report", headers="Accept=application/json")
    @ApiOperation(value ="Reports a user using its username.")
    @ResponseBody
    public ResponseEntity report(@RequestBody ReportSchema reportSchema, @RequestHeader("Authorization") String token)  throws ExceptionInvalidAccount {
        petSittersService.report(reportSchema, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value="/getCoordinates", headers="Accept=application/json")
    @ApiOperation(value = "Given the name of a city, returns the geographical position (latitude and longitude)")
    @ResponseBody
    public ResponseEntity getCoordinates(@RequestBody GetCoordinatesSchema getCoordinatesSchema) throws IOException, ExceptionServiceError, JSONException {
        Coordinates coordinates = petSittersService.getCoordinates(getCoordinatesSchema);
        return new ResponseEntity(coordinates, HttpStatus.OK);
    }

    @PostMapping(value="/startChat", headers="Accept=application/json")
    @ApiOperation(value = "Given the username of another user, starts a chat between both users.")
    public ResponseEntity startChat(@RequestBody StartChatSchema startChatSchema, @RequestHeader("Authorization") String token) throws ExceptionInvalidAccount {
        try {
            petSittersService.startChat(startChatSchema, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Chat already exists");
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}

