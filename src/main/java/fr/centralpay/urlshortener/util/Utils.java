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

/**
 * Classe contenant les méthodes utiles utilisées par l'application
 */
public class Utils {

    private Utils() {
        // Constructeur privé
    }

    /**
     * Récupération de l'instance d'un {@link MessageDigest} correspondance à l'algorithme de hash MD5
     *
     * @return l'instance de {@link MessageDigest} MD5
     */
    public static MessageDigest getMD5Digest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralTechnicalException();
        }
    }

    /**
     * Récupération de l'instance d'un {@link MessageDigest} correspondance à l'algorithme de hash SHA-256
     *
     * @return l'instance de {@link MessageDigest} SHA-256
     */
    public static MessageDigest getSHA256Digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralTechnicalException();
        }
    }

    /**
     * Méthode qui va créer une response HTTP générique depuis une {@link ApiErrorDTO}
     *
     * @param error l'{@link ApiErrorDTO} depuis laquelle créer une réponse HTTP
     * @return {@link ResponseEntity} avec un {@link Object} générique dans le corps de cette dernière
     */
    public static ResponseEntity<Object> buildErrorResponse(ApiErrorDTO error) {
        return new ResponseEntity<>(error, error.requestStatus());
    }

    /**
     * Méthode qui permet de hash une chaîne de caractère
     *
     * @param msgDigest    le {@link MessageDigest} utilisé pour hash la chaîne de caractères
     * @param stringToHash la chaîne de caractère à hashée
     * @return la chaîne hashée
     */
    public static String hashAndShortenString(MessageDigest msgDigest, String stringToHash) {
        return StringUtils.substring(String.format("%32x", new BigInteger(1, msgDigest.digest(stringToHash.getBytes(StandardCharsets.UTF_8)))), 0, 8).trim();
    }

    /**
     * Méthode qui va retourner l'URL "complète" depuis le hash généré pour que l'utilisateur puisse la partager
     *
     * @param hash le hash en entrée
     * @return l'URL courte complète
     */
    public static String getShortUrlForCurrentServerFromHash(String hash) {
        return StringUtils.join(ServletUriComponentsBuilder.fromCurrentContextPath().build().toString(), Constants.SLASH_STRING, Constants.REST_MAPPING, Constants.SLASH_STRING, hash);
    }

    /**
     * Méthode qui va générer un random UUID raccourci à 8 caractères
     *
     * @return l'UUID raccourci
     */
    public static String getShortRandomUuid() {
        return StringUtils.substring(UUID.randomUUID().toString(), 0, 8).trim();
    }

    /**
     * Méthode qui transforme des bytes en une chaîne de caractère encodée en base64
     *
     * @param byteArray le tableau d'entrée
     * @return une chaîne de caractère en base64
     */
    public static String convertByteArrayToBase64EncodedString(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }


}
