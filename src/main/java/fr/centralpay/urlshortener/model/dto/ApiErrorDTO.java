package fr.centralpay.urlshortener.model.dto;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Record représentant le corps des réponses lors de l'interception d'une exception
 *
 * @param date
 * @param requestStatus
 * @param errorMessage
 */
public record ApiErrorDTO(LocalDateTime date, HttpStatus requestStatus, String errorMessage) {

    public ApiErrorDTO(@NonNull RuntimeException exception, @NonNull HttpStatus requestStatus) {
        this(LocalDateTime.now(), requestStatus, exception.getMessage());
    }

    public ApiErrorDTO(@NonNull String errorMessage, @NonNull HttpStatus requestStatus) {
        this(LocalDateTime.now(), requestStatus, errorMessage);
    }
}
