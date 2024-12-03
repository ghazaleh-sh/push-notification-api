package ir.co.sadad.pushnotification.services;

import ir.co.sadad.pushnotification.dtos.MultiMessageReqDto;
import ir.co.sadad.pushnotification.dtos.SingleMessageReqDto;
import reactor.core.publisher.Mono;

public interface FirebaseCloudMessagingService {

    void sendSingle(SingleMessageReqDto msgReq);

    /**
     * MulticastMessage class as part of the Firebase Admin SDK
     * Create notification(including title, body, hyperlink) and a list containing up to 500 registration tokens.
     */
    Mono<Void> sendMulticast(MultiMessageReqDto msgReq);

//    void sendSingleWithJwt(SingleMessageReqDto msgReq);
}
