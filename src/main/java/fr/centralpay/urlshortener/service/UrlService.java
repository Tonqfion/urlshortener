package fr.centralpay.urlshortener.service;

import fr.centralpay.urlshortener.exception.ElementNotFoundException;
import fr.centralpay.urlshortener.model.dto.UrlResponseDTO;
import fr.centralpay.urlshortener.model.entity.UrlEntity;
import fr.centralpay.urlshortener.repository.UrlEntityRepository;
import fr.centralpay.urlshortener.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service gérant la logique métier en lien avec les URL
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final UrlEntityRepository urlEntityRepository;
    private final UuidPoolService uuidPoolService;

    private final QRCodeService qrCodeService;

    /**
     * Méthode qui va retourner une réponse contentant l'URL raccourcie depuis une URL d'origine
     *
     * @param longUrl l'URL longue contenu dans la requête
     * @return une {@link UrlResponseDTO} contenant l'URL courte et la chaîne représentant le QRCode généré
     */
    public UrlResponseDTO generateShortUrl(String longUrl) {
        var urlEntity = createNewOrFindUrlEntity(longUrl);
        return new UrlResponseDTO(urlEntity);
    }

    /**
     * Méthode qui, à partir d'une URL longue, va générer ou retrouver une {@link UrlEntity} et la sauvegarder ou la
     * mettre à jour en base
     *
     * @param longUrl l'URL d'origine
     * @return l'{@link UrlEntity} sauvegardée ou mise à jour
     */
    public UrlEntity createNewOrFindUrlEntity(String longUrl) {
        var urlEntity = createNewOrFindExistingUrlEntityByDigest(Utils.getMD5Digest(), longUrl);
        if (urlEntity == null) {
            urlEntity = createNewOrFindExistingUrlEntityByDigest(Utils.getSHA256Digest(), longUrl);
        }
        if (urlEntity == null) {
            urlEntity = createUrlEntityFromUuidPool(longUrl);
        }
        urlEntityRepository.save(urlEntity);
        return urlEntity;
    }

    /**
     * Méthode qui à partir d'une URL longue et d'un {@link MessageDigest} va générer ou retrouver une
     * {@link UrlEntity}
     *
     * @param digest  le {@link MessageDigest} utilisé avec un algorithme spécifique
     * @param longUrl l'URL d'origine
     * @return l'{@link UrlEntity} retrouvée ou instanciée
     */
    private UrlEntity createNewOrFindExistingUrlEntityByDigest(MessageDigest digest, String longUrl) {
        log.info("Hashing {} de l'URL", digest.getAlgorithm());
        var hash = StringUtils.EMPTY;
        hash = Utils.hashAndShortenString(digest, longUrl);
        log.info("Vérification de la présence en base du hash...");
        Optional<UrlEntity> optExistingEntity = urlEntityRepository.findById(hash);
        if (optExistingEntity.isPresent()) {
            log.warn("Collision détectée, vérification de l'URL originale");
            var matchingEntity = optExistingEntity.get();
            if (StringUtils.equals(matchingEntity.getCompleteUrl(), longUrl)) {
                log.info("URL déjà présente en base, pas de nécessité de créer une nouvelle entrée, incrémentation du compteur de demande et mise à jour de la date de dernière requête");
                matchingEntity.setRequests(matchingEntity.getRequests() + 1);
                matchingEntity.setLastQueried(LocalDateTime.now());
                return matchingEntity;
            } else {
                log.info("URL différente, changement du process de  génération du hash");
                return null;
            }
        }
        log.info("Hash absent, enregistrement de l'entrée");
        var newUrlEntity = new UrlEntity();
        newUrlEntity.setCompleteUrl(longUrl);
        newUrlEntity.setQrcodeImageString(qrCodeService.getQrImageAsByteArray(longUrl));
        newUrlEntity.setHashId(hash);
        return newUrlEntity;
    }

    /**
     * Méthode qui, à partir d'une URL longue va créer et retourner une {@link UrlEntity}
     *
     * @param longUrl l'URL d'origine
     * @return l'{@link UrlEntity} instanciée
     */
    private UrlEntity createUrlEntityFromUuidPool(String longUrl) {
        log.info("Récupération d'une châine aléatoire pour l'identifiant de l'entrée et enregistrement");
        var randomString = uuidPoolService.getRandomString();
        var newUrlEntity = new UrlEntity();
        newUrlEntity.setCompleteUrl(longUrl);
        newUrlEntity.setHashId(randomString);
        newUrlEntity.setQrcodeImageString(qrCodeService.getQrImageAsByteArray(longUrl));
        return newUrlEntity;
    }

    /**
     * Méthode qui à partir d'un hash va retrouver une {@link UrlEntity} si cette dernière existe, la mettre à jour et
     * renvoyer l'URL longue stockée
     *
     * @param hash le hash reçu de la requête
     * @return l'URL longue associée au hash passé en paramètre
     */
    public String retrieveOriginalUrl(String hash) {
        log.info("Recherche de l'URL correspondant au hash");
        Optional<UrlEntity> optUrlEntity = urlEntityRepository.findById(hash);
        if (optUrlEntity.isEmpty()) {
            log.error("Aucune entrée trouvée");
            throw new ElementNotFoundException();
        }
        log.info("URL retrouvée, mise à jour du compteur de visites et de la date de dernière requête");
        var urlEntity = optUrlEntity.get();
        urlEntity.setVisits(urlEntity.getVisits() + 1);
        urlEntity.setLastQueried(LocalDateTime.now());
        urlEntityRepository.save(urlEntity);
        return urlEntity.getCompleteUrl();
    }

    /**
     * Méthode appelée par la tâche programmée de purge pour supprimer les {@link UrlEntity} stockés en base
     *
     * @param urlDaysTtl le nombre de jour d'ancienneté limite avant suppression. Fixé dans la configuration
     */
    public void removeOldUrlEntity(Integer urlDaysTtl) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        var expiryDate = LocalDateTime.now().minusDays(urlDaysTtl);
        log.info("Suppression des URL non consultées depuis le {}", formatter.format(expiryDate));
        var urlEntitiesRemoved = urlEntityRepository.deleteByDateCreatedBefore(expiryDate);
        log.info("{} URL supprimée(s)", urlEntitiesRemoved);
    }

}
