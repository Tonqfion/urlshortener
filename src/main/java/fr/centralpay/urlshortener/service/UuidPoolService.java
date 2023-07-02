package fr.centralpay.urlshortener.service;


import fr.centralpay.urlshortener.exception.ImpossibleToShortenException;
import fr.centralpay.urlshortener.model.entity.UrlEntity;
import fr.centralpay.urlshortener.model.entity.UuidPoolEntity;
import fr.centralpay.urlshortener.repository.UrlEntityRepository;
import fr.centralpay.urlshortener.repository.UuidPoolRepository;
import fr.centralpay.urlshortener.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant la logique métier
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UuidPoolService {

    @Value("${fr.centralpay.urlshortener.amount-uuid-to-store}")
    private int amountUuidToStore;

    @Value("${fr.centralpay.urlshortener.minimum-uuid-before-filling}")
    private int minimumUuidBeforeFilling;

    private final UuidPoolRepository uuidPoolRepository;

    private final UrlEntityRepository urlEntityRepository;

    /**
     * Méthode permettant de récupérer la première {@link UuidPoolEntity} de la base par ordre alpha numérique, de la
     * supprimer de la base et de retourner son contenu
     *
     * @return la chaîne aléatoire contenue dans l'entité {@link UuidPoolEntity} récupérée et supprimée
     */
    public String getRandomString() {
        var optionalUuidPoolEntity = uuidPoolRepository.findFirstByOrderByRandomStringAsc();
        if (optionalUuidPoolEntity.isEmpty()) {
            throw new ImpossibleToShortenException();
        }
        var uuidPoolEntity = optionalUuidPoolEntity.get();
        uuidPoolRepository.delete(uuidPoolEntity);
        return uuidPoolEntity.getRandomString();
    }

    /**
     * Méthode permettant de créer de nouvelles chaîne aléatoires à ajouter dans la table UUID Pool et qui serviront en
     * cas de collisions successives lors de la création d'un hash en MD5 puis SHA256. Exécutée au lancement de l'appli
     * et à des intervalles réguliers lorsqu'elle est appelée par le service {@link ScheduleTaskService}. Le nombre de
     * UUID Pool minimum restants en base avant mise à jour, et le nombre de UUID maximum sont fixés dans la
     * configuration
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkAndFeedRandomStringPool() {
        List<UuidPoolEntity> listAllUuidPoolEntity = uuidPoolRepository.findAll();
        if (CollectionUtils.isEmpty(listAllUuidPoolEntity) || listAllUuidPoolEntity.size() < minimumUuidBeforeFilling) {
            log.info("Remplissage de la table UUID Pool");
            List<UuidPoolEntity> listNewUuidPoolEntity = getPotentialNewUuidPoolEntities(listAllUuidPoolEntity);
            purgerListPotentialNewUuid(listNewUuidPoolEntity);
            uuidPoolRepository.saveAll(listNewUuidPoolEntity);
            log.info("{} chaînes aléatoires ajoutées dans la table UUID Pool", listNewUuidPoolEntity.size());
        } else {
            log.info("Pool de chaîne suffisant, mise à jour de la table UUID Pool ignorée");
        }
    }

    /**
     * Méthode qui va créer un nombre de chaînes aléatoires à préparer pour l'insertion en base. Cependant, afin
     * d'éviter les doublons, on ne va générer que des nombres dont on s'assure qu'ils ne sont pas dans la table UUID
     * Pool
     *
     * @param listAllUuidPoolEntity liste des {@link UuidPoolEntity} en base
     * @return une liste de potentiels {@link UuidPoolEntity} à insérer en base
     */
    private List<UuidPoolEntity> getPotentialNewUuidPoolEntities(List<UuidPoolEntity> listAllUuidPoolEntity) {
        List<String> listUuidInPool = listAllUuidPoolEntity.stream().map(UuidPoolEntity::getRandomString).toList();
        List<UuidPoolEntity> listNewUuidPoolEntity = new ArrayList<>();
        int nbrUuidToSave = amountUuidToStore - listAllUuidPoolEntity.size();
        UuidPoolEntity uuidPoolEntity;
        int i = 0;
        while (i < nbrUuidToSave) {
            var uuidShortString = Utils.getShortRandomUuid();
            if (!listUuidInPool.contains(uuidShortString)) {
                uuidPoolEntity = new UuidPoolEntity();
                uuidPoolEntity.setRandomString(uuidShortString);
                listNewUuidPoolEntity.add(uuidPoolEntity);
                i++;
            }
        }
        return listNewUuidPoolEntity;
    }

    /**
     * Méthode qui va supprimer de la liste de potentiels nouveaux UUID raccourcis à insérer dans le pool ceux déjà
     * utilisés comme id de {@link UrlEntity}
     *
     * @param listPotentialNewUuid la liste de potentiels nouveaux générée précédemment {@link UuidPoolEntity}
     */
    private void purgerListPotentialNewUuid(List<UuidPoolEntity> listPotentialNewUuid) {
        var listNewUuidAlreadyPresentInUrlEntity = urlEntityRepository.findAllById(listPotentialNewUuid.stream().map(UuidPoolEntity::getRandomString).collect(Collectors.toList())).stream().map(UrlEntity::getHashId).toList();
        if (!CollectionUtils.isEmpty(listNewUuidAlreadyPresentInUrlEntity)) {
            listPotentialNewUuid.removeIf(evalUuidEntity -> listNewUuidAlreadyPresentInUrlEntity.contains(evalUuidEntity.getRandomString()));
        }
    }
}
