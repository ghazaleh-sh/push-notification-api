package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.common.exceptions.PushNotificationException;
import ir.co.sadad.pushnotification.common.validators.FirebaseRequest;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateReqDto;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateResDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserReqDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserResDto;
import ir.co.sadad.pushnotification.entities.FirebaseUser;
import ir.co.sadad.pushnotification.enums.UserStatus;
import ir.co.sadad.pushnotification.mappers.FirebaseUserMapper;
import ir.co.sadad.pushnotification.repositories.FirebaseUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class PushNotificationServiceImpl implements PushNotificationService {

    private final FirebaseUserRepository firebaseUserRepository;
    private final FirebaseUserMapper firebaseUserMapper;
    private final MessageSource messageSource;

    @Override
    public FirebaseUserResDto addOrUpdateUserInfo(@FirebaseRequest FirebaseUserReqDto firebaseUserReqDto) {

        FirebaseUserResDto response = new FirebaseUserResDto();

        Optional<FirebaseUser> firebaseUser = firebaseUserRepository.findByNationalCodeAndUserPlatformAndApplicationName(
                firebaseUserReqDto.getNationalCode(), firebaseUserReqDto.getUserPlatform(), firebaseUserReqDto.getApplicationName());
        if (firebaseUser.isPresent()) {
            FirebaseUser savedUser = firebaseUser.get();
            savedUser.setFcmToken(firebaseUserReqDto.getFcmToken());
            savedUser.setUserStatus(firebaseUserReqDto.getUserStatus());
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
        FirebaseUser newFBUser = firebaseUserMapper.toEntity(reqDto);
        newFBUser.setIsTrusted(false);
        firebaseUserRepository.saveAndFlush(newFBUser);
    }

    public ActivateDeactivateResDto activeDeactivePushForUser(ActivateDeactivateReqDto reqDto, String ssn) {

        ActivateDeactivateResDto response = new ActivateDeactivateResDto();

        if (reqDto.getIsTrusted()) {
            firebaseUserRepository.findByNationalCode(ssn).forEach(
                    firebaseUser -> {
                        if (firebaseUser.getIsTrusted()) {
                            firebaseUser.setIsTrusted(false);
                            firebaseUserRepository.saveAndFlush(firebaseUser);
                        }
                    }
            );
        }
        firebaseUserRepository.findByNationalCodeAndUserPlatformAndApplicationName(ssn, reqDto.getPlatform(), reqDto.getApplicationName())
                .ifPresentOrElse(firebaseUser -> {
                            firebaseUser.setIsTrusted(reqDto.getIsTrusted());
                            if (reqDto.getIsTrusted())
                                firebaseUser.setUserStatus(UserStatus.ACTIVE);
                            else
                                firebaseUser.setUserStatus(UserStatus.NONACTIVE);
                            firebaseUserRepository.saveAndFlush(firebaseUser);

                            response.setActive(firebaseUser.getUserStatus().equals(UserStatus.ACTIVE));
                        }, () -> {
                            throw new PushNotificationException("user.with.platform.appName.not.found", HttpStatus.NOT_FOUND);
                        }
                );

        return response;

    }
}
