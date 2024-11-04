package ir.co.sadad.pushnotification.entities;

import ir.co.sadad.pushnotification.enums.UserPlatform;
import ir.co.sadad.pushnotification.services.utils.UUIDToBytesConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

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

    @Column(name = "USER_UUID", columnDefinition = "CHAR(16) FOR BIT DATA") //reduces the overall database size.
    @Convert(converter = UUIDToBytesConverter.class)
    private UUID userUuid;

    @Column(name = "FCM_TOKEN", length = 200, nullable = false)
    private String fcmToken;

    @Column(name = "NATIONAL_CODE", columnDefinition = "char(10)", length = 10, nullable = false)
    private String nationalCode;

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

    public FirebaseUser() {
        this.userUuid = UUID.randomUUID(); // Automatically generates a UUID
    }
}
