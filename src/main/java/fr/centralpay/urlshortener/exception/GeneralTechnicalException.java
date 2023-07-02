package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.util.Constants;

/**
 * Exception technique générale renvoyée lorsqu'une Exception technique spécifique est levée
 */
public class GeneralTechnicalException extends RuntimeException {

    public GeneralTechnicalException() {
        super(Constants.TECHNICAL_EXCEPTION_ERROR_MESSAGE);
    }
}
