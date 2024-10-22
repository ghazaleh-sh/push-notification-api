package ir.co.sadad.pushnotification.dtos;

import lombok.Data;

import java.util.List;

@Data
public class MultiMessageReqDto {
    private List<String> successSsn;
    private String title;
    private String description;
    private String platform;
    private String activationDate;
    private String hyperlink;
}
