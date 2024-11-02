package ir.co.sadad.pushnotification.services.sso;

import lombok.Data;

@Data
public class SsoErrorDto {

    private String error;
    private String error_description;
}
