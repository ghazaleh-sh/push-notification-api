package ir.co.sadad.pushnotification.dtos;

import ir.co.sadad.pushnotification.enums.AppUser;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import ir.co.sadad.pushnotification.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class FirebaseUserReqDto {

    private String userId;

    @NotNull(message = "{fcmToken.must.not.be.null}")
    private String fcmToken;

    private String nationalCode;

    private String mobileNumber;

    private AppUser applicationName;

    private UserStatus userStatus;

    private String serialId;

//    @Pattern(regexp = "^(ANDROID|IOS|WEB)$", message = "{platform.not.valid}")
    @NotNull(message = "{platform.must.not.be.null}")
    private UserPlatform userPlatform;

    private Boolean isTrusted;
}
