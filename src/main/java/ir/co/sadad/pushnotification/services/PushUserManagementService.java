package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.common.validators.FirebaseRequest;
import ir.co.sadad.pushnotification.dtos.*;

import java.util.List;

/**
 * this service saves validated users information based on their platform
 * Also activates and deactivates sending notifications to a user
 *
 * @author g.shahrokhabadi
 * 2022/04/24
 */
public interface PushUserManagementService {

    /**
     * gets user information with firebase token and maps them into the table
     * If user has been saved before, its data will be updated.Otherwise, add user info
     *
     * @param firebaseUserReqDto -@FirebaseRequest checks ssn validity
     * @return FirebaseUserResDto
     */
    FirebaseUserResDto addOrUpdateUserInfo(@FirebaseRequest FirebaseUserReqDto firebaseUserReqDto);

    /**
     * If isActivatedOnTransaction is true in the request based on a platform, this service sets isTrusted of other platforms of the user to false
     * and sets requested platform to true.
     * Otherwise, sets current platform to false
     *
     * @param reqDto activation or deactivation by isTrusted field, platform and applicationName which sets by default
     * @param ssn    as national code
     * @return boolean active
     */
    ActivateDeactivateResDto activeInactivePushForUser(ActivateDeactivateReqDto reqDto, String ssn, String otp);

    /**
     * this service delivers a list of user info (except FcmToken) based on its nationalCode
     * A user might have more than one device to get push notification
     *
     * @param ssn
     * @return
     */
    List<UserFcmInfoResDto> userFcmInfo(String ssn);
}
