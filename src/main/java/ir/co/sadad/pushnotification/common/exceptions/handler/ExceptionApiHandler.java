package ir.co.sadad.pushnotification.common.exceptions.handler;

import ir.co.sadad.pushnotification.common.exceptions.GlobalErrorResponse;
import ir.co.sadad.pushnotification.common.exceptions.PushNotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(PushNotificationException.class)
    protected ResponseEntity<Object> handleBaseBusinessException(PushNotificationException ex) {
        log.warn("Unhandled Exception: ", ex);

        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(ex.getMessage(), null, new Locale("fa"));
        } catch (NoSuchMessageException e) {
            localizedMessage = ex.getMessage();
        }

        GlobalErrorResponse globalErrorResponse = new GlobalErrorResponse();
        globalErrorResponse
                .setStatus(ex.getHttpStatus())
                .setTimestamp(new Date().getTime())
                .setCode("E" + (ex.getCode() == null ? ex.getHttpStatus().value() : ex.getCode()))
                .setLocalizedMessage(localizedMessage);

        return new ResponseEntity<>(globalErrorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GlobalErrorResponse> handleConstraintValidation(DataIntegrityViolationException ex) {

        log.warn("constraint validation exception", ex);

        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(Objects.requireNonNull(ex.getMessage()), null, new Locale("fa"));
        } catch (NoSuchMessageException e) {
            localizedMessage = ex.getMessage();
        }

        GlobalErrorResponse globalErrorResponse = new GlobalErrorResponse();
        globalErrorResponse
                .setStatus(HttpStatus.BAD_REQUEST)
                .setTimestamp(new Date().getTime())
                .setCode("E" + HttpStatus.BAD_REQUEST.value() + "PSHNF")
                .setLocalizedMessage(localizedMessage);

        return new ResponseEntity<>(globalErrorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.warn("validation exception", ex);
        String generalMsg = messageSource.getMessage("method.argument.not.valid", null, new Locale("fa"));

        List<GlobalErrorResponse.SubError> subErrorList = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            GlobalErrorResponse.SubError subError = new GlobalErrorResponse.SubError();
            subError.setCode("E" + HttpStatus.BAD_REQUEST.value() + "PSHNF");
            subError.setTimestamp(new Date().getTime());
            try {
                subError.setLocalizedMessage(messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), null, new Locale("fa")));
            } catch (NoSuchMessageException exp) {
                subError.setLocalizedMessage(error.getDefaultMessage());
            }
            subErrorList.add(subError);
        });

        GlobalErrorResponse generalErrorResponse = new GlobalErrorResponse();
        generalErrorResponse
                .setStatus(HttpStatus.BAD_REQUEST)
                .setTimestamp(new Date().getTime())
                .setCode("E" + HttpStatus.BAD_REQUEST.value() + "PSHNF")
                .setMessage(generalMsg)
                .setLocalizedMessage(generalMsg)
                .setSubErrors(subErrorList);
        return new ResponseEntity<>(generalErrorResponse, HttpStatus.BAD_REQUEST);

    }

    /**
     * when for example pattern error generate
     * <p>
     * If UserPlatform enum has error, this method handles a suitable message.
     *
     * @param ex
     * @return ResponseEntity<GlobalErrorResponse>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("message not readable exception", ex);

        String localizedMessage;
        try {
            localizedMessage = messageSource.getMessage(ex.getCause().getCause().getMessage(), null, new Locale("fa"));
        } catch (Exception e) {
            localizedMessage = messageSource.getMessage("http.message.not.readable.exception", null, new Locale("fa"));
        }
        GlobalErrorResponse generalErrorResponse = new GlobalErrorResponse();
        generalErrorResponse
                .setStatus(HttpStatus.BAD_REQUEST)
                .setTimestamp(new Date().getTime())
                .setCode("E" + HttpStatus.BAD_REQUEST.value() + "PSHNF")
                .setLocalizedMessage(localizedMessage);


        return new ResponseEntity<>(generalErrorResponse, HttpStatus.BAD_REQUEST);

    }

}
