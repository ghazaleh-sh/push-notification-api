package ir.co.sadad.pushnotification.dtos;

import ir.co.sadad.pushnotification.enums.UserPlatform;
import lombok.Data;

import java.util.UUID;

@Data
public class UserFcmInfoResDto {

    private UUID userUuid;

    private String nationalCode;

    private UserPlatform userPlatform;

    private Boolean isActivatedOnTransaction;

    private String deviceUniqueId;

    private String deviceModelId;
}
