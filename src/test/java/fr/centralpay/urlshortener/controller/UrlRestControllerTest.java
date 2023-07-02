package fr.centralpay.urlshortener.controller;

import fr.centralpay.urlshortener.exception.ElementNotFoundException;
import fr.centralpay.urlshortener.exception.GeneralTechnicalException;
import fr.centralpay.urlshortener.model.dto.UrlRequestDTO;
import fr.centralpay.urlshortener.model.dto.UrlResponseDTO;
import fr.centralpay.urlshortener.service.UrlService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class UrlRestControllerTest {

    @InjectMocks
    UrlRestController tested;

    @Mock
    UrlService urlService;

    String givenValidatedUrl = "givenValidatedUrl";

    UrlRequestDTO urlRequestDTO;

    @BeforeEach
    void setUp() {
        urlRequestDTO = new UrlRequestDTO(givenValidatedUrl);
    }

    @Test
    void test_postShortUrl_ok() {
        // Given
        UrlResponseDTO givenUrlResponseDto = new UrlResponseDTO(givenValidatedUrl, "qrCodeImage");
        Mockito.when(urlService.generateShortUrl(givenValidatedUrl)).thenReturn(givenUrlResponseDto);

        // When
        ResponseEntity<UrlResponseDTO> result = tested.postShortUrl(urlRequestDTO);

        // Then
        Assertions.assertEquals(givenUrlResponseDto, result.getBody());
    }

    @Test
    void test_postShortUrl_ko() {
        // Given
        Mockito.when(urlService.generateShortUrl(givenValidatedUrl)).thenThrow(GeneralTechnicalException.class);

        // Then
        Assertions.assertThrows(GeneralTechnicalException.class, () -> tested.postShortUrl(urlRequestDTO));
    }

    @Test
    void test_redirect_ok() {
        // Given
        String givenHash = "hash";
        String givenUrl = "url";
        Mockito.when(urlService.retrieveOriginalUrl(givenHash)).thenReturn(givenUrl);

        // When
        ResponseEntity<Void> result = tested.redirect(givenHash);

        // Then
        Assertions.assertEquals(HttpStatus.TEMPORARY_REDIRECT, result.getStatusCode());
        Assertions.assertEquals(givenUrl, Objects.requireNonNull(result.getHeaders().getLocation()).toString());
    }

    @Test
    void test_redirect_notfound_ko() {
        // Given
        String givenHash = "hash";
        Mockito.when(urlService.retrieveOriginalUrl(givenHash)).thenThrow(ElementNotFoundException.class);

        Assertions.assertThrows(ElementNotFoundException.class, () -> tested.redirect(givenHash));
    }
}
