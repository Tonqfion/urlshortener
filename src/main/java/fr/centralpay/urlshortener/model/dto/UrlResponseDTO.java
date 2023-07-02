package fr.centralpay.urlshortener.model.dto;

import fr.centralpay.urlshortener.model.entity.UrlEntity;
import fr.centralpay.urlshortener.util.Utils;

/**
 * Record représentant le corps de l'objet renvoyé après la création réussie d'une URL courte
 *
 * @param shortUrl
 * @param qrCodeImageEncoded
 */
public record UrlResponseDTO(String shortUrl, String qrCodeImageEncoded) {

    public UrlResponseDTO(UrlEntity urlEntity) {
        this(Utils.getShortUrlForCurrentServerFromHash(urlEntity.getHashId()), urlEntity.getQrcodeImageString());
    }
}
