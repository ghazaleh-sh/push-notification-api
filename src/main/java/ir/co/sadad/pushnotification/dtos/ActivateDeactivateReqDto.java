package ir.co.sadad.pushnotification.dtos;

import ir.co.sadad.pushnotification.enums.AppUser;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ActivateDeactivateReqDto {

    @NotNull(message = "{platform.must.not.be.null}")
    private UserPlatform platform;

//    @NotNull(message = "{applicationName.must.not.be.null}")
    private AppUser applicationName;

    private Boolean isTrusted;
}
