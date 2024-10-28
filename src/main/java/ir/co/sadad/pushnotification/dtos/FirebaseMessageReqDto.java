package ir.co.sadad.pushnotification.dtos;

import lombok.Data;

@Data
public class FirebaseMessageReqDto {
    private String title;
    private String description;
    private String platform;
    private String activationDate;
    private String hyperlink;
}
