package ir.co.sadad.pushnotification.entities;

import ir.co.sadad.pushnotification.enums.AppUser;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import ir.co.sadad.pushnotification.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * stores firebase token of each user from different applications and platforms
 */
@Entity
@Table(name = "FIREBASE_USER", uniqueConstraints = {@UniqueConstraint(columnNames = {"NATIONAL_CODE", "APPLICATION_NAME", "USER_PLATFORM"}, name = "UKFIREBASE_SSN_PLATFORM_APPNAME")})
@Getter
@Setter
public class FirebaseUser extends AbstractEntity {

    @Column(name = "USER_ID", length = 40)
    private String userId;

    @Column(name = "FCM_TOKEN", length = 200, nullable = false)
    private String fcmToken;

    @Column(name = "NATIONAL_CODE", columnDefinition = "char(10)", length = 10, nullable = false)
    private String nationalCode;

    @Column(name = "MOBILE_NUMBER", columnDefinition = "char(12)", length = 12)
    private String mobileNumber;

    @Column(name = "APPLICATION_NAME", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private AppUser applicationName;

    @Column(name = "USER_STATUS", nullable = false, columnDefinition = "varchar(50)", length = 50)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(name = "SERIAL_ID", length = 50)
    private String serialId;

    @Column(name = "USER_PLATFORM", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserPlatform userPlatform;

    @Column(name = "IS_TRUSTED", columnDefinition = "SMALLINT DEFAULT 0")
//    @org.hibernate.annotations.ColumnDefault("false")
    private Boolean isTrusted;

}
