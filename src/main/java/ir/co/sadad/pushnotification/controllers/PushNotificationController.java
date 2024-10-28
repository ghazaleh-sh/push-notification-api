package ir.co.sadad.pushnotification.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
//import ir.bmi.identity.security.interceptor.Scope;
import ir.co.sadad.pushnotification.dtos.*;
import ir.co.sadad.pushnotification.enums.AppUser;
import ir.co.sadad.pushnotification.services.FirebaseCloudMessagingService;
import ir.co.sadad.pushnotification.services.PushUserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static ir.co.sadad.pushnotification.common.Constants.*;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * This class includes entry points of services for add or update user information, active and deactive sending push for them
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(description = "مستندات مدیریت پوش نوتیفیکیشن", name = "Push Notification API")
public class PushNotificationController {

    private final PushUserManagementService pushUserManagementService;
    private final FirebaseCloudMessagingService firebaseCloudMessagingService;

//    @Scope(values = "hambam-push-notification-secure")
    @Operation(summary = "سرویس ثبت مشخصات کاربر بام پی", description = "سرویسی که مشخصات کاربر را در دیتابیس ذخیره میکند")
    @PostMapping(value = "/push-bampay")
    public ResponseEntity<HttpStatus> addOrUpdateUserBampay(
            @RequestHeader(USER_ID) String userId,
            @RequestHeader(SERIAL_ID) String serialId,
            @RequestHeader(CELL_PHONE) String cellPhone,
            @RequestHeader(SSN) String ssn,
            @Valid @RequestBody FirebaseUserReqDto reqDto) {

        reqDto.setMobileNumber(cellPhone);
        reqDto.setNationalCode(ssn);
        reqDto.setSerialId(serialId);
        reqDto.setApplicationName(AppUser.BAAMPAY);
        reqDto.setUserId(userId);
        pushUserManagementService.addOrUpdateUserInfo(reqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @Scope(values = "customer-super")
    @Operation(summary = "سرویس ثبت مشخصات کاربر همراه بام", description = "سرویسی که مشخصات کاربر را در دیتابیس ذخیره میکند")
    @PostMapping(value = "/push-hamrahbam")
    public ResponseEntity<FirebaseUserResDto> addOrUpdateUserHambam(
            @Valid @RequestBody FirebaseUserReqDto reqDto,
            @RequestHeader(SSN) String ssn,
            @RequestHeader(CLIENT_ID) String userId,
            @RequestHeader(SERIAL_ID) String serialId) {
        reqDto.setNationalCode(ssn);
        reqDto.setSerialId(serialId);
        reqDto.setUserId(userId);
        reqDto.setApplicationName(AppUser.HAMRAHBAM);
        FirebaseUserResDto response = pushUserManagementService.addOrUpdateUserInfo(reqDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @Scope(values = "customer-super")
    @Operation(summary = "سرویس فعالسازی/غیرفعالسازی ارسال پوش به کاربر", description = "این سرویس ارسال پوش به دیوایس کاربر را فعال/غیرفعال میکند.")
    @PutMapping(value = "/setting")
    public ResponseEntity<ActivateDeactivateResDto> activePushForUser(
            @Valid @RequestBody ActivateDeactivateReqDto reqDto,
            @RequestHeader(SSN) String ssn) {
        reqDto.setApplicationName(AppUser.HAMRAHBAM);
        ActivateDeactivateResDto response = pushUserManagementService.activeInactivePushForUser(reqDto, ssn);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //    @Scope(values = "customer-super")
    @Operation(summary = "سرویس ارسال پوش به کاربر", description = "این سرویس ")
    @PostMapping(value = "/send")
    public ResponseEntity<HttpStatus> sendMessage(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String noti_id) {
        try {
            this.firebaseCloudMessagingService.pushNotificationWithJsonData(
                    title,
                    description,
                    noti_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "سرویس ارسال بچ پوش به کاربران", description = "این سرویس ")
    @PostMapping(value = "/campaign-send")
    public ResponseEntity<HttpStatus> sendMulticastMessage(
            @RequestBody MultiMessageReqDto reqDto) {
        this.firebaseCloudMessagingService.sendMulticast(reqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @Scope(values = "customer-super")
    @Operation(summary = "سرویس ارسال پوش به یک کاربر توسط سرویس های third-party اعلانات", description = "این سرویس ")
    @PostMapping(value = "/single-send")
    public ResponseEntity<HttpStatus> sendSingleMessage(
            @RequestBody MultiMessageReqDto reqDto) {
        firebaseCloudMessagingService.sendSingle(reqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
