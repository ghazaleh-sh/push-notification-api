package ir.co.sadad.pushnotification.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseSDKInitializer {

    @Value(value = "${fcm.service.account}")
    private static final String accountJsonPath;

    @Bean
    public FirebaseMessaging FirebaseInitializer() throws IOException {
        try (InputStream serviceAccount = getClass().getResourceAsStream(accountJsonPath)) {
            if (serviceAccount == null) {
                throw new IOException("Firebase JSON file not found at path: " + accountJsonPath);
            }
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);

            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(googleCredentials).build();
            FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions);
            log.info("Firebase App initialized: {}", app.getName());
            return FirebaseMessaging.getInstance(app);
        }
    }
}
