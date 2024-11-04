package ir.co.sadad.pushnotification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import ir.co.sadad.pushnotification.common.exceptions.PushNotificationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
//For representing Enums as a JSON Object
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserPlatform {
    ANDROID("ANDROID"),
    IOS("IOS"),
    PWA("PWA"),
    ALL("ALL"),
    ANDROID_TEST("ANDROID_TEST");

    private static final Map<String, UserPlatform> FORMAT_MAP = Stream
            .of(UserPlatform.values())
            .collect(Collectors.toMap(s -> s.formatted, Function.identity()));

    private final String formatted;

    @JsonCreator // This is the factory method and must be static
    public static UserPlatform fromString(String string) {
        return Optional
                .ofNullable(FORMAT_MAP.get(string))
                .orElseThrow(() -> new PushNotificationException("platform.not.valid", HttpStatus.BAD_REQUEST));
    }
}
