package PetSitters.controller;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.ChangePasswordSchema;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.schemas.LoginSchema;
import PetSitters.schemas.RegisterSchema;
import PetSitters.security.*;
import PetSitters.service.GridFS;
import PetSitters.service.PetSittersService;
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
    GridFS gridFS;



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
}

