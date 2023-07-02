package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.util.Constants;

public class ImpossibleToShortenException extends RuntimeException{

    public ImpossibleToShortenException() {
        super(Constants.CANNOT_SHORTEN_ERROR_MESSAGE);
    }
}
