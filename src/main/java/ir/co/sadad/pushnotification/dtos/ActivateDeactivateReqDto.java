package ir.co.sadad.pushnotification.dtos;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class ActivateDeactivateReqDto {

    @NotNull(message = "{userId.must.not.be.null}")
    private Long userId;

    private Boolean isActivatedOnTransaction;
}
