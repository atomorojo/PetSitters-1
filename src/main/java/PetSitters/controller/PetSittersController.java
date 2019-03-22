package PetSitters.controller;

import PetSitters.entity.UserPetSitters;
import PetSitters.exception.ExceptionInvalidAccount;
import PetSitters.schemas.DeleteAccountSchema;
import PetSitters.schemas.RegisterSchema;
import PetSitters.service.PetSittersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@SuppressWarnings("ALL")
@RestController
@RequestMapping("/petsitters")
@Api(value = "PetSittersApi", produces = MediaType.APPLICATION_JSON_VALUE)
public class PetSittersController {

    @Autowired
    PetSittersService petSittersService;

    @PostMapping(value = "register")
    @ApiOperation(value = "Register process.")
    public ResponseEntity<UserPetSitters> register(@RequestBody RegisterSchema register) throws ParseException {
        petSittersService.register(register);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "deleteAccount")
    @ApiOperation(value = "Deletes an existent account.")
    public ResponseEntity<UserPetSitters> deleteAccount(@RequestBody DeleteAccountSchema account) throws ExceptionInvalidAccount {
        petSittersService.deleteAccount(account);
        return new ResponseEntity(HttpStatus.OK);
    }

}

