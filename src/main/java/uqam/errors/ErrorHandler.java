/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.errors;

import javax.servlet.http.HttpServletResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
/**
 *
 * @author arnaud
 */
@ControllerAdvice
@RestController
public class ErrorHandler {

    public ErrorHandler() {

    }
    
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error error400(MissingServletRequestParameterException e, HttpServletResponse r) {       
       return new Error(400, e.getMessage());         
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error error400(MethodArgumentTypeMismatchException e, HttpServletResponse r) {       
       return new Error(400, e.getMessage());         
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error error405(HttpRequestMethodNotSupportedException e, HttpServletResponse r) {       
       return new Error(405, e.getMessage());         
    }

}
