package PetSitters.security;

public class Constants {


    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
    public static final String SIGNING_KEY = "petsitters";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SWAGGER_URL = "/swagger-ui.html";
    public static final String REGISTER_URL = "/petsitters/register";
    //public static final String REGISTER_NO_MAIL_URL = "/petsitters/registerNoMail";//
    public static final String REQUEST_RESET_PASSWORD_URL = "/petsitters/requestResetPassword";
    public static final String RESET_PASSWORD_URL = "/petsitters/resetPassword";
    public static final String LOGIN_URL = "/petsitters/login";
    public static final String EMAIL_URL = "/petsitters/email-verification";
    public static final String EMAIL_URL_VERIFY = "/petsitters/verify-email";
    public static final String EMAIL_URL_RESEND = "/petsitters/sendEmail";
    public static final String ADMIN_REPORTS =  "/petsitters/getUserReports";
    public static final String ADMIN_DELETE =  "/petsitters/deleteUserAccount";
    public static final String ADMIN_USERS = "/petsitters/getAllReportedUsers";

    public static final String CHANGE_PASSWORD_WEB_PAGE_PATH = "/src/main/resources/webPages/change-password.html";
    public static final String ERROR_EMAIL_CONFIRMATION_WEB_PAGE_PATH = "/src/main/resources/webPages/error-email-confirmation.html";
    public static final String OK_EMAIL_CONFIRMATION_WEB_PAGE_PATH = "/src/main/resources/webPages/ok-email-confirmation.html";
    public static final String ERROR_PAGE_PATH = "/src/main/resources/webPages/error-page.html";
    public static final String ADMINISTRATOR_PAGE_PATH = "/src/main/resources/webPages/administrator.html";

    public static final String DEBUG_PATH = "/petsitters/DEBUG*";
    public static final String ADMINISTRATOR_PATH = "/admin";
}