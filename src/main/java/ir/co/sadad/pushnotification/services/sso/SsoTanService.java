package ir.co.sadad.pushnotification.services.sso;

public interface SsoTanService {

    void sendTanRequest(String userPasswordToken);
    void tanVerification(String userPasswordToken, String otpCode);
}
