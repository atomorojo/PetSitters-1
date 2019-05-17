package PetSitters.controller;

import PetSitters.auxiliary.ReadWebPage;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static PetSitters.security.Constants.ADMINISTRATOR_PAGE_PATH;

@Controller
@Api(value = "PetSittersAdminApi", produces = MediaType.TEXT_HTML_VALUE)
public class PetSittersAdminController {

    @GetMapping("/admin")
    @ResponseBody
    public String defaultPage(HttpServletRequest request) throws IOException {
        ReadWebPage read = new ReadWebPage();
        String formattedString = read.getText(ADMINISTRATOR_PAGE_PATH);
        return formattedString;
    }
}