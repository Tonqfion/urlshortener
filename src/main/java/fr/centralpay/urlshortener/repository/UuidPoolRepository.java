package fr.centralpay.urlshortener.repository;

import fr.centralpay.urlshortener.model.entity.UuidPoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Composant d'accès à la table réprésentant par {@link UuidPoolEntity}
 */
@Repository
public interface UuidPoolRepository extends JpaRepository<UuidPoolEntity, String> {
    Optional<UuidPoolEntity> findFirstByOrderByRandomStringAsc();
}
