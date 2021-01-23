package ru.skillbox.socialnetwork.services.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.skillbox.socialnetwork.api.responses.ErrorErrorDescriptionResponse;

@ControllerAdvice()
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handlePersonNotFoundException(PersonNotFoundException ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.OK);
    }
    @ExceptionHandler(DialogNotFoundException.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handleDialogNotFoundException(DialogNotFoundException ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.OK);
    }
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.OK);
    }

}
