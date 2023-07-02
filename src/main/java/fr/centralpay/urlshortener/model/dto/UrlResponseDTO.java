package fr.centralpay.urlshortener.model.dto;

import fr.centralpay.urlshortener.model.entity.UrlEntity;
import fr.centralpay.urlshortener.util.Utils;

public record UrlResponseDTO(String shortUrl, String qrCodeImageEncoded) {

    public UrlResponseDTO(UrlEntity urlEntity) {
        this(Utils.getShortUrlForCurrentServerFromHash(urlEntity.getHashId()), urlEntity.getQrcodeImageString());
    }
}
