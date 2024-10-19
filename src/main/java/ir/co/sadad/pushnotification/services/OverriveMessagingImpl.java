package ir.co.sadad.pushnotification.services;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * A test class to modify firebase messages based on platform
 */
public class OverriveMessagingImpl extends FcmService{


//    private static  String TITLE = "FCM Notification Ghazaleh";
//    private static  String BODY = "Notification from FCM";
//
//    /**
//     * Send a message that uses the common FCM fields to send a notification message to all
//     * platforms. Also platform specific overrides are used to customize how the message is
//     * received on Android and iOS.
//     *
//     * @throws IOException
//     */
//    private static void sendOverrideMessage(String path) throws IOException {
//        JsonObject overrideMessage = buildOverrideMessage();
//        System.out.println("FCM request body for override message:");
//        prettyPrint(overrideMessage);
//        sendMessage(overrideMessage, path);
//    }
//
//    /**
//     * {
//     * "message": {
//     * "topic": "news",
//     * "notification": {
//     * "title": "Breaking News",
//     * "body": "New news story available."
//     * },
//     * "data": {
//     * "story_id": "story_12345"
//     * },
//     * "android": {
//     * "notification": {
//     * "click_action": "TOP_STORY_ACTIVITY",
//     * "body": "Check out the Top Story"
//     * }
//     * },
//     * // For send to iOS
//     * "apns": {
//     * "payload": {
//     * "aps": {
//     * "category" : "NEW_MESSAGE_CATEGORY"
//     * }
//     * }
//     * }
//     * }
//     * }
//     * <p>
//     * Build the body of an FCM request. This body defines the common notification object
//     * as well as platform specific customizations using the android and apns objects.
//     *
//     * @return JSON representation of the FCM request body.
//     */
//    private static JsonObject buildOverrideMessage() {
//        JsonObject jNotificationMessage = buildNotificationMessage();
//
//        JsonObject messagePayload = jNotificationMessage.get(MESSAGE_KEY).getAsJsonObject();
//        messagePayload.add("android", buildAndroidOverridePayload());
//
//        JsonObject apnsPayload = new JsonObject();
//        apnsPayload.add("headers", buildApnsHeadersOverridePayload());
//        apnsPayload.add("payload", buildApsOverridePayload());
//
//        messagePayload.add("apns", apnsPayload);
//
//        jNotificationMessage.add(MESSAGE_KEY, messagePayload);
//
//        return jNotificationMessage;
//    }
//
//
//    /**
//     * Build the android payload that will customize how a message is received on Android.
//     *
//     * @return android payload of an FCM request.
//     */
//    private static JsonObject buildAndroidOverridePayload() {
//        JsonObject androidNotification = new JsonObject();
//        androidNotification.addProperty("click_action", "android.intent.action.MAIN");
//
//        JsonObject androidNotificationPayload = new JsonObject();
//        androidNotificationPayload.add("notification", androidNotification);
//
//        return androidNotificationPayload;
//    }
//
//    /**
//     * Build the apns payload that will customize how a message is received on iOS.
//     *
//     * @return apns payload of an FCM request.
//     */
//    private static JsonObject buildApnsHeadersOverridePayload() {
//        JsonObject apnsHeaders = new JsonObject();
//        apnsHeaders.addProperty("apns-priority", "10");
//
//        return apnsHeaders;
//    }
//
//    /**
//     * Build aps payload that will add a badge field to the message being sent to
//     * iOS devices.
//     *
//     * @return JSON object with aps payload defined.
//     */
//    private static JsonObject buildApsOverridePayload() {
//        JsonObject badgePayload = new JsonObject();
//        badgePayload.addProperty("badge", 1);
//
//        JsonObject apsPayload = new JsonObject();
//        apsPayload.add("aps", badgePayload);
//
//        return apsPayload;
//    }
//
//    /**
//     * Send notification message to FCM for delivery to registered devices.
//     *
//     * @throws IOException
//     */
//    public void sendCommonMessage(String path) throws IOException {
//        JsonObject notificationMessage = buildNotificationMessage();
//        System.out.println("FCM request body for message using common notification object:");
//        prettyPrint(notificationMessage);
//        sendMessage(notificationMessage, path);
//    }
//
//
//    /**
//     * Send request to FCM message using HTTP.
//     * Encoded with UTF-8 and support special characters.
//     *
//     * @param fcmMessage Body of the HTTP request.
//     * @throws IOException
//     */
//    private static void sendMessage(JsonObject fcmMessage, String path) throws IOException {
//        HttpURLConnection connection = getConnection(path);
////        connection.setDoOutput(true);
//        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
//        writer.write(fcmMessage.toString());
//        writer.flush();
//        writer.close();
//
//        int responseCode = connection.getResponseCode();
//        if (responseCode == 200) {
//            String response = inputstreamToString(connection.getInputStream());
//            System.out.println("Message sent to Firebase for delivery, response:");
//            System.out.println(response);
//        } else {
//            System.out.println("Unable to send message to Firebase:");
//            String response = inputstreamToString(connection.getErrorStream());
//            System.out.println(response);
//        }
//    }
//
//    /**
//     * https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send
//     * Content-Type:application/json
//     * Authorization: Bearer ya29.ElqKBGN2Ri_Uz...HnS_uNreA
//     * <p>
//     * {
//     * "message":{
//     * "topic": "paralela",
//     * "notification" : {
//     * "body" : "This is a Firebase Cloud Messaging Topic Message!",
//     * "title" : "FCM Message"
//     * }
//     * }
//     * }
//     * <p>
//     * ---------------------------------------------------------------------
//     * {
//     * "message":{
//     * "token" : "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1..."
//     * "data": {
//     * "score": "5x1",
//     * "time": "15:10"
//     * },
//     * "android": {
//     * "direct_boot_ok": true,
//     * },
//     * }
//     * <p>
//     * <p>
//     * Construct the body of a notification message request.
//     *
//     * @return JSON of notification message.
//     */
//    private static JsonObject buildNotificationMessage() {
//        JsonObject jNotification = new JsonObject();
//        jNotification.addProperty("title", TITLE);
//        jNotification.addProperty("body", BODY);
//
//        JsonObject jMessage = new JsonObject();
//        jMessage.add("notification", jNotification);
//        jMessage.addProperty("topic", "news");
//
//        JsonObject jFcm = new JsonObject();
//        jFcm.add(MESSAGE_KEY, jMessage);
//
//        return jFcm;
//    }
//
//
//    /**
//     * Pretty print a JsonObject.
//     *
//     * @param jsonObject JsonObject to pretty print.
//     */
//    private static void prettyPrint(JsonObject jsonObject) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println(gson.toJson(jsonObject) + "\n");
//    }
//
//    public static void main(String[] args) throws IOException {
//        if (args.length == 1 && args[0].equals("common-message")) {
//            sendCommonMessage(path);
//        } else if (args.length == 1 && args[0].equals("override-message")) {
//            sendOverrideMessage(path);
//        } else {
//            System.err.println("Invalid command. Please use one of the following commands:");
//            // To send a simple notification message that is sent to all platforms using the common
//            // fields.
//            System.err.println("./gradlew run -Pmessage=common-message");
//            // To send a simple notification message to all platforms using the common fields as well as
//            // applying platform specific overrides.
//            System.err.println("./gradlew run -Pmessage=override-message");
//        }
//    }

}
