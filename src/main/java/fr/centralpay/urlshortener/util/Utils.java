package fr.centralpay.urlshortener.util;

import fr.centralpay.urlshortener.exception.GeneralTechnicalException;
import fr.centralpay.urlshortener.model.dto.ApiErrorDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class Utils {

    private Utils() {
        // Constructeur privé
    }

    public static MessageDigest getMD5Digest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralTechnicalException();
        }
    }

    public static MessageDigest getSHA256Digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralTechnicalException();
        }
    }

    public static ResponseEntity<Object> buildErrorResponse(ApiErrorDTO error) {
        return new ResponseEntity<>(error, error.requestStatus());
    }

    public static String hashAndShortenString(MessageDigest msgDigest, String stringToHash) {
        return StringUtils.substring(String.format("%32x", new BigInteger(1, msgDigest.digest(stringToHash.getBytes(StandardCharsets.UTF_8)))), 0, 8).trim();
    }

    public static String getShortUrlForCurrentServerFromHash(String hash) {
        return StringUtils.join(ServletUriComponentsBuilder.fromCurrentContextPath().build().toString(), Constants.SLASH_STRING, Constants.REST_MAPPING, Constants.SLASH_STRING, hash);
    }

    public static String getShortRandomUuid() {
        return StringUtils.substring(UUID.randomUUID().toString(), 0, 8).trim();
    }

    public static String convertByteArrayToBase64EncodedString(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }


}
