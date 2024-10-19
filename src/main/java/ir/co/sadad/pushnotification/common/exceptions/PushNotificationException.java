package ir.co.sadad.pushnotification.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * All push notification Exceptions use this class
 */
@Getter
public class PushNotificationException extends RuntimeException {
    private final HttpStatus httpStatus;
    private Integer code;

    public PushNotificationException(String message, Integer code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public PushNotificationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
