package ir.co.sadad.pushnotification.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ir.co.sadad.pushnotification.dtos.*;
import ir.co.sadad.pushnotification.services.FirebaseCloudMessagingService;
import ir.co.sadad.pushnotification.services.PushUserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * This class includes entry points of services for add or update user information, active and deactivate sending push for them
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(description = "مستندات مدیریت کاربران پوش نوتیفیکیشن", name = "Push Notification API")
public class PushNotificationController {

    private final PushUserManagementService pushUserManagementService;
    private final FirebaseCloudMessagingService firebaseCloudMessagingService;


    public ResponseEntity<FirebaseUserResDto> addOrUpdateUser(
            @Valid @RequestBody FirebaseUserReqDto reqDto) {
        FirebaseUserResDto response = pushUserManagementService.addOrUpdateUserInfo(reqDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ActivateDeactivateResDto> activePushForUser(
            @Valid @RequestBody ActivateDeactivateReqDto reqDto,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @RequestHeader("otp") String otp) {
        ActivateDeactivateResDto response = pushUserManagementService.activeInactivePushForUser(reqDto, authToken, otp);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<List<UserFcmInfoResDto>> userFcmInfo(
            @PathVariable("nationalCode") String nationalCode) {
        List<UserFcmInfoResDto> response = pushUserManagementService.userFcmInfo(nationalCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> sendMulticastMessage(
            @RequestBody MultiMessageReqDto reqDto) {
        firebaseCloudMessagingService.sendMulticast(reqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> sendSingleMessage(
            @RequestBody SingleMessageReqDto reqDto) {
        firebaseCloudMessagingService.sendSingle(reqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
