package ir.co.sadad.pushnotification.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.*;
import com.google.gson.JsonObject;
import ir.co.sadad.hambaam.persiandatetime.PersianUTC;
import ir.co.sadad.pushnotification.dtos.MultiMessageReqDto;
import ir.co.sadad.pushnotification.dtos.SingleMessageReqDto;
import ir.co.sadad.pushnotification.entities.FirebaseUser;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import ir.co.sadad.pushnotification.repositories.FirebaseUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ir.co.sadad.pushnotification.common.Constants.HTTPV1_ENDPOINT;
import static ir.co.sadad.pushnotification.common.Constants.MESSAGE_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseCloudMessagingServiceImpl implements FirebaseCloudMessagingService {

    @Value(value = "${fcm.service.account}")
    private String fcm_account_fileName;

    @Value(value = "${fcm.service.project-id}")
    private String project_id;

    @Value(value = "${fcm.service.scope}")
    private static String MESSAGING_SCOPE;

    private static final String[] SCOPES = {MESSAGING_SCOPE};

    @Value(value = "${fcm.endpoint.httpV1-send-url}")
    private String path;

    private final FirebaseUserRepository firebaseUserRepository;
    private final WebClient webClient;

    private final String currentDate = PersianUTC.currentUTC().getDate().concat("T20:30:00.000Z");

    @SneakyThrows
    public void sendSingle(SingleMessageReqDto msgReq) {
        if (msgReq.getSsn() == null || msgReq.getSsn().isEmpty())
            return;

        List<String> userFCMToken;// = "cjXADdcGQs-G7N6ks5lXS-:APA91bFJPY_8iI-06ARajU1WaNDtZNXi4AphlojKx4fKsu9YArq1FFZ_jvkyC21rfwF-28VrUUNCOhXgQsLoCn8EV1WfZb4vzxgg7uBIjbqArY5-ZX39E8nzfmF9Ugy1HXhpDzczbXEx";
        List<FirebaseUser> userInfoToPush;

        if (UserPlatform.ALL.toString().equals(msgReq.getPlatform()))
            userInfoToPush = firebaseUserRepository.findByNationalCode(msgReq.getSsn());
        else
            userInfoToPush = firebaseUserRepository.findByNationalCodeAndUserPlatform(
                            msgReq.getSsn(), UserPlatform.valueOf(msgReq.getPlatform()))
                    .stream().toList();

        if (!userInfoToPush.isEmpty()) {
            userFCMToken = userInfoToPush.stream().map(FirebaseUser::getFcmToken).toList();

//        if (msgReq.getActivationDate().compareTo(currentDate) > 0) {
//            //TODO: send this notification toward the job
//        }

            callHttpV1Api(msgReq, userFCMToken);
        }

    }

    protected void callHttpV1Api(SingleMessageReqDto msgReq, List<String> userFCMToken) {
        userFCMToken.forEach(token ->
                webClient
                        .post()
                        .uri(path + project_id + HTTPV1_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(buildMyNotificationMessage(token, msgReq.getTitle(), msgReq.getDescription() + " " + msgReq.getHyperlink()))
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                            log.error("4xx Error: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error 4XX response body: {}", errorBody);
                                        return Mono.error(new RuntimeException("4xx Error: " + errorBody));
                                    });
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                            log.error("5xx Error: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Error 5XX response body: {}", errorBody);
                                        return Mono.error(new RuntimeException("5xx Error: " + errorBody));
                                    });
                        })
                        .bodyToMono(String.class)
                        .block());

    }

    public void sendMulticast(MultiMessageReqDto msgReq) {
        try {
//            if (msgReq.getActivationDate().compareTo(currentDate) > 0) {
//                //TODO: send this notification toward the job
//            }

            List<String> registrationTokens = new java.util.ArrayList<>();//(List.of("cjXADdcGQs-G7N6ks5lXS-:APA91bFJPY_8iI-06ARajU1WaNDtZNXi4AphlojKx4fKsu9YArq1FFZ_jvkyC21rfwF-28VrUUNCOhXgQsLoCn8EV1WfZb4vzxgg7uBIjbqArY5-ZX39E8nzfmF9Ugy1HXhpDzczbXEx"
//                    , "eyefizXbRvqvBh2oKWKMHP:APA91bF714BuVvky0nI1U3PUzVmGR07MOhW1LXR4erdhfqC6v04MzOlrnN9jkqeXKPhWgUyrg4vL3lxCcAAKefpi9Fo4PJ5hnCrz3VpBdp0ZjS0MxisjYeU"));

            if (msgReq.getSuccessSsn().isEmpty()) { //general notice
                firebaseUserRepository.findByUserPlatform(UserPlatform.valueOf(msgReq.getPlatform()))
                        .forEach(firebaseUser -> registrationTokens.add(firebaseUser.getFcmToken()));

            } else {
                msgReq.getSuccessSsn().forEach(givenSsn ->
                        firebaseUserRepository.findByNationalCodeAndUserPlatform(givenSsn, UserPlatform.valueOf(msgReq.getPlatform()))
                                .ifPresent(firebaseUser -> registrationTokens.add(firebaseUser.getFcmToken()))
                );
            }


            if (!registrationTokens.isEmpty()) {
                Notification campaingNotification = Notification.builder()
                        .setTitle(msgReq.getTitle())
                        .setBody(msgReq.getDescription() + " " + msgReq.getHyperlink())
                        .build();

                callMulticastSDKService(campaingNotification, msgReq.getHyperlink(), registrationTokens);
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Async //to parallelize the sending process.
    protected void callMulticastSDKService(Notification campaingNotification, String hyperlink, List<String> registrationTokens) {
        try {
            List<List<String>> batches = Lists.partition(registrationTokens, 500); // splits the token list into batches of 500

            for (List<String> batch : batches) {

                MulticastMessage message = MulticastMessage.builder()
                        .setNotification(campaingNotification)
                        .putData("hyperlink", hyperlink)
                        .addAllTokens(batch)
                        .build();
                BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
                Thread.sleep(500);// pause between batches
                System.out.println(response.getSuccessCount() + " messages were sent successfully");
                System.out.println(response.getFailureCount() + " messages were not sent");
            }
        } catch (FirebaseMessagingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private String getAccessToken() {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(fcm_account_fileName))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refreshIfExpired();
//        return googleCredentials.refreshAccessToken().getTokenValue();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    /**
     * {
     * "message": {
     * "token": "FCM_TOKEN",
     * "notification": {
     * "body": "Body of Your Notification in data",
     * "title": "Title of Your Notification in data"
     * }
     * }
     * }
     */
    private JsonObject buildMyNotificationMessage(String deviceToken, String title, String body) {
        JsonObject jNotification = new JsonObject();
        jNotification.addProperty("title", title);
        jNotification.addProperty("body", body);

        JsonObject jMessage = new JsonObject();
        jMessage.add("notification", jNotification);
        jMessage.addProperty("token", deviceToken);

        JsonObject jFcm = new JsonObject();
        jFcm.add(MESSAGE_KEY, jMessage);

        return jFcm;
    }


//    /**
//     * Read contents of InputStream into String.
//     *
//     * @param inputStream InputStream to read.
//     * @return String containing contents of InputStream.
//     * @throws IOException
//     */
//    protected static String inputStreamToString(InputStream inputStream) throws IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        Scanner scanner = new Scanner(inputStream);
//        while (scanner.hasNext()) {
//            stringBuilder.append(scanner.nextLine());
//        }
//        return stringBuilder.toString();
//    }


    //    /**
//     * Send request to FCM message using HTTP.
//     * Encoded with UTF-8 and support special characters.
//     *
//     * @param title
//     * @param description
//     * @param noti_id
//     * @throws IOException
//     * @throws IOException
//     */
    @SneakyThrows
    public void pushNotificationWithJsonData(String title, String description, String noti_id) {

//        String userDeviceIdKey = "cjXADdcGQs-G7N6ks5lXS-:APA91bFJPY_8iI-06ARajU1WaNDtZNXi4AphlojKx4fKsu9YArq1FFZ_jvkyC21rfwF-28VrUUNCOhXgQsLoCn8EV1WfZb4vzxgg7uBIjbqArY5-ZX39E8nzfmF9Ugy1HXhpDzczbXEx";
//        Optional<FirebaseUser> userToPush = firebaseUserRepository.findTopByNationalCodeAndIsTrustedIsTrue(String.valueOf(noti_id));
//        if (userToPush.isPresent()) {
//            userDeviceIdKey = userToPush.get().getFcmToken();
//        }
//        else
//            throw new PushNotificationException("user.with.platform.appName.not.trusted", HttpStatus.NOT_FOUND);

//        HttpURLConnection httpURLConnection = getConnection(path);
//        OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);

//        JSONObject json_data = new JSONObject();
//        JSONObject json_payload = new JSONObject();
//        JSONObject payload = new JSONObject();
//        JSONObject obj = new JSONObject();
//
//        json_data.put("TITLE_EN", title);
//        json_data.put("DESCRIPTION_EN", description);
//        json_data.put("NOTI_ID", String.valueOf(noti_id));
//
//        payload.put("TITLE_EN", title);
////        payload.put("TITLE_KH", title_kh);
//        payload.put("DESCRIPTION_EN", description);
//        payload.put("NOTI_ID", String.valueOf(noti_id));
//
//        obj.put("content-available", 1);
//        payload.put("aps", obj);
//
//        json_payload.put("payload", payload);

//        JSONObject req = dataMessagesHttpV1(json_data, json_payload, userDeviceIdKey);
//        JsonObject req = buildMyNotificationMessage(userDeviceIdKey, title, description);
//        wr.write(req.toString());
//        wr.flush();
//        wr.close();
//
//        log.info("===( Start PushNotiHttpV1Service response log )===");
//        int responseCode = httpURLConnection.getResponseCode();
//
//        if (responseCode == 200) {
//            String response = inputStreamToString(httpURLConnection.getInputStream());
//            log.info("Response Code : " + responseCode);
//            log.info("Response Message : " + httpURLConnection.getResponseMessage());
//            log.info("Sending 'POST' request to URL : " + path);
//            log.info("Post parameters : " + req);
//            log.info("Message sent to Firebase for delivery, response:");
//            log.info(response);
//        } else {
//            log.info("Response Code : " + responseCode);
//            log.info("Response Message : " + httpURLConnection.getResponseMessage());
//            log.info("Unable to send message to Firebase:");
//            String response = inputStreamToString(httpURLConnection.getErrorStream());
//            log.info(response);
//        }
//        log.info("===( End PushNotiHttpV1Service response log )===");
    }

}
