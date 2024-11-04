package ir.co.sadad.pushnotification.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MultiMessageReqDto extends FirebaseMessageReqDto {
    private List<String> successSsn;
}
