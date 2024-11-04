package ir.co.sadad.pushnotification.dtos;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Data
@Builder
public class ActivateDeactivateReqDto {

    @NotNull(message = "{userId.must.not.be.null}")
    private UUID userUuid;

    private Boolean isActivatedOnTransaction;
}
