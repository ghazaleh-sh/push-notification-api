package ir.co.sadad.pushnotification.common.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class GlobalErrorResponse {

    private HttpStatus status;
    private Long timestamp;
    private String code;
    private String message;
    private String localizedMessage;
    private List<SubError> subErrors = new ArrayList<>();
    private String extraData;

    @Getter
    @Setter
    public static class SubError {
        private Long timestamp;
        private String code;
        private String message;
        private String localizedMessage;
    }

}
