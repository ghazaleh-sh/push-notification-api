package ir.co.sadad.pushnotification.repositories;

import ir.co.sadad.pushnotification.entities.FirebaseUser;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FirebaseUserRepository extends JpaRepository<FirebaseUser, Long> {
    List<FirebaseUser> findByNationalCode(String nationalCode);

    List<FirebaseUser> findByUserPlatform(UserPlatform platform);

    Optional<FirebaseUser> findByUserUuid(UUID userUuid);

    List<FirebaseUser> findByUserPlatformAndIsActivatedOnTransactionIsTrue(UserPlatform platform);

    List<FirebaseUser> findAllByIsActivatedOnTransactionIsTrue();

    Optional<FirebaseUser> findByNationalCodeAndUserPlatformAndDeviceUniqueIdAndDeviceModelId(String nationalCode, UserPlatform platform, String deviceUniqueId, String deviceModelId);

    List<FirebaseUser> findByNationalCodeAndUserPlatform(String nationalCode, UserPlatform platform);

    List<FirebaseUser> findByNationalCodeAndIsActivatedOnTransactionIsTrue(String nationalCode);

    List<FirebaseUser> findByNationalCodeAndUserPlatformAndIsActivatedOnTransactionIsTrue(String nationalCode, UserPlatform platform);
}
