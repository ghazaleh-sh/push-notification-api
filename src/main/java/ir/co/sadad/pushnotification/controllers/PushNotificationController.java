package ir.co.sadad.pushnotification.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
//import ir.bmi.identity.security.interceptor.Scope;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateReqDto;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateResDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserReqDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserResDto;
import ir.co.sadad.pushnotification.enums.AppUser;
import ir.co.sadad.pushnotification.services.HttpV1ServiceImpl;
import ir.co.sadad.pushnotification.services.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    private final PushNotificationService pushNotificationService;
    private final HttpV1ServiceImpl httpV1Service;

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
        pushNotificationService.addOrUpdateUserInfo(reqDto);
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
        FirebaseUserResDto response = pushNotificationService.addOrUpdateUserInfo(reqDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @Scope(values = "customer-super")
    @Operation(summary = "سرویس فعالسازی/غیرفعالسازی ارسال پوش به کاربر", description = "این سرویس ارسال پوش به دیوایس کاربر را فعال/غیرفعال میکند.")
    @PutMapping(value = "/setting")
    public ResponseEntity<ActivateDeactivateResDto> activePushForUser(
            @Valid @RequestBody ActivateDeactivateReqDto reqDto,
            @RequestHeader(SSN) String ssn) {
        reqDto.setApplicationName(AppUser.HAMRAHBAM);
        ActivateDeactivateResDto response = pushNotificationService.activeDeactivePushForUser(reqDto, ssn);
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
            this.httpV1Service.pushNotificationWithJsonData(
                    title,
                    description,
                    noti_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
