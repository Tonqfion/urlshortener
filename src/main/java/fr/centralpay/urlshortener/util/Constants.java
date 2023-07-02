package fr.centralpay.urlshortener.util;

/**
 * Classe contenant les constantes utilisées par l'application
 */
public class Constants {

    public static final String INVALID_URL_ERROR_MESSAGE = "URL non valide";
    public static final String CANNOT_SHORTEN_ERROR_MESSAGE = "Impossible de réduire l'URL actuellement";
    public static final String TECHNICAL_EXCEPTION_ERROR_MESSAGE = "Erreur technique, veuillez contacter le support";
    public static final String ELEMENT_NOT_FOUND_ERROR_MESSAGE = "Aucune URL associée à cette adresse courte";
    public static final String SLASH_STRING = "/";
    public static final String REST_MAPPING = "rest";
    public static final String GRAPHQL_MAPPING = "graphql";

    private Constants() {
        // Constructeur privé
    }
}
