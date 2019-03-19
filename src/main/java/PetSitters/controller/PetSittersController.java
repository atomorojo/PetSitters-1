package PetSitters.controller;

import PetSitters.entity.User;
import PetSitters.schemas.LoginSchema;
import PetSitters.schemas.LogoutSchema;
import PetSitters.schemas.RegisterSchema;
import PetSitters.service.PetSittersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@SuppressWarnings("ALL")
@RestController
@RequestMapping("/petsitters")
@Api(value = "PetSittersApi", produces = MediaType.APPLICATION_JSON_VALUE)
public class PetSittersController {

    @Autowired
    PetSittersService petSittersService;

    @PostMapping(value = "login")
    @ApiOperation(value = "Login process.")
    public ResponseEntity<User> login(@RequestBody LoginSchema login) {
        List<User> us = petSittersService.login(login);
        return new ResponseEntity(us, HttpStatus.OK);
    }
    @PostMapping(value = "logout")
    @ApiOperation(value = "Logout process.")
    public ResponseEntity<User> logout(@RequestBody LogoutSchema logout) {
        petSittersService.logout(logout);
        return new ResponseEntity(HttpStatus.OK);
    }
    @PostMapping(value = "register")
    @ApiOperation(value = "Register process.")
    public ResponseEntity<User> register(@RequestBody RegisterSchema register) throws ParseException {
        petSittersService.register(register);
        return new ResponseEntity(HttpStatus.OK);
    }

}

