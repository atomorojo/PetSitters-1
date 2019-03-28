package PetSitters.controller;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.schemas.LoginSchema;
import PetSitters.schemas.RegisterSchema;
import PetSitters.security.ApiResponse;
import PetSitters.security.AuthToken;
import PetSitters.security.JwtTokenUtil;
import PetSitters.security.UserServiceImpl;
import PetSitters.service.PetSittersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "login", method = RequestMethod.POST,headers="Accept=application/json")
    @ApiOperation(value = "Login process.")
    public ApiResponse<AuthToken> register(@RequestBody LoginSchema loginUser) throws AuthenticationException {
        loginUser.validate();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        final UserPetSitters user = userService.findOne(loginUser.getUsername());
        final String token = jwtTokenUtil.generateToken(user);
        return new ApiResponse<>(200, "success",new AuthToken(token, user.getUsername()));
    }

    @PostMapping(value = "register",headers="Accept=application/json")
    @ApiOperation(value = "Register process.")
    public ResponseEntity register(@RequestBody RegisterSchema register) throws ParseException {
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

