package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.PushNotificationApplicationTests;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateReqDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserReqDto;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class PushNotificationServiceImplTest extends PushNotificationApplicationTests {

    @Autowired
    private PushUserManagementService service;

    private String userId = "130";
    private String ssn = "0079993141";
    private String otp = "111";
    private String deviceId = "5700cd58df7";

    PushNotificationServiceImplTest() {
    }

    @Test
    void shouldPassAddOrUpdateUserInfo() {
        FirebaseUserReqDto reqDto = FirebaseUserReqDto.builder()
                .fcmToken("8888888888888888888")
                .nationalCode(ssn)
                .userPlatform(UserPlatform.PWA)
                .build();

        assertEquals("user.info.added", service.addOrUpdateUserInfo(reqDto).getMessage());

    }

    @Test
    void shouldActiveUser() {
        ActivateDeactivateReqDto act = ActivateDeactivateReqDto.builder()
                .isActivatedOnTransaction(true).build();

        assertTrue(service.activeInactivePushForUser(act, ssn, otp).isActive());
    }

}