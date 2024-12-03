package ir.co.sadad.pushnotification.common.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Converter
public class UUIDToBytesConverter implements AttributeConverter<UUID, byte[]> {

    // Caches for UUID
    private static final ConcurrentHashMap<UUID, byte[]> uuidToBytesCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, UUID> bytesToUuidCache = new ConcurrentHashMap<>();

    @Override
    public byte[] convertToDatabaseColumn(UUID uuid) {
        if (uuid == null) return null;

        // Check cache first
        return uuidToBytesCache.computeIfAbsent(uuid, key -> {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            return buffer.array();
        });
    }

    @Override
    public UUID convertToEntityAttribute(byte[] bytes) {
        if (bytes == null || bytes.length != 16) return null;

        // Convert byte[] to a unique cache key (using hex representation)
        String bytesKey = bytesToHex(bytes);

        // Check cache first
        return bytesToUuidCache.computeIfAbsent(bytesKey, key -> {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            long mostSigBits = buffer.getLong();
            long leastSigBits = buffer.getLong();
            return new UUID(mostSigBits, leastSigBits);
        });
    }

    // Helper method to convert byte array to a unique hex string key
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static PrivateKey readPrivateKeyFromString(String key) throws Exception {
        // Remove PEM headers
        String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        // Decode the base64 string
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

        // Convert to RSAPrivateKey
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
