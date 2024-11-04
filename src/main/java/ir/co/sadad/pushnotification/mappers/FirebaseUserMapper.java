package ir.co.sadad.pushnotification.mappers;

import ir.co.sadad.pushnotification.dtos.FirebaseUserReqDto;
import ir.co.sadad.pushnotification.dtos.UserFcmInfoResDto;
import ir.co.sadad.pushnotification.entities.FirebaseUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * mapper for entity of firebase user - map dto to entity
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface FirebaseUserMapper {
    FirebaseUser toEntity(FirebaseUserReqDto firebaseUserReqDto);

    UserFcmInfoResDto toUserFcmInfoRes(FirebaseUser firebaseUser);
}
