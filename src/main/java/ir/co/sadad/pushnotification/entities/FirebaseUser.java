package ir.co.sadad.pushnotification.entities;

import ir.co.sadad.pushnotification.enums.UserPlatform;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

/**
 * @author g.shahrokhabadi
 * @version 11, 1401/09/20
 * <p>
 * stores firebase token of each user from different applications and platforms
 */
@Entity
@Table(name = "FIREBASE_USER", uniqueConstraints = {@UniqueConstraint(columnNames = {"NATIONAL_CODE", "USER_PLATFORM", "DEVICE_UNIQUE_ID", "MODEL_ID"}, name = "UKFIREBASE_SSN_PLATFORM_MODEL")})
@Getter
@Setter
public class FirebaseUser extends AbstractEntity {

//    @Column(name = "USER_ID", length = 40)
//    private String userId;

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "FCM_TOKEN", length = 200, nullable = false)
    private String fcmToken;

    @Column(name = "NATIONAL_CODE", columnDefinition = "char(10)", length = 10, nullable = false)
    private String nationalCode;

//    @Column(name = "MOBILE_NUMBER", columnDefinition = "char(12)", length = 12)
//    private String mobileNumber;

//    @Column(name = "APPLICATION_NAME", length = 15, nullable = false)
//    @Enumerated(EnumType.STRING)
//    private AppUser applicationName;

//    @Column(name = "USER_STATUS", nullable = false, columnDefinition = "varchar(50)", length = 50)
//    @Enumerated(EnumType.STRING)
//    private UserStatus userStatus;

//    @Column(name = "SERIAL_ID", length = 50)
//    private String serialId;

    @Column(name = "USER_PLATFORM", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserPlatform userPlatform;

    @Column(name = "IS_ACTIVATED_ON_TRANSACTION", columnDefinition = "SMALLINT")
//    @org.hibernate.annotations.ColumnDefault("false")
    private Boolean isActivatedOnTransaction;

    @Column(name = "DEVICE_UNIQUE_ID", length = 100)//, nullable = false)
    private String deviceUniqueId;

    @Column(name = "DEVICE_MODEL_ID", length = 100)
    private String deviceModelId;
//
//    @Column(name = "CAMPAIGN_PUSH", columnDefinition = "SMALLINT")
//    private Boolean campaignPush;

}
