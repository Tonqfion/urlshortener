package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.model.dto.ApiErrorDTO;
import fr.centralpay.urlshortener.util.Constants;
import fr.centralpay.urlshortener.util.Utils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Controller Advice permettant de gérer le comportement et d'intercepter des exceptions diverses en renvoyant une
 * réponse HTTP avec une erreur adaptée dans le corps de la réponse
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ImpossibleToShortenException.class)
    public ResponseEntity<Object> handleImpossibleToShortenException(ImpossibleToShortenException impossibleToShortenException) {
        var error = new ApiErrorDTO(impossibleToShortenException, HttpStatus.INTERNAL_SERVER_ERROR);
        return Utils.buildErrorResponse(error);
    }

    @ExceptionHandler(GeneralTechnicalException.class)
    public ResponseEntity<Object> handleGeneralTechnicalException(GeneralTechnicalException generalTechnicalException) {
        var error = new ApiErrorDTO(generalTechnicalException, HttpStatus.INTERNAL_SERVER_ERROR);
        return Utils.buildErrorResponse(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        log.error(constraintViolationException.getMessage(), constraintViolationException);
        var error = new ApiErrorDTO(constraintViolationException, HttpStatus.BAD_REQUEST);
        return Utils.buildErrorResponse(error);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<Object> handleElementNotFoundException(ElementNotFoundException elementNotFoundException) {
        var error = new ApiErrorDTO(elementNotFoundException, HttpStatus.NOT_FOUND);
        return Utils.buildErrorResponse(error);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccessViolation(DataAccessException dataAccessException) {
        log.error(dataAccessException.getMessage(), dataAccessException);
        var error = new ApiErrorDTO(Constants.TECHNICAL_EXCEPTION_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        return Utils.buildErrorResponse(error);
    }

}
