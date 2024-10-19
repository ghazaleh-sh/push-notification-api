package ir.co.sadad.pushnotification.repositories;

import ir.co.sadad.pushnotification.entities.FirebaseUser;
import ir.co.sadad.pushnotification.enums.AppUser;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FirebaseUserRepository extends JpaRepository<FirebaseUser, Long> {
    List<FirebaseUser> findByNationalCode(String nationalCode);
    Optional<FirebaseUser> findByNationalCodeAndUserPlatformAndApplicationName(String nationalCode, UserPlatform platform, AppUser applicationName);

    Optional<FirebaseUser> findTopByNationalCodeAndIsTrustedIsTrue(String nationalCode);
}
