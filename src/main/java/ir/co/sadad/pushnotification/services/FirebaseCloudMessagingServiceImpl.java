package ir.co.sadad.pushnotification.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.*;
import com.google.gson.JsonObject;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import ir.co.sadad.pushnotification.dtos.MultiMessageReqDto;
import ir.co.sadad.pushnotification.dtos.SingleMessageReqDto;
import ir.co.sadad.pushnotification.entities.FirebaseUser;
import ir.co.sadad.pushnotification.enums.NoticeType;
import ir.co.sadad.pushnotification.enums.UserPlatform;
import ir.co.sadad.pushnotification.repositories.FirebaseUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ir.co.sadad.pushnotification.common.Constants.HTTPV1_ENDPOINT;
import static ir.co.sadad.pushnotification.common.Constants.MESSAGE_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseCloudMessagingServiceImpl implements FirebaseCloudMessagingService {

    //    @Value(value = "${fcm.service.account}")
    private final String fcm_account_fileName = "/static/agpush-test-f9450c25240b.json";

    @Value(value = "${fcm.service.project-id}")
    private String project_id;

    //    @Value(value = "${fcm.service.scope}")
    private static final String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";

    @Value(value = "${fcm.endpoint.httpV1-send-url}")
    private String path;

    private final FirebaseUserRepository firebaseUserRepository;
    private final WebClient webClient;

    @SneakyThrows
//    @RateLimiter(name = "singleRateLimit", fallbackMethod = "getSingleMessageFallbackMethod")
    public void sendSingle(SingleMessageReqDto msgReq) {
        if (msgReq.getSsn() == null || msgReq.getSsn().isEmpty())
            return;

        List<String> userFCMToken;
        List<FirebaseUser> userInfoToPush;
        boolean allPlatform = msgReq.getPlatform() == null || UserPlatform.ALL.toString().equals(msgReq.getPlatform());

        if (NoticeType.TRANSACTION.getValue().equals(msgReq.getNoticeType())) {
            userInfoToPush = allPlatform
                    ? firebaseUserRepository.findByNationalCodeAndIsActivatedOnTransactionIsTrue(msgReq.getSsn())
                    : firebaseUserRepository.findByNationalCodeAndUserPlatformAndIsActivatedOnTransactionIsTrue(msgReq.getSsn(), UserPlatform.valueOf(msgReq.getPlatform()));
        } else {
            userInfoToPush = allPlatform
                    ? firebaseUserRepository.findByNationalCode(msgReq.getSsn())
                    : firebaseUserRepository.findByNationalCodeAndUserPlatform(msgReq.getSsn(), UserPlatform.valueOf(msgReq.getPlatform()));

        }

        if (!userInfoToPush.isEmpty()) {
            userFCMToken = userInfoToPush.stream().map(FirebaseUser::getFcmToken).toList();

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
                        .bodyValue(buildMyNotificationMessage(token, msgReq))
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleErrorResponse(clientResponse, "4XX"))
                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleErrorResponse(clientResponse, "5XX"))
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Error sending Http v1 message: {}  on userFCMToken: {}", e.getMessage(), token)) // Log error without stopping
                        .subscribe() // Subscribe to execute asynchronously and continue to next batch
        );

    }

    private Mono<Throwable> handleErrorResponse(ClientResponse clientResponse, String errorType) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    log.error("Error {} response body: {}", errorType, errorBody);
                    return Mono.error(new RuntimeException(errorType + " Error: " + errorBody));
                });
    }

    @SneakyThrows
    private String getAccessToken() {
        try (InputStream serviceAccount = getClass().getResourceAsStream(fcm_account_fileName)) {
            if (serviceAccount == null) {
                throw new IOException("Firebase JSON file not found at path: " + fcm_account_fileName);
            }

            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(serviceAccount)
                    .createScoped(Arrays.asList(SCOPES));

            try {
                googleCredentials.refreshIfExpired();
            } catch (IOException e) {
                log.info("-------------------------------------Token Refresh Failed: {}", e.getMessage());

            }
            String accessToken = googleCredentials.getAccessToken().getTokenValue();
            log.info("----------------access token created successfully");
            return accessToken;
        }
    }

