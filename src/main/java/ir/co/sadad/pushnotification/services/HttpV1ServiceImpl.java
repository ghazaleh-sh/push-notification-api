package ir.co.sadad.pushnotification.services;

// [START storage_batch_request]

//import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.storage.Blob;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageBatch;
//import com.google.cloud.storage.StorageOptions;


import com.google.common.collect.Lists;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.gson.JsonObject;
import ir.co.sadad.pushnotification.common.exceptions.PushNotificationException;
import ir.co.sadad.pushnotification.entities.FirebaseUser;
import ir.co.sadad.pushnotification.repositories.FirebaseUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Firebase Cloud Messaging (FCM) can be used to send messages to clients on iOS, Android and Web.
 * <p>
 * This sample uses FCM to send two types of messages to clients that are subscribed to the `news`
 * topic. One type of message is a simple notification message (display message). The other is
 * a notification message (display notification) with platform specific customizations, for example,
 * a badge is added to messages that are sent to iOS devices.
 */
@Service
@Slf4j
public class HttpV1ServiceImpl extends FcmService {

    //    @Value(value = "${fcm.service.account}")
    private String fcm_svc = "google-services.json";
    //    @Value(value = "${fcm.service.project-id}")
    private String PROJECT_ID = "agpush-test";
    //    @Value(value = "${fcm.scope}")
    private static String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = {MESSAGING_SCOPE};

    //    @Value(value = "${fcm.endpoint.base-url}")
//    private String BASE_URL;
//    @Value(value = "${fcm.endpoint.send-url}")
//    private String FCM_SEND_ENDPOINT;
    private final String path = "https://fcm.googleapis.com/v1/projects/agpush-test/messages:send";//BASE_URL + FCM_SEND_ENDPOINT;

    public static final String MESSAGE_KEY = "message";

    private final FirebaseUserRepository firebaseUserRepository;

    public HttpV1ServiceImpl(FirebaseUserRepository firebaseUserRepository) {
        this.firebaseUserRepository = firebaseUserRepository;
    }

    /**
     * To get Credentials from a Service Account JSON key use GoogleCredentials.fromStream(InputStream) or
     * GoogleCredentials.fromStream(InputStream, HttpTransportFactory).
     * Note that the credentials must be refreshed before the access token is available.
     * <p>
     * Use your Firebase credentials together with the Google Auth Library for your preferred language
     * to Retrieve a valid (a short-lived OAuth 2.0) access token that can be use to authorize requests to the FCM REST API.
     * <p>
     * After your access token expires, the token refresh method is called automatically to retrieve an updated access token.
     *
     * @return Access token.
     * @throws IOException
     */
    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("agpush"))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refreshIfExpired();
