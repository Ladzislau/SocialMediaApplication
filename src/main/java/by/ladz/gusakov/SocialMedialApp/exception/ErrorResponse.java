package by.ladz.gusakov.SocialMedialApp.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ErrorResponse {

    private String error;

    private Date timestamp;

    public ErrorResponse(String error, Date timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }
}