//    {
//        "message": {
//        "notification": {
//            "title": "Flash Sale!",
//             "body": "Limited time offer."
//        },
//        "token": "device_token_here",
//        "data": {
//            "hyperlink": "https://example.com/sale"
//        },
//        "android": {
//            "analyticsLabel": "Flash Sale!"
//        }
//    }
//    }
    private String buildMyNotificationMessage(String deviceToken, SingleMessageReqDto msgReqDto) {
        log.info("---------------buildMyNotificationMessage method");
        JsonObject jNotification = new JsonObject();
        jNotification.addProperty("title", msgReqDto.getTitle());
        jNotification.addProperty("body", msgReqDto.getDescription());

        JsonObject jMessage = new JsonObject();
        jMessage.add("notification", jNotification);
        jMessage.addProperty("token", deviceToken);

        JsonObject jData = new JsonObject();
        if (msgReqDto.getHyperlink() != null) {
            jData.addProperty("hyperlink", msgReqDto.getHyperlink());
        }
        jMessage.add("data", jData);

//        JsonObject jAndroid = new JsonObject();
//        jAndroid.addProperty("analyticsLabel", msgReqDto.getTitle());
//        jMessage.add("android", jAndroid);

        JsonObject jFcm = new JsonObject();
        jFcm.add(MESSAGE_KEY, jMessage);

        return jFcm.toString();
    }

    //when the rate limit is exceeded, the application gracefully handles the situation by executing the fallback method
    private void getSingleMessageFallbackMethod(SingleMessageReqDto msgReq, RequestNotPermitted requestNotPermitted) {
        log.info("Fallback method called.");
        log.info("RequestNotPermitted exception message: {}", requestNotPermitted.getMessage());
    }

    ///////////////////////////////////////////////send multicast using firebase admin SDK///////////////////////////////////////////
    public Mono<Void> sendMulticast(MultiMessageReqDto msgReq) {
        boolean isTransactionNotice = NoticeType.TRANSACTION.getValue().equals(msgReq.getNoticeType());
        boolean isGeneralNotice = msgReq.getSuccessSsn().isEmpty();

        List<String> registrationTokens = isGeneralNotice
                ? getTokensForGeneral(msgReq.getPlatform(), isTransactionNotice)
                : getTokensForSsnList(msgReq.getSuccessSsn(), msgReq.getPlatform(), isTransactionNotice);


        if (!registrationTokens.isEmpty()) {
            Notification campaingNotification = Notification.builder()
                    .setTitle(msgReq.getTitle())
                    .setBody(msgReq.getDescription())
                    .build();

            return callMulticastSDKService(campaingNotification, msgReq.getHyperlink(), registrationTokens)
                    .doOnError(e -> log.error("Error occurred while sending multicast message: {}", e.getMessage(), e))
                    .onErrorResume(e -> Mono.empty());
        }

        return Mono.empty();
    }

    private List<String> getTokensForGeneral(String platform, boolean isTransactionNotice) {
        List<FirebaseUser> usersForGeneral;
        boolean allPlatform = platform == null || UserPlatform.ALL.toString().equals(platform);

        if (isTransactionNotice) {
            usersForGeneral = allPlatform
                    ? firebaseUserRepository.findAllByIsActivatedOnTransactionIsTrue()
                    : firebaseUserRepository.findByUserPlatformAndIsActivatedOnTransactionIsTrue(UserPlatform.valueOf(platform));
        } else {
            usersForGeneral = allPlatform
                    ? firebaseUserRepository.findAll()
                    : firebaseUserRepository.findByUserPlatform(UserPlatform.valueOf(platform));
        }

        return usersForGeneral.stream()
                .map(FirebaseUser::getFcmToken)
                .toList();
    }

    private List<String> getTokensForSsnList(List<String> ssnList, String platform, boolean isTransactionNotice) {
        boolean allPlatform = platform == null || UserPlatform.ALL.toString().equals(platform);
        return ssnList.stream()
                .flatMap(givenSsn -> {
                    List<FirebaseUser> savedUsers;
                    if (isTransactionNotice) {
                        savedUsers = allPlatform
                                ? firebaseUserRepository.findByNationalCodeAndIsActivatedOnTransactionIsTrue(givenSsn)
                                : firebaseUserRepository.findByNationalCodeAndUserPlatformAndIsActivatedOnTransactionIsTrue(givenSsn, UserPlatform.valueOf(platform));
                    } else {
                        savedUsers = allPlatform
                                ? firebaseUserRepository.findByNationalCode(givenSsn)
                                : firebaseUserRepository.findByNationalCodeAndUserPlatform(givenSsn, UserPlatform.valueOf(platform));
                    }
                    return savedUsers.stream()
                            .map(FirebaseUser::getFcmToken);
                })
                .toList();
    }


    @RateLimiter(name = "multipleRateLimit")
    public Mono<Void> callMulticastSDKService(Notification campaignNotification, String hyperlink, List<String> registrationTokens) {

        List<List<String>> batches = Lists.partition(registrationTokens, 500);

        // Shared queue to store failed tokens across all batches
        ConcurrentLinkedQueue<String> failedTokensQueue = new ConcurrentLinkedQueue<>();

        return Flux.fromIterable(batches)
                .flatMap(batch -> {
                    MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                            .setNotification(campaignNotification)
                            .addAllTokens(batch);

                    if (hyperlink != null) {
                        messageBuilder.putData("hyperlink", hyperlink);
                    }

                    MulticastMessage message = messageBuilder.build();
                    //each batch is sent in parallel without blocking the thread
                    return Mono.fromCallable(() -> {
                        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
                        List<String> failedTokens = response.getResponses().stream()
                                .filter(res -> res.getException() != null)
                                .map(res -> batch.get(response.getResponses().indexOf(res)))
                                .toList();

                        failedTokensQueue.addAll(failedTokens);

                        log.info("{} messages sent successfully", response.getSuccessCount());
                        log.info("{} messages failed", response.getFailureCount());
                        return response;

                    }).onErrorResume(e -> {
                        log.error("Batch-level error occurred: {}", e.getMessage(), e);
                        return Mono.empty(); // Return empty Mono to continue processing other batches
                    });

                }, 5)
                .then(Mono.defer(() -> retryMultipleFallback(campaignNotification, hyperlink, failedTokensQueue))); // Trigger fallback after all batches
    }

    public Mono<Void> retryMultipleFallback(Notification campaignNotification, String hyperlink, ConcurrentLinkedQueue<String> failedTokensQueue) {
        log.info("Retrying {} failed tokens", failedTokensQueue.size());

        List<String> failedTokensToRetry = new ArrayList<>(failedTokensQueue);
        failedTokensQueue.clear();

        // Retry logic for failed tokens just once
        return Flux.fromIterable(failedTokensToRetry)
                .flatMap(token -> {
                    Message.Builder messageBuilder = Message.builder()
                            .setNotification(campaignNotification)
                            .setToken(token); // Send to a single token

                    if (hyperlink != null) {
                        messageBuilder.putData("hyperlink", hyperlink);
                    }

                    Message message = messageBuilder.build();

                    return Mono.fromCallable(() -> {
                                try {
                                    FirebaseMessaging.getInstance().send(message);
                                    log.info("Retry success for token: {}", token);
                                    return token;
                                } catch (FirebaseMessagingException ex) {
                                    log.error("Retry failed for token: {}", token, ex);
                                    return ex; // Mark as failed again
                                }
                            })
                            .onErrorResume(ex -> {
                                return Mono.empty(); // Skip failed token
                            });
                })
                .then();
    }


    ////////////////////////////////////////////////call via creating JWT manually///////////////////////////////////////////
