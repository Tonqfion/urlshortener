package fr.centralpay.urlshortener.model.dto;

/**
 * Record représentant la requête reçue lors de la demande de raccourcissement d'une URL
 *
 * @param url
 */
public record UrlRequestDTO(String url) {
}
