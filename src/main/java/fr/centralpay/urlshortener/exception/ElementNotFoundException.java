package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.util.Constants;

/**
 * Exception levée lorsqu'un objet n'est pas retrouvé en base
 */
public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException() {
        super(Constants.ELEMENT_NOT_FOUND_ERROR_MESSAGE);
    }
}
