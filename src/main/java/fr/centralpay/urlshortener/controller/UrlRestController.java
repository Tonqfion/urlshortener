package fr.centralpay.urlshortener.controller;

import fr.centralpay.urlshortener.model.dto.UrlRequestDTO;
import fr.centralpay.urlshortener.model.dto.UrlResponseDTO;
import fr.centralpay.urlshortener.model.entity.UrlEntity;
import fr.centralpay.urlshortener.service.UrlService;
import fr.centralpay.urlshortener.util.Constants;
import fr.centralpay.urlshortener.validator.UrlConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Controller dédié à l'API Rest
 */
@RestController
@RequestMapping(value = Constants.REST_MAPPING)
@RequiredArgsConstructor
@Validated
public class UrlRestController {

    private final UrlService urlService;

    /**
     * Méthode permettant de créer une {@link UrlEntity} depuis une {@link UrlRequestDTO}
     *
     * @param requestDTO l'objet entrant contenant l'URL longue en chaîne de caractères et validé
     * @return {@link ResponseEntity} avec une {@link UrlResponseDTO} dans le corps
     */
    @PostMapping
    public ResponseEntity<UrlResponseDTO> postShortUrl(@RequestBody @UrlConstraint UrlRequestDTO requestDTO) {
        var urlResponseDTO = urlService.generateShortUrl(requestDTO.url());
        return ResponseEntity.ok(urlResponseDTO);
    }

    /**
     * Méthode permettant d'être redirigé vers une URL longue depuis un hash
     *
     * @param hash l'id de l'entité recherchée
     * @return {@link ResponseEntity} avec un status de redirection temporaire et un renvoi vers l'URL longue si le hash
     * permet de la retrouver
     */
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable(value = "hash") String hash) {
        var longUrl = urlService.retrieveOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(longUrl))
                .header(HttpHeaders.CONNECTION, "close")
                .build();
    }
}
