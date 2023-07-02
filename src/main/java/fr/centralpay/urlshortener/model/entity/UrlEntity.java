package fr.centralpay.urlshortener.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * Réprésentation de l'objet stockée en base
 */
@Entity
@Table(name = "url")
@Getter
@Setter
public class UrlEntity {

    @Id
    @Column(name = "hash_id")
    @Length(min = 1, max = 8)
    private String hashId;

    @Column(name = "complete_url", columnDefinition = "TEXT")
    @NotBlank
    private String completeUrl;

    @Column(name = "qrcode_image_string", columnDefinition = "LONGTEXT")
    @NotNull
    private String qrcodeImageString;

    @Column(name = "date_created")
    @NotNull
    private LocalDateTime dateCreated;

    @Column(name = "last_queried")
    @NotNull
    private LocalDateTime lastQueried;

    @Column(name = "visits")
    @NotNull
    private Integer visits;

    @Column(name = "requests")
    @NotNull
    private Integer requests;

    public UrlEntity() {
        this.dateCreated = LocalDateTime.now();
        this.lastQueried = LocalDateTime.now();
        this.visits = 0;
        this.requests = 1;
    }

}