//    public void sendSingleWithJwt(SingleMessageReqDto msgReq) {
//        if (msgReq.getSsn() == null || msgReq.getSsn().isEmpty())
//            return;
//
//        List<String> userFCMToken;
//        List<FirebaseUser> userInfoToPush;
//
//        if (msgReq.getPlatform() == null || UserPlatform.ALL.toString().equals(msgReq.getPlatform()))
//            userInfoToPush = firebaseUserRepository.findByNationalCode(msgReq.getSsn());
//        else
//            userInfoToPush = firebaseUserRepository.findByNationalCodeAndUserPlatform(
//                    msgReq.getSsn(), UserPlatform.valueOf(msgReq.getPlatform()));
//
//
//        if (NoticeType.TRANSACTION.getValue().equals(msgReq.getNoticeType())) {
//            userInfoToPush = userInfoToPush.stream().filter(FirebaseUser::getIsActivatedOnTransaction).toList();
//        }
//
//        if (!userInfoToPush.isEmpty()) {
//            userFCMToken = userInfoToPush.stream().map(FirebaseUser::getFcmToken).toList();
//
//            callHttpV1ApiWithJwtToken(msgReq, userFCMToken);
//        }
//
//    }
//
//    protected void callHttpV1ApiWithJwtToken(SingleMessageReqDto msgReq, List<String> userFCMToken) {
//        userFCMToken.forEach(token ->
//                webClient
//                        .post()
//                        .uri(path + project_id + HTTPV1_ENDPOINT)
//                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessTokenViaJwt())
//                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .bodyValue(buildMyNotificationMessage(token, msgReq.getTitle(), msgReq.getDescription()))
//                        .retrieve()
//                        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleErrorResponse(clientResponse, "4XX"))
//                        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleErrorResponse(clientResponse, "5XX"))
//                        .bodyToMono(String.class)
//                        .doOnError(e -> log.error("Error sending Http v1 with jwt message: {}  on userFCMToken: {}", e.getMessage(), token)) // Log error without stopping
//                        .subscribe() // Subscribe to execute asynchronously and continue to next batch
//        );
//
//    }
//
//    @SneakyThrows
//    private String getAccessTokenViaJwt() {
//        String generatedJwt = generateJwt();
//        String generatedJwtToken = Objects.requireNonNull(generatedJwt).replaceAll("\\n", "").trim();
//
//        try {
//            String base64Token = Objects.requireNonNull(generatedJwt).split("\\.")[1]; // Get the payload part
//            byte[] decodedBytes = Base64.getDecoder().decode(base64Token);
//            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
//            log.info("--------------decodedString is: {}", decodedString);
//        } catch (Exception e) {
//            log.info("-----------error in decoding assertion: " + e.getMessage());
//        }
//
//        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
//        formParams.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
//        formParams.add("assertion", generatedJwtToken);
//
//        String accessToken = webClient
//                .post()
//                .uri("http://oauth2.googleapis.com:9080/token")
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
////                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
////                .bodyValue("grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + generatedJwtToken)
//                .body(BodyInserters.fromFormData(formParams))
//                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> handleErrorResponse(clientResponse, "4XX"))
//                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> handleErrorResponse(clientResponse, "5XX"))
//                .bodyToMono(Map.class) // Parse response as JSON map
//                .map(response -> {
//                    log.info("response of this call: " + response);
//                    return (String) response.get("access_token");
//                })
//                .doOnError(e -> log.error("Error creating token with JWT: {}", e.getMessage()))
//                .block();
//        log.info("--------------getAccessTokenViaJwt method end with token: {}", accessToken);
//        return accessToken;
//    }
//
//    private String generateJwt() {
//        try {
//            InputStream serviceAccount = getClass().getResourceAsStream(fcm_account_fileName);
//            if (serviceAccount == null) {
//                throw new FileNotFoundException("Resource file 'agpush-test-f9450c25240b.json' not found in classpath");
//            }
//            JsonObject serviceAccountJson = JsonParser.parseReader(new InputStreamReader(serviceAccount)).getAsJsonObject();
//
//            String privateKeyPem = serviceAccountJson.get("private_key").getAsString();
//            String clientEmail = serviceAccountJson.get("client_email").getAsString();
//
//            // Convert the private key to RSAPrivateKey format
//            RSAPrivateKey privateKey = (RSAPrivateKey) UUIDToBytesConverter.readPrivateKeyFromString(privateKeyPem);
//
//            long now = System.currentTimeMillis();
//            String jwt = JWT.create()
//                    .withIssuer(clientEmail) // "iss" claim
//                    .withAudience("https://oauth2.googleapis.com/token") // "aud" claim
//                    .withClaim("scope", SCOPES) // "scope" claim
//                    .withIssuedAt(new Date(now)) // "iat" claim
//                    .withExpiresAt(new Date(now + 3600 * 1000)) // "exp" claim
//                    .sign(Algorithm.RSA256(null, privateKey)); // Sign with RSA256 and the private key
//
//            log.info("------------------------------Generated JWT: " + jwt);
//            return jwt;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }
//
//
//    protected Mono<Void> callMulticastViaRestApi(MultiMessageReqDto msgReqDto, List<String> registrationTokens) {
//        return Flux.fromIterable(registrationTokens)
//                .flatMap(token -> webClient.post()
//                                .uri(path + project_id + HTTPV1_ENDPOINT)
//                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
//                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                                .bodyValue(buildMyNotificationMessage(token, msgReqDto.getTitle(), msgReqDto.getDescription()))
//                                .retrieve()
//                                .bodyToMono(String.class)
////                                .doOnSuccess(response -> log.info("Message sent successfully to token {}", token))
//                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))) // Retry transient errors
//                                .onErrorResume(e -> {
//                                    log.error("Error sending message to token {}: {}", token, e.getMessage());
//                                    return Mono.empty(); // Continue processing even on error
//                                }),
//                        10 // Limit concurrency to 10 requests at a time
//                )
//                .onBackpressureBuffer(1000,
//                        buffer -> log.warn("------------------Backpressure applied."),
//                        BufferOverflowStrategy.DROP_OLDEST)
//                .then(); // Signal completion of all batches
//    }
//
//
//    @SneakyThrows
//    public static void disableSslVerification() {
//        TrustManager[] trustAllCerts = new TrustManager[]{
//                new X509TrustManager() {
//                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                        return null;
//                    }
//
//                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//                    }
//
//                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
//                    }
//                }
//        };
//
//        SSLContext sc = SSLContext.getInstance("TLS");
//        sc.init(null, trustAllCerts, new java.security.SecureRandom());
//        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//        HostnameVerifier allHostsValid = (hostname, session) -> true;
//        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//    }

    //-----------if SDK method wants to call through accessToken:
//        String accessToken = getAccessToken();
//
//        GoogleCredentials googleCredentials = GoogleCredentials.create(new AccessToken(accessToken, null));
//
//        FirebaseMessaging firebaseMessaging;
//        try {
//            firebaseMessaging = FirebaseMessaging.getInstance();
//        } catch (IllegalStateException e) {
//            // Initialize FirebaseApp if not already initialized
//            FirebaseApp defaultApp = FirebaseApp.initializeApp(FirebaseOptions.builder()
//                    .setCredentials(googleCredentials)
//                    .setProjectId("agpush-test")
//                    .build());
//            firebaseMessaging = FirebaseMessaging.getInstance(defaultApp);
//        }

}
