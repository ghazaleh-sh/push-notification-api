package ir.co.sadad.pushnotification.dtos;

import ir.co.sadad.pushnotification.enums.UserPlatform;
import lombok.Data;

@Data
public class UserFcmInfoResDto {

    private Long id;

    private String nationalCode;

    private UserPlatform userPlatform;

    private Boolean isActivatedOnTransaction;

    private String deviceUniqueId;

    private String deviceModelId;
}
