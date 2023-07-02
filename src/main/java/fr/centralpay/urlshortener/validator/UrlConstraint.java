package fr.centralpay.urlshortener.validator;

import fr.centralpay.urlshortener.util.Constants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Interface permettant la création d'une annotation custom à appliquer au paramètre de la méthode post du controller
 */
@Documented
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlConstraint {
    String message() default Constants.INVALID_URL_ERROR_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
