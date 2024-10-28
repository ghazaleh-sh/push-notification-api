package ir.co.sadad.pushnotification.common.validators;

import ir.co.sadad.pushnotification.common.exceptions.PushNotificationException;
import ir.co.sadad.pushnotification.dtos.FirebaseUserReqDto;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 *
 * <pre>
 *     This class checks validity of nationalCode and mobile number of users
 *  </pre>
 */
public class FirebaseRequestValidator implements ConstraintValidator<FirebaseRequest, FirebaseUserReqDto> {

    @Override
//    @SneakyThrows
    public boolean isValid(FirebaseUserReqDto reqDto, ConstraintValidatorContext constraintValidatorContext) {
        if (reqDto.getNationalCode() == null) {
            throw new PushNotificationException("national.code.must.not.be.null", HttpStatus.BAD_REQUEST);
        }

        if (checkNationalCode(reqDto.getNationalCode())) {
            if (reqDto.getMobileNumber() != null && !checkMobileNumber(reqDto.getMobileNumber()))
                throw new PushNotificationException("invalid.mobile.no", HttpStatus.BAD_REQUEST);
            else if (reqDto.getMobileNumber() == null)
                throw new PushNotificationException("mobile.num.must.not.be.null", HttpStatus.BAD_REQUEST);
        } else
            throw new PushNotificationException("invalid.national.code", HttpStatus.BAD_REQUEST);

        return true;
    }

    private boolean checkMobileNumber(String mobileNumber) {
        if (mobileNumber.length() != 12 || !mobileNumber.matches("^\\d{12}$")) {
            return false;
        }

        return mobileNumber.startsWith("98");
    }

    private boolean checkNationalCode(String nationalCode) {
        if (nationalCode.length() != 10 || !nationalCode.matches("^\\d{10}$")) {
            return false;
        }

        int sum = 0;
        int lenght = 10;
        for (int i = 0; i < lenght - 1; i++) {
            sum += Integer.parseInt(String.valueOf(nationalCode.charAt(i))) * (lenght - i);
        }

        int r = Integer.parseInt(String.valueOf(nationalCode.charAt(9)));

        int c = sum % 11;

        return (((c < 2) && (r == c)) || ((c >= 2) && ((11 - c) == r)));
    }
}