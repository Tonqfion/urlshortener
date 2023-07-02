# Installation de l'application

## Version locale

Clonez le repo, puis installer les dépendances avec maven. Installez Docker Desktop puis, via un terminal, depuis le
dossier dans lequel le repo a été cloné, lancez la commande

```shell
docker-compose up
```

Une image MariaDB sera téléchargée puis lancée dans un container Docker en local avec les tables nécessaires à
l'application initalisée.

Lancez ensuite l'application depuis votre IDE en utilisant le profil "local" afin que le fichier
application-local.properties soit utilisée. L'IDE pourra vous demander d'installer une version 17 de la JDK pour pouvoir
compiler le projet.

Autre solution, vous pouvez lancer l'application après avoir cloné le repo via la commande maven suivante (sur Windows)
depuis la racine du projet.

```shell
mvn spring-boot:run
```

NB : demande d'avoir Maven installé sur la machine et d'avoir comme JDK 17 installée et configurée comme variable
d'environnement

## Version Distante

Si vous n'avez pas Docker ou ne pouvez pas vous en servir, vous pouvez utiliser le fichier
application-distant.properties envoyé par mail et le copier dans le dossier src/main/resources.

Reprenez les étapes pour lancer l'application mais avec le profil "distant" via l'IDE, ou avec la commande

```shell
mvn spring-boot:run -D"spring-boot.run.profiles"=distant
```



