package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.util.Constants;

public class GeneralTechnicalException extends RuntimeException{

    public GeneralTechnicalException() {
        super(Constants.TECHNICAL_EXCEPTION_ERROR_MESSAGE);
    }
}
