package ir.co.sadad.pushnotification.services;

//import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A basic service for making connection with google and getting access token to call firebase APIs Http v1
 */
public abstract class FcmService {

//    //    @Value(value = "${fcm.scope}")
//    private static String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
//    private static final String[] SCOPES = {MESSAGING_SCOPE};
//
//    @Value(value = "${fcm.endpoint.base-url}")
//    private String BASE_URL;
//    @Value(value = "${fcm.endpoint.send-url}")
//    private String FCM_SEND_ENDPOINT;
//    protected  String path = BASE_URL + FCM_SEND_ENDPOINT;
//
//    public static final String MESSAGE_KEY = "message";
//
//    /**
//     * To get Credentials from a Service Account JSON key use GoogleCredentials.fromStream(InputStream) or
//     * GoogleCredentials.fromStream(InputStream, HttpTransportFactory).
//     * Note that the credentials must be refreshed before the access token is available.
//     * <p>
//     * Use your Firebase credentials together with the Google Auth Library for your preferred language
//     * to Retrieve a valid (a short-lived OAuth 2.0) access token that can be use to authorize requests to the FCM REST API.
//     * <p>
//     * After your access token expires, the token refresh method is called automatically to retrieve an updated access token.
//     *
//     * @return Access token.
//     * @throws IOException
//     */
//    // [START retrieve_access_token]
//    private static String getAccessToken() throws IOException {
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new FileInputStream("google-services.json"))
//                .createScoped(Arrays.asList(SCOPES));
//        return googleCredentials.refreshAccessToken().getTokenValue();
////        return googleCredentials.getAccessToken().getTokenValue();
//    }
//    // [END retrieve_access_token]
//
//    /**
//     * Create HttpURLConnection that can be used for both retrieving and publishing.
//     *
//     * @return Base HttpURLConnection.
//     * @throws IOException
//     */
//    protected static HttpURLConnection getConnection(String path) throws IOException {
//        // [START use_access_token]
//        URL url = new URL(path);
//        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//
//        httpURLConnection.setUseCaches(false);
//        httpURLConnection.setDoInput(true);
//        httpURLConnection.setDoOutput(true);
//
//        httpURLConnection.setRequestMethod("POST");
//
//        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
//        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
//        return httpURLConnection;
//        // [END use_access_token]
//    }


    /**
     * Read contents of InputStream into String.
     *
     * @param inputStream InputStream to read.
     * @return String containing contents of InputStream.
     * @throws IOException
     */
    protected static String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

}
