package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.util.Constants;

/**
 * Exception levée lorsqu'il est impossible de créer un hash d'une URL. A lieu en cas de collision du hash MD5, hash
 * SHA-256
 */
public class ImpossibleToShortenException extends RuntimeException {

    public ImpossibleToShortenException() {
        super(Constants.CANNOT_SHORTEN_ERROR_MESSAGE);
    }
}
