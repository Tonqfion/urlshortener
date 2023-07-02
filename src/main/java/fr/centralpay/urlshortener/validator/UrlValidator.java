package fr.centralpay.urlshortener.validator;

import fr.centralpay.urlshortener.model.dto.UrlRequestDTO;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
public class UrlValidator implements ConstraintValidator<UrlConstraint, UrlRequestDTO> {
    @Override
    public void initialize(UrlConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UrlRequestDTO urlRequestDTO, ConstraintValidatorContext constraintValidatorContext) {
        var url = urlRequestDTO.url();
        if (StringUtils.isEmpty(url) || url.length() > 2000) {
            log.error("Longueur de l'URL non valide");
            return false;
        }
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException ex) {
            log.error("URL mal construite ou syntaxe incorrecte");
            return false;
        }
        log.info("URL valide");
        return true;
    }
}
