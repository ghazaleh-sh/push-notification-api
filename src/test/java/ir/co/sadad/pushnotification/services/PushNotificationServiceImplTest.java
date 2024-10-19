package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.PushNotificationApplicationTests;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateReqDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserReqDto;
import ir.co.sadad.pushnotification.enums.AppUser;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import ir.co.sadad.pushnotification.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class PushNotificationServiceImplTest extends PushNotificationApplicationTests {

    @Autowired
    private PushNotificationService service;

    private String userId = "130";
    private String ssn = "0079993141";
    private String cellphone = "989124150188";
    private String deviceId = "5700cd58df7";

    PushNotificationServiceImplTest() {
    }

    @Test
    void shouldPassAddOrUpdateUserInfo() {
        FirebaseUserReqDto reqDto = FirebaseUserReqDto.builder()
                .fcmToken("8888888888888888888")
                .nationalCode(ssn)
                .userId(userId)
                .applicationName(AppUser.BAAMPAY)
                .userStatus(UserStatus.ACTIVE)
                .userPlatform(UserPlatform.WEB)
                .mobileNumber(cellphone).build();

        assertEquals("user.info.added", service.addOrUpdateUserInfo(reqDto).getMessage());

    }

    @Test
    void shouldActiveUser(){
        ActivateDeactivateReqDto act = ActivateDeactivateReqDto.builder()
                .applicationName(AppUser.HAMRAHBAM)
                .isTrusted(true)
                .platform(UserPlatform.IOS).build();

        assertTrue(service.activeDeactivePushForUser(act, ssn).isActive());
    }

}