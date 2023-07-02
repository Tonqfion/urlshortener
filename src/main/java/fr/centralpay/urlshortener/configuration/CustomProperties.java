package fr.centralpay.urlshortener.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Classe permettant de décrire les properties custom dans les fichiers *.properties
 */
@Configuration
@ConfigurationProperties(prefix = "fr.centralpay.urlshortener")
@Setter
@Getter
public class CustomProperties {

    /**
     * Activation des tâches programmées
     */
    private boolean scheduledTaskEnabled;

    /**
     * Expression CRON décrivant la régularité à laquelle la table UUID Pool est vérifiée pour mise à jour
     */
    private String autoUpdateUuidCron;

    /**
     * Expression CRON décrivant la régularité à laquelle la table URL est purgée
     */
    private String removeOldUrlCron;

    /**
     * Nombre maximum de uuid à stocker dans la table uuid pool
     */
    private int amountUuidToStore;

    /**
     * Nombre minimum à atteindre avant de remplir la table uuid pool
     */
    private int minimumUuidBeforeFilling;

    /**
     * Nombre de jour entre la date d'exécution de la méthode de purge et le dernier accès (visite ou demande) d'une URL
     * avant sa suppression
     */
    private int urlDaysTtl;

}
