package PetSitters.controller;

import PetSitters.auxiliary.ReadWebPage;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static PetSitters.security.Constants.ERROR_PAGE_PATH;

@Controller
public class CustomErrorHandler implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) throws IOException {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        Map<String, String> data = new HashMap<>();
        if (exception == null) {
            switch (statusCode) {
                case 401:
                    exception = new Exception("Not Authorized");
                    data.put("visibility", "visible");
                    break;
                default:
                    data.put("visibility", "none");
            }
        }
        data.put("statusCode", String.valueOf(statusCode));
        data.put("exception", exception==null? "N/A": exception.getMessage());
        ReadWebPage read = new ReadWebPage();
        String formattedString = read.getProcessedText(ERROR_PAGE_PATH, data);
        return formattedString;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}