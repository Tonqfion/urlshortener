package fr.centralpay.urlshortener.repository;

import fr.centralpay.urlshortener.model.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface UrlEntityRepository extends JpaRepository<UrlEntity, String> {

    @Transactional
    long deleteByDateCreatedBefore(LocalDateTime dateTime);
}
