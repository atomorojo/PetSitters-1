package PetSitters.controller;

import PetSitters.exception.ExceptionInvalidAccount;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected ModelMap ValidationException(Exception ex, WebRequest request) {
        ModelMap map = new ModelMap();
        map.addAttribute("error", ex.getMessage());
        return map;
    }
    @ExceptionHandler(value = DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ModelMap DuplicateKeyException(Exception ex, WebRequest request) {
        ModelMap map = new ModelMap();
        map.addAttribute("error", ex.getMessage());
        return map;
    }
    @ExceptionHandler(value = ExceptionInvalidAccount.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ModelMap ExceptionInvalidAccount(Exception ex, WebRequest request) {
        ModelMap map = new ModelMap();
        map.addAttribute("error", ex.getMessage());
        return map;
    }
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected ModelMap ExceptionNullPointer(Exception ex, WebRequest request) {
        ModelMap map = new ModelMap();
        map.addAttribute("error", ex.getMessage());
        return map;
    }
}