package com.enspy.csi.config;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtUtil {

    private static final String SECRET = "a-very-long-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-256-securite-sociale-csi";

    public String generateToken(String email, String role) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            long now = System.currentTimeMillis();
            long exp = now + 1000L * 60 * 60 * 24; // 24 hours
            String payload = String.format("{\"sub\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}", email, role, now / 1000, exp / 1000);

            String encodedHeader = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
            String encodedPayload = base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8));

            String signatureInput = encodedHeader + "." + encodedPayload;
            String signature = hmacSha256(signatureInput, SECRET);

            return signatureInput + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Erreur de génération du token JWT", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String decodedPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int subIndex = decodedPayload.indexOf("\"sub\":\"");
            if (subIndex == -1) return null;
            int start = subIndex + 7;
            int end = decodedPayload.indexOf("\"", start);
            return decodedPayload.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String decodedPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int roleIndex = decodedPayload.indexOf("\"role\":\"");
            if (roleIndex == -1) return null;
            int start = roleIndex + 8;
            int end = decodedPayload.indexOf("\"", start);
            return decodedPayload.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String encodedHeader = parts[0];
            String encodedPayload = parts[1];
            String signature = parts[2];

            String signatureInput = encodedHeader + "." + encodedPayload;
            String expectedSignature = hmacSha256(signatureInput, SECRET);
            if (!expectedSignature.equals(signature)) {
                return false;
            }

            String decodedPayload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            int expIndex = decodedPayload.indexOf("\"exp\":");
            if (expIndex == -1) return false;
            int start = expIndex + 6;
            int end = start;
            while (end < decodedPayload.length() && Character.isDigit(decodedPayload.charAt(end))) {
                end++;
            }
            long exp = Long.parseLong(decodedPayload.substring(start, end));
            if (System.currentTimeMillis() / 1000 > exp) {
                return false;
            }

            String tokenEmail = getEmailFromToken(token);
            return email.equals(tokenEmail);
        } catch (Exception e) {
            return false;
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256.init(secretKey);
        byte[] hash = sha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(hash);
    }
}