//        return googleCredentials.refreshAccessToken().getTokenValue();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    /**
     * Create HttpURLConnection that can be used for both retrieving and publishing.
     *
     * @return Base HttpURLConnection.
     * @throws IOException
     */
    protected static HttpURLConnection getConnection(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);

        httpURLConnection.setRequestMethod("POST");

        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
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
//     * Send data message with Firebase Http V1
//     *
//     * @param json_data
//     * @param json_payload
//     * @param userDeviceIdKey
//     * @return
//     * @throws IOException
//     */
//    private static JSONObject dataMessagesHttpV1(JSONObject json_data, JSONObject json_payload, String userDeviceIdKey) throws IOException, JSONException {
//        JSONObject json = new JSONObject();
//        JSONObject json_obj = new JSONObject();
//        json_obj.put("token", userDeviceIdKey);
//        json_obj.put("data", json_data);
//        json_obj.put("apns", json_payload);
//        json.put("message", json_obj);
//        return json;
//    }

    /**
     * Send request to FCM message using HTTP.
     * Encoded with UTF-8 and support special characters.
     *
     * @param title
     * @param description
     * @param noti_id
     * @throws IOException
     * @throws IOException
     */
    @Async
    public void pushNotificationWithJsonData(String title, String description, String noti_id)
            throws IOException, JSONException {

        String userDeviceIdKey = "cjXADdcGQs-G7N6ks5lXS-:APA91bFJPY_8iI-06ARajU1WaNDtZNXi4AphlojKx4fKsu9YArq1FFZ_jvkyC21rfwF-28VrUUNCOhXgQsLoCn8EV1WfZb4vzxgg7uBIjbqArY5-ZX39E8nzfmF9Ugy1HXhpDzczbXEx";
//        Optional<FirebaseUser> userToPush = firebaseUserRepository.findTopByNationalCodeAndIsTrustedIsTrue(String.valueOf(noti_id));
//        if (userToPush.isPresent()) {
//            userDeviceIdKey = userToPush.get().getFcmToken();
//        }
//        else
//            throw new PushNotificationException("user.with.platform.appName.not.trusted", HttpStatus.NOT_FOUND);

        HttpURLConnection httpURLConnection = getConnection(path);
        OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);

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
        JsonObject req = buildMyNotificationMessage(userDeviceIdKey, title, description);
        wr.write(req.toString());
        wr.flush();
        wr.close();

        log.info("===( Start PushNotiHttpV1Service response log )===");
        int responseCode = httpURLConnection.getResponseCode();

        if (responseCode == 200) {
            String response = inputstreamToString(httpURLConnection.getInputStream());
            log.info("Response Code : " + responseCode);
            log.info("Response Message : " + httpURLConnection.getResponseMessage());
            log.info("Sending 'POST' request to URL : " + path);
            log.info("Post parameters : " + req);
            log.info("Message sent to Firebase for delivery, response:");
            log.info(response);
        } else {
            log.info("Response Code : " + responseCode);
            log.info("Response Message : " + httpURLConnection.getResponseMessage());
            log.info("Unable to send message to Firebase:");
            String response = inputstreamToString(httpURLConnection.getErrorStream());
            log.info(response);
        }
        log.info("===( End PushNotiHttpV1Service response log )===");
    }

    public void sendMulticast() throws FirebaseMessagingException {
        // [START send_multicast]
        // MulticastMessage class as part of the Firebase Admin SDK, simplifies managing the batch requests and error handling.
        // Create a list containing up to 500 registration tokens.
        // These registration tokens come from the client FCM SDKs.
        List<String> registrationTokens = Arrays.asList(
                "YOUR_REGISTRATION_TOKEN_1",
                // ...
                "YOUR_REGISTRATION_TOKEN_n"
        );

        List<List<String>> batches = Lists.partition(registrationTokens, 500); // This splits your token list into batches of 500

        for (List<String> batch : batches) {
            MulticastMessage message = MulticastMessage.builder()
//                .putData("score", "850")
//                .putData("time", "2:45")
                    .putData("title", "my title")
                    .putData("body", "my body")
                    .addAllTokens(batch)
                    .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            // See the BatchResponse reference documentation
            // for the contents of response.
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
            // [END send_multicast]
        }
    }
//
//    public  void batchSetObjectMetadata(String bucketName, String directoryPrefix) {
//        // The ID of your GCS bucket
//        // String bucketName = "your-unique-bucket-name";
//
//        // The directory prefix. All objects in the bucket with this prefix will have their metadata
//        // updated
//        // String directoryPrefix = "yourDirectory/";
//
//        Storage storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
//        Map<String, String> newMetadata = new HashMap<>();
//        newMetadata.put("keyToAddOrUpdate", "value");
//        Page<Blob> blobs =
//                storage.list(
//                        bucketName,
//                        Storage.BlobListOption.prefix(directoryPrefix),
//                        Storage.BlobListOption.currentDirectory());
//        StorageBatch batchRequest = storage.batch();
//
//        // Add all blobs with the given prefix to the batch request
//        for (Blob blob : blobs.iterateAll()) {
//            batchRequest.update(blob.toBuilder().setMetadata(newMetadata).build());
//        }
//
//        // Execute the batch request
//        batchRequest.submit();
//
//        System.out.println(
//                "All blobs in bucket "
//                        + bucketName
//                        + " with prefix '"
//                        + directoryPrefix
//                        + "' had their metadata updated.");
//
//
//
//        MultipartBuilder multipartBuilder = new MultipartBuilder();
//        MediaType http = MediaType.parse("application/http");
//        MediaType mixed = MediaType.parse("multipart/mixed");
//        byte[] request = "https://www.googleapis.com/gmail/v1/users/me/messages".getBytes();
//        multipartBuilder.addPart(RequestBody.create(http, "GET https://www.googleapis.com/gmail/v1/users/me/messages"));
//        multipartBuilder.type(mixed);
//        Request request = new Request.Builder()
//                .url("https://www.googleapis.com/batch")
//                .header("Authorization", " Bearer " + token)
//                .post(multipartBuilder.build())
//                .build();
//
//        try{
//            Response response = client.newCall(request).execute();
//            String res = response.body().string();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }

}

