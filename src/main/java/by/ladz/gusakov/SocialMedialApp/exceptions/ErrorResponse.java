package by.ladz.gusakov.SocialMedialApp.exceptions;

import java.util.Date;

public class ErrorResponse {

    private String error;

    private Date timestamp;

    public ErrorResponse(String error, Date timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
