package PetSitters.controller;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.*;
import PetSitters.security.*;
import PetSitters.service.GridFS;
import PetSitters.service.PetSittersService;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.text.ParseException;

@SuppressWarnings("ALL")
@RestController
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
    GridFS gridFS;



    @PostMapping(value = "modify/{name}")
    @ApiOperation(value = "Retrieve a file.")
    public ResponseEntity modify(@PathVariable String name,@RequestBody String toModify,@RequestHeader("Authorization") String token) throws ParseException, IOException {
        System.out.println(name);
        petSittersService.modify(name, toModify, jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length())));
        return new ResponseEntity(name,HttpStatus.OK);
    }



    @PostMapping(value = "store")
    @ApiOperation(value = "Store a file.")
    public ResponseEntity store(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) throws ParseException, IOException {
        String username=jwtTokenUtil.getUsernameFromToken(token.substring(7, token.length()));
        String name=gridFS.saveFile(file,username);
        return new ResponseEntity(name,HttpStatus.OK);
    }

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
    public String setAnotherPassword(@RequestParam String code, @RequestBody SetAnotherPasswordSchema setAnotherPasswordSchema) throws ExceptionInvalidAccount, IOException, TemplateException {
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
}

