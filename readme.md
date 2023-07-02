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

# Présentation

Application permettant de raccourcir une URL. Comprend une API REST et un endpoint GraphQL.

## Stack technique

Java 17\
Spring Boot 3.1.1\
MariaDB

## Dépendances Java / Spring

Client MariaDB\
Apache Language Commons3\
GraphQL pour Spring boot\
GraphQL DateTime (Scalar GraphQL pour Java Temporals)\
Google Zxing pour création de QRCode\
Lombok

# Fonctionnalités

## Base de données

La base de données comprend deux tables simples. Une représentant les URL enregistrées avec de multiples informations,
l'autre représentant un pool de chaînes de caractères pouvant servir d'identifiant à la première table dans le cas de
collision lors du hashing des URL longues (cf plus bas).

## Création d'une URL courte

### Fonctionnement général

L'application permet, depuis un endpoint utilisant le verbe POST de soumettre une requête au
format JSON.

POST : http://adresse_du_serveur/rest

```json
{
  "url": "adresse à insérer"
}
```

Après validation, l'URL sera hashée puis ce hash raccourci et enregistré dans une base de données et renvoyé à l'
appelant pour que ce
dernier puisse partager l'adresse courte. L'adresse concatène l'adresse du serveur, le chemin des requêtes associées au
controller (/rest) et le hash afin de récupérer l'adresse complète.

A noter que la réponse contient également une chaîne de caractère contenant les bytes d'une image encodée en Base64 sous
forme de chaîne de caractères.

```json
{
  "shortUrl": "adresse_du_serveur/rest/hash",
  "qrcodeImageEncoded": "encodage au format Base64 des bytes représentant le QR Code généré depuis l'URL originale"
}
```

Lors de l'enregistrement de la donnée, les propriétés suivantes sont alimentées en base :

* le hash de l'URL, correspondant à l'ID de l'enregistrement
* l'URL complète
* la date de création (initialisé au timestamp actuel)
* le nombre de fois où cette URL a été raccouçie (initialisée à 1)
* le nombre de fois où elle a été consultée (initialisée à 0)
* la date de dernière demande (requête de création d'une URL courte, ou consultation de l'URL courte associée à l'URL
  longue, initialisée à la date du jour)

### Création du hash

Le hash d'une URL est évalué de la manière suivante.
D'abord, on hash selon un premier algorithme. Voici les étapes du hashing d'une URL

1. On récupère les bytes de l'URL originale au format UTF-8
2. On hash ces bytes via l'algorithme déterminé
2. On transforme les bytes récupérés en un nombre entier
3. On transforme ce nombre en une chaîne hexadécimale
4. On ne conserve que les 8 premiers caractères de la chaîne
5. On supprime les éventuels espaces en début et fin de chaîne

Le premier algo utilisé est le MD5. Une fois généré, on recherche si ce hash est présent dans la base de données (le
hash correspond à l'id des enregistrements). Si ce n'est pas le cas, parfait : on enregistre l'entité avec les valeurs
décrites plus haut.

En revanche, si le hash existe déjà, on va vérifier l'URL longue associée à l'enregistrement présent en base. Si celle
que l'appelant demande à raccourcir est égale à celle présente en base, alors on ne crée pas un nouvel enregistrement.
On met simplement à jour celui déjà présent en incrémentant le compteur de demande et en mettant à jour la date de
dernière consultation, puis on renvoie les informations à l'appelant depuis cet enregistrement pré-existant.

Maintenant, si l'URL longue de l'entité retrouvée en base est différente de celle requêtée, on est dans le cas d'une
collision de hash. Cela signifie que que deux URL différentes ont généré le même hash. C'est statistiquement presque
impossible, même sur une chaîne de 8 caractères, mais cela peut arriver. Dans ce cas, on va effectuer les mêmes étapes
mais avec un hash utilisant l'algorithme SHA-256.

Si il y a également collision entre deux URLs différentes du hash SHA-256, on va récupérer une chaîne de caractère
aléatoire, constituée des 8 premiers caractères d'un random UUID présents dans un pool de chaînes de caractères stockées
dans la base de données. Une fois cette chaîne attribuée à un enregistrement, elle sera supprimé du pool.

### Pool de chaînes de caractères

Le pool de UUID est vérifié à chaque lancement d'application, et à des intervales réguliers selon la configuration de
l'appli via les tâches programmées de Spring.
Si le nombre d'entrée restant dans le pool est inférieur à un nombre donné dans la configuration, alors le on va essayer
de remplir le pool à nouveau en prenant garde à ne pas créé des chaînes déjà présente dans le pool lui même, et en
faisant également attention à ne pas générer une chaîne aléatoire déjà attribuée dans la table principale (à réfléchir
différemment dans le cas d'une application massive).

### QRCode

A noter que l'API génère un QRCode enregistré en base sous forme de chaîne de caractère représentant un encodage en
base64 des bytes contenant l'image du QRCode. Ce QRCode contient l'adresse originale, et non l'adresse courte générée,
cette dernière étant dépendante du serveur où est hebergée l'appli consultée, et pourrait donc générer des liens morts
en consultant le QRCode si elle était sauvegardée en dure. Une évolution pourrait être envisagée

## Consultation d'une URL courte

Un second endpont utilisant le verbe GET et prenant en paramètre une chaîne de caractère permet d'être automatiquement
redirigé vers l'URL originale si le paramètre correspond a un hash associé à une URL complète en base de données.

Le retour du endpoint est le suivant :

```json
{
  "shortUrl": "adresse_du_serveur/rest/hash",
  "qrcodeImageEncoded": "encodage au format Base64 des bytes représentant le QR Code généré depuis l'URL originale"
}
```

On pourrait imaginer un retour plus complet contenant le nombre de visites ou de demandes

## Gestion des erreurs

Les erreurs sont prises en charge par une classe de gestion d'erreurs qui va, selon l'exception levée par l'API, générer
une réponse HTTP adaptée. Par exemple, une exception de type ConstraintViolationException génèrera automatiquement une
erreur de type 400 avec un message générique.

## Purge

A l'aide d'une tâche programmée Spring, une purge sur les URL en bdd sera effectuée, permettant selon différents
paramètres (régularité, nombre de jours d'ancienneté) de supprimer des URLs non consultées ou requêtées lors des X
derniers jours.

## GraphQL

Par curiosité, et parce que CentralPay semble utiliser du graphQL, j'ai créé un endpoint permettant

1. D'afficher toutes URLs en base
2. D'afficher une unique URL
3. De créer une URL

Consulter le fichier resources/graphql/schema.graphqls pour retrouver les requêtes et mutations disponibles

# Limites de l'application

## Scalabilité de la base de données

Utiliser MariaDB pour stocker des informations aussi simples, sans relations, ne semble pas idéal. Une ou deux base
REDIS auraient pu faire l'affaire. Cependant, j'avais une base distante MariaDB à disposition via un hébergeur, ce qui
permet de plus simplement brancher l'application sur cette dernière pour présentation.

De même, l'évaluation des uuid à créer ne semble pas idéale. Dans le cadre d'un exercice, pas de soucis, mais si on
s'imagine un nombre de données bien plus importante, effectuer des requêtes comparant une liste d'id à créer avec ceux
potentiellement présents dans une gigantesque base pour éviter des collisions peut poser des problèmes importants.

Une solution de remplacement serait peut être de simplement créer les UUID à la volée, sans les persister dans un pool,
et s'il est présent de simplement en générer un nouveau et tenter de sauvegarder l'entité avec ce hash comme ID.


