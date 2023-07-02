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

    public String getRandomString() {
        var optionalUuidPoolEntity = uuidPoolRepository.findFirstByOrderByRandomStringDesc();
        if (optionalUuidPoolEntity.isEmpty()) {
            throw new ImpossibleToShortenException();
        }
        var uuidPoolEntity = optionalUuidPoolEntity.get();
        uuidPoolRepository.delete(uuidPoolEntity);
        return uuidPoolEntity.getRandomString();
    }

    /**
     * Méthode permettant de créer de nouvelles chaîne aléatoires à ajouter dans la table UUID Pool et qui serviront en
     * cas de collisions successives lors de la création d'un hash en MD5 ou SHA256
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkAndFeedRandomStringPool() {
        List<UuidPoolEntity> listAllUuidPoolEntity = uuidPoolRepository.findAll();
        if (CollectionUtils.isEmpty(listAllUuidPoolEntity) || listAllUuidPoolEntity.size() < minimumUuidBeforeFilling) {
            log.info("Remplissage de la table UUID Pool");
            List<UuidPoolEntity> listNewUuidPoolEntity = getPotentialNewUuidPoolEntities(listAllUuidPoolEntity);
            var listNewUuidAlreadyPresentInUrlEntity = urlEntityRepository.findAllById(listNewUuidPoolEntity.stream().map(UuidPoolEntity::getRandomString).collect(Collectors.toList())).stream().map(UrlEntity::getHashId).toList();
            if (!CollectionUtils.isEmpty(listNewUuidAlreadyPresentInUrlEntity)) {
                listNewUuidPoolEntity.removeIf(evalUuidEntity -> listNewUuidAlreadyPresentInUrlEntity.contains(evalUuidEntity.getRandomString()));
            }
            uuidPoolRepository.saveAll(listNewUuidPoolEntity);
            log.info("{} chaînes aléatoires ajoutées dans la table UUID Pool", listNewUuidPoolEntity.size());
        } else {
            log.info("Pool de chaîne suffisant, mise à jour de la table UUID Pool ignorée");
        }
    }

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
}
