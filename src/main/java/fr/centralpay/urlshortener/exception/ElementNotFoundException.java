package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.util.Constants;

public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException() {
        super(Constants.ELEMENT_NOT_FOUND_ERROR_MESSAGE);
    }
}
