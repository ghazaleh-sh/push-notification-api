package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.dtos.MultiMessageReqDto;

public interface FirebaseCloudMessagingService {

    void pushNotificationWithJsonData(String title, String description, String noti_id);

    void sendSingle(MultiMessageReqDto msgReq);

    /**
     * MulticastMessage class as part of the Firebase Admin SDK
     * Create notification(including title, body, hyperlink) and a list containing up to 500 registration tokens.
     */
    default void sendMulticast(MultiMessageReqDto msgReq) {

    }
}
