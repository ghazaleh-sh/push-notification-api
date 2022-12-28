package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.common.validators.FirebaseRequest;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateReqDto;
import ir.co.sadad.pushnotification.dtos.ActivateDeactivateResDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserReqDto;
import ir.co.sadad.pushnotification.dtos.FirebaseUserResDto;

/**
 * this service saves validated users information based on their platform
 * Also activates and deactivates sending notifications to a user
 *
 * @author g.shahrokhabadi
 * 2022/04/24
 */
public interface PushNotificationService {

    /**
     * gets user information with firebase token and maps them into the table
     * If user has been saved before, its data will be update.Otherwise, add user info
     *
     * @param firebaseUserReqDto -@FirebaseRequest checks ssn and mobile number validity
     * @return FirebaseUserResDto
     */
    FirebaseUserResDto addOrUpdateUserInfo(@FirebaseRequest FirebaseUserReqDto firebaseUserReqDto);

    /**
     * If isTrusted is true in the request based on a platform, this service sets isTrusted of other platforms of the user to false
     * and sets requested platform to true.
     * Otherwise, sets current platform to false
     *
     * @param reqDto activation or deactivation by isTrusted field, platform and applicationName which sets by default
     * @param ssn    as national code
     * @return boolean active
     */
    ActivateDeactivateResDto activeDeactivePushForUser(ActivateDeactivateReqDto reqDto, String ssn);
}
