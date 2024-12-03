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
import reactor.core.publisher.Mono;

import java.util.List;

import static ir.co.sadad.pushnotification.common.Constants.SSN;

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

    ///////////////////////////managing fcm users////////////////////////////////
    //    @Scope(values = "customer-super")
    @Operation(summary = "سرویس ثبت مشخصات کاربر همراه بام", description = "سرویسی که مشخصات کاربر را در دیتابیس ذخیره یا به روزرسانی میکند")
    @PostMapping(value = "/user/add")
    public ResponseEntity<FirebaseUserResDto> addOrUpdateUser(
            @RequestHeader(SSN) String ssn,
            @Valid @RequestBody FirebaseUserReqDto reqDto) {
        reqDto.setNationalCode(ssn);
        FirebaseUserResDto response = pushUserManagementService.addOrUpdateUserInfo(reqDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //    @Scope(values = "customer-super")
    @Operation(summary = "سرویس فعالسازی/غیرفعالسازی ارسال پوش تراکنش ها به کاربر", description = "این سرویس ارسال پوش تراکنش ها به دیوایس کاربر را فعال/غیرفعال میکند.")
    @PutMapping(value = "/user/activation")
    public ResponseEntity<ActivateDeactivateResDto> activePushForUser(
            @Valid @RequestBody ActivateDeactivateReqDto reqDto,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
            @RequestHeader("otp") String otp) {
        ActivateDeactivateResDto response = pushUserManagementService.activeInactivePushForUser(reqDto, authToken, otp);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "سرویس اطلاعات کاربر", description = "این سرویس لیستی از اطلاعات کاربر را بر اساس کدملی باز میگرداند.")
    @GetMapping(value = "/user/info")
    public ResponseEntity<List<UserFcmInfoResDto>> userFcmInfo(
            @RequestHeader(SSN) String nationalCode) {
        List<UserFcmInfoResDto> response = pushUserManagementService.userFcmInfo(nationalCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    ///////////////////////////sending push notification services////////////////////////////////
    @Operation(summary = "سرویس ارسال پوش به بچ کاربران", description = "این سرویس به لیستی از کاربران اعلانات کمپینی را بصورت نوتیفیکیشن ارسال میکند. در صورتی که لیست خالی باشد، پوش به تمام کاربران موجود در دیتابیس ارسال میشود")
    @PostMapping(value = "/push/campaign-send")
    public Mono<ResponseEntity<HttpStatus>> sendMulticastMessage(
            @RequestBody MultiMessageReqDto reqDto) {
        return firebaseCloudMessagingService.sendMulticast(reqDto)
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    }

    //    @Scope(values = "customer-super")
    @Operation(summary = "سرویس ارسال پوش به یک کاربر توسط سرویس های third-party اعلانات", description = "این سرویس نوتیفیکیشن را به دستگاه های کاربر مربوطه(بر اساس پلتفرم) ارسال میکند")
    @PostMapping(value = "/push/single-send")
    public ResponseEntity<HttpStatus> sendSingleMessage(
            @RequestBody SingleMessageReqDto reqDto) {
        firebaseCloudMessagingService.sendSingle(reqDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
