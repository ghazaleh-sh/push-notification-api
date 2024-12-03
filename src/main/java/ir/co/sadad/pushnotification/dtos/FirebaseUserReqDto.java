package ir.co.sadad.pushnotification.dtos;

import ir.co.sadad.pushnotification.enums.UserPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FirebaseUserReqDto {

    @NotBlank(message = "{fcmToken.must.not.be.null}")
    private String fcmToken;

    private String nationalCode;

    @NotNull(message = "{platform.must.not.be.null}")
    private UserPlatform userPlatform;

    @NotBlank(message = "{deviceUniqueId.must.not.be.null}")
    private String deviceUniqueId;

    @NotBlank(message = "{deviceModelId.must.not.be.null}")
    private String deviceModelId;
}
