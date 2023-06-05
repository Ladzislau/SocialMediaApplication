package by.ladz.gusakov.SocialMedialApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomException e) {
        HttpStatus status = determineHttpStatus(e);
        ErrorResponse response = new ErrorResponse(e.getMessage(), new Date());
        return new ResponseEntity<>(response, status);
    }

    private HttpStatus determineHttpStatus(CustomException e) {
        if (e instanceof PersonNotAuthenticatedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (e instanceof CantLoadImageException ||
                e instanceof PersonNotFoundException ||
                e instanceof PublicationNotFoundedException) {
            return HttpStatus.NOT_FOUND;
        } else if (e instanceof PersonNotCreatedException ||
                e instanceof IncorrectPageParametersException ||
                e instanceof MessageException ||
                e instanceof IncorrectPublicationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (e instanceof UnauthorizedPublicationModificationException ||
                e instanceof NotFriendException) {
            return HttpStatus.FORBIDDEN;
        } else if (e instanceof FollowingException) {
            return HttpStatus.CONFLICT;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
