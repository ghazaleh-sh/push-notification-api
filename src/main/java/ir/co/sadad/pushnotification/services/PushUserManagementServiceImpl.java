package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.common.exceptions.PushNotificationException;
import ir.co.sadad.pushnotification.common.validators.FirebaseRequest;
import ir.co.sadad.pushnotification.dtos.*;
import ir.co.sadad.pushnotification.entities.FirebaseUser;
import ir.co.sadad.pushnotification.mappers.FirebaseUserMapper;
import ir.co.sadad.pushnotification.repositories.FirebaseUserRepository;
import ir.co.sadad.pushnotification.services.sso.SsoTanService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class PushUserManagementServiceImpl implements PushUserManagementService {

    private final FirebaseUserRepository firebaseUserRepository;
    private final FirebaseUserMapper mapper;
    private final MessageSource messageSource;
    private final SsoTanService ssoTanService;

    @Override
    @SneakyThrows
    public FirebaseUserResDto addOrUpdateUserInfo(@FirebaseRequest FirebaseUserReqDto firebaseUserReqDto) {

        FirebaseUserResDto response = new FirebaseUserResDto();

        Optional<FirebaseUser> firebaseUser = firebaseUserRepository.findByNationalCodeAndUserPlatformAndDeviceUniqueIdAndDeviceModelId(
                firebaseUserReqDto.getNationalCode(), firebaseUserReqDto.getUserPlatform(),
                firebaseUserReqDto.getDeviceUniqueId(), firebaseUserReqDto.getDeviceModelId());
        if (firebaseUser.isPresent()) {
            FirebaseUser savedUser = firebaseUser.get();
            savedUser.setFcmToken(firebaseUserReqDto.getFcmToken());
            firebaseUserRepository.saveAndFlush(savedUser);
            response.setMessage(messageSource.getMessage("user.info.updated", null, new Locale("fa")));

        } else {
            createFirebaseUser(firebaseUserReqDto);
            response.setMessage(messageSource.getMessage("user.info.added", null, new Locale("fa")));
        }

        response.setNationalCode(firebaseUserReqDto.getNationalCode());
        return response;
    }

    private void createFirebaseUser(FirebaseUserReqDto reqDto) {
        FirebaseUser newFBUser = mapper.toEntity(reqDto);
        newFBUser.setIsActivatedOnTransaction(false);
        firebaseUserRepository.saveAndFlush(newFBUser);
    }

    @Override
    @SneakyThrows
    public ActivateDeactivateResDto activeInactivePushForUser(ActivateDeactivateReqDto reqDto, String authToken, String otp) {

        FirebaseUser savedUser = firebaseUserRepository.findByUserUuid(reqDto.getUserUuid())
                .orElseThrow(() -> new PushNotificationException("user.not.found", HttpStatus.NOT_FOUND));

        if (otp == null || otp.isEmpty()) {
            ssoTanService.sendTanRequest(authToken);
            return null;

        } else {
            ssoTanService.tanVerification(authToken, otp);

            ActivateDeactivateResDto response = new ActivateDeactivateResDto();

            savedUser.setIsActivatedOnTransaction(reqDto.getIsActivatedOnTransaction());
            firebaseUserRepository.saveAndFlush(savedUser);

            response.setActive(reqDto.getIsActivatedOnTransaction());

            return response;
        }
    }

    @Override
    @SneakyThrows
    public List<UserFcmInfoResDto> userFcmInfo(String ssn) {
        List<UserFcmInfoResDto> res = new ArrayList<>();

        firebaseUserRepository.findByNationalCode(ssn)
                .forEach(firebaseUser -> res.add(mapper.toUserFcmInfoRes(firebaseUser)));

        return res;

    }
}
