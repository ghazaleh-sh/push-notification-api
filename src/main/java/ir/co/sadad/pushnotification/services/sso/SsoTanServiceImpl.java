package ir.co.sadad.pushnotification.services.sso;

import ir.co.sadad.pushnotification.common.exceptions.PushNotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SsoTanServiceImpl implements SsoTanService {

    private final WebClient webClient;

    @Value(value = "${sso.send-otp}") //PORT:IP/identity-manager/users/tan
    private String sendOtpPath;

    @Value(value = "${sso.verify-otp}") //PORT:IP/identity-manager/users/tan
    private String verifyOtpPath;

    @Override
    public void sendTanRequest(String userPasswordToken) {
        String otpMessage = "کد تایید";

        webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(sendOtpPath)
                        .queryParam("msg", otpMessage)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userPasswordToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("4xx Error: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(SsoErrorDto.class)
                            .flatMap(errorBody -> {
                                log.error("Error 4XX response body: {}", errorBody.getError());
                                return Mono.error(new PushNotificationException(errorBody.getError(), HttpStatus.BAD_REQUEST));
                            });
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.error("5xx Error: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(SsoErrorDto.class)
                            .flatMap(errorBody -> {
                                log.error("Error 5XX response body: {}", errorBody.getError());
                                return Mono.error(new PushNotificationException(errorBody.getError(), HttpStatus.BAD_REQUEST));
                            });
                })
                .bodyToMono(Void.class)
                .subscribe();
    }

    @Override
    public void tanVerification(String userPasswordToken, String otpCode) {
        webClient
                .post()
                .uri(verifyOtpPath)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userPasswordToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .bodyValue(otpCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("4xx Error: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(SsoErrorDto.class)
                            .flatMap(errorBody -> {
                                log.error("Error 4XX response body: {}", errorBody.getError());
                                return Mono.error(new PushNotificationException(errorBody.getError(), HttpStatus.BAD_REQUEST));
                            });
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.error("5xx Error: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(SsoErrorDto.class)
                            .flatMap(errorBody -> {
                                log.error("Error 5XX response body: {}", errorBody.getError());
                                return Mono.error(new PushNotificationException(errorBody.getError(), HttpStatus.BAD_REQUEST));
                            });
                })
                .bodyToMono(Void.class)
                .subscribe();
    }
}
