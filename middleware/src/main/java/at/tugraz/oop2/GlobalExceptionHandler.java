package at.tugraz.oop2;

import at.tugraz.oop2.errors.InternalServerErrorException;
import at.tugraz.oop2.errors.JsonErrorMessage;
import at.tugraz.oop2.errors.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

//   400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleIllegalArgumentException(IllegalArgumentException ex) {
        return new JsonErrorMessage(ex.getMessage());
    }

//   404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Object handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new JsonErrorMessage(ex.getMessage());
    }

//   500 Internal Server Error
    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleInternalServerErrorException(InternalServerErrorException ex) {
        return new JsonErrorMessage(ex.getMessage());
    }

//   500 Internal Server Error

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
//        String error = "Internal server error";
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//
//        String error = "Invalid or missing parameters";
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
}
