package PetSitters.security;

public class Constants {



        public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5*60*60;
        public static final String SIGNING_KEY = "petsitters";
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_STRING = "Authorization";
        public static final String SWAGGER_URL = "/swagger-ui.html";
        public static final String REGISTER_URL = "/petsitters/register";
        public static final String LOGIN_URL =  "/petsitters/login";
        public static final String EMAIL_URL= "/petsitters/email-verification";
        public static final String EMAIL_URL_VERIFY="/petsitters/verify-email";
}