package ru.skillbox.socialnetwork.exceptions;


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
        return ResponseEntity.status(200)
                .body(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()));
    }
    @ExceptionHandler(DialogNotFoundException.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handleDialogNotFoundException(DialogNotFoundException ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.OK);
    }
    @ExceptionHandler(MessageNotFoundException.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handleMessageNotFoundException(MessageNotFoundException ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.OK);
    }
    @ExceptionHandler(MessageEmptyException.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handleMessageEmptyException(MessageEmptyException ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.OK);
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.OK);
    }

    @ExceptionHandler(CustomExceptionBadRequest.class)
    protected ResponseEntity<ErrorErrorDescriptionResponse> handleCustomException(CustomExceptionBadRequest ex) {
        return new ResponseEntity<>(new ErrorErrorDescriptionResponse("invalid_request", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<?> handleUnauthorizedException() {
        return ResponseEntity.status(401).body(null);
    }
}

