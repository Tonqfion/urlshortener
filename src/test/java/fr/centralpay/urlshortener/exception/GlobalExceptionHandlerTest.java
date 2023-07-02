package fr.centralpay.urlshortener.exception;

import fr.centralpay.urlshortener.model.dto.ApiErrorDTO;
import fr.centralpay.urlshortener.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler tested;

    @Test
    void testHandleImpossibleToShortenException() {
        // Given
        ImpossibleToShortenException givenException = new ImpossibleToShortenException();

        // When
        ApiErrorDTO result = (ApiErrorDTO) tested.handleImpossibleToShortenException(givenException).getBody();

        //
        Assertions.assertAll("handleImpossibleToShortenException test",
                () -> {
                    assert result != null;
                    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.requestStatus());
                },
                () -> {
                    assert result != null;
                    Assertions.assertEquals(Constants.CANNOT_SHORTEN_ERROR_MESSAGE, result.errorMessage());
                });

    }

    @Test
    void testHandleGeneralTechnicalException() {
        // Given
        GeneralTechnicalException givenException = new GeneralTechnicalException();

        // When
        ApiErrorDTO result = (ApiErrorDTO) tested.handleGeneralTechnicalException(givenException).getBody();

        //
        Assertions.assertAll("handleGeneralTechnicalException test",
                () -> {
                    assert result != null;
                    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.requestStatus());
                },
                () -> {
                    assert result != null;
                    Assertions.assertEquals(Constants.TECHNICAL_EXCEPTION_ERROR_MESSAGE, result.errorMessage());
                });

    }

    @Test
    void testHandleElementNotFoundException() {
        // Given
        ElementNotFoundException givenException = new ElementNotFoundException();

        // When
        ApiErrorDTO result = (ApiErrorDTO) tested.handleElementNotFoundException(givenException).getBody();

        //
        Assertions.assertAll("handleElementNotFoundException test",
                () -> {
                    assert result != null;
                    Assertions.assertEquals(HttpStatus.NOT_FOUND, result.requestStatus());
                },
                () -> {
                    assert result != null;
                    Assertions.assertEquals(Constants.ELEMENT_NOT_FOUND_ERROR_MESSAGE, result.errorMessage());
                });

    }
}
