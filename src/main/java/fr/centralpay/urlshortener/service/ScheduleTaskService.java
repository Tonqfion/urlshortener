package fr.centralpay.urlshortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(value = "fr.centralpay.urlshortener.scheduled-task-enabled", havingValue = "true")
@RequiredArgsConstructor
public class ScheduleTaskService {

    @Value("${fr.centralpay.urlshortener.url-days-ttl}")
    private Integer urlDaysTtl;
    private final UuidPoolService uuidPoolService;
    private final UrlService urlService;

    @Scheduled(cron = "${fr.centralpay.urlshortener.auto-update-uuuid-cron}")
    public void scheduledUuidPoolUpdate() {
        log.info("Mise à jour automatique de la table UUID Pool");
        uuidPoolService.checkAndFeedRandomStringPool();
        log.info("Fin de la tâche programmée de MAJ de la table uuid pool");
    }

    @Scheduled(cron = "${fr.centralpay.urlshortener.remove-old-url-cron}")
    public void scheduledOldUrlEntitiesDeletion() {
        log.info("Début de la tâche programmée de purge de la table URL");
        urlService.removeOldUrlEntity(urlDaysTtl);
        log.info("Fin de la tâche programmée de purge");
    }
}
