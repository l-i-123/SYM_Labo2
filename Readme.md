#### 4.1 Traitement des erreurs

En cas d'absence du serveur ou si une requête retourne un code d'erreur, les réponse sont ignoré par notre première activité. vous trouverez ci-dessous les améliorations que nous avons apporté afin de gérer les deux cas évoqué précédemment.

##### Cas du serveur qui n'est pas connecté

Pour gérer le cas du serveur qui n'est pas connecté on a ajouter un contrôle de sa disponibilité en ajouter les deux lignes ci-dessous afin de contrôler sa présence à l'aide d'une socket

```java
InetAddrese adresss = InetAddress.getByName("sym.iict.ch");
Socket socket = new Socket(adresse.getHostAddress(), 80);
if(socket.isConnected()){
    ......
}
```

##### Cas du serveur qui retourne un code d'erreur

Pour le cas du serveur qui retourne un code d'erreur, le cas à été géré de la manière suivante

```java
int responseCode = urlConnection.getResponseCode();
if (responseCode >= 400 && responseCode <= 499) {
       content.append("Bad request");
}
```

Le code de retour est lu après l'envoie de la requête et permet de savoir si oui ou non la requête c'est bien passé.

#### 4.2 Authentification

On pourrait imaginer une connexion par mot de passe dans laquelle il faudrait s'authentifier avant chaque requête. Si les requêtes sont fréquentes cela pourrait fortement alourdir la communication. Du coup, ce serait préférable de pouvoir conserver cette authentification plus qu'une seule ouverture de connexion et requête. Cependant le HTTP ne propose pas de gestion de la couche "session". Pour le faire, il serait nécessaire d'implémenter ce mécanisme dans la couche applicative. On pourrait imaginer par exemple un système de jeton que l’utilisateur reçoit lors de la première connexion avec authentification, qu'il pourra ensuite présenter dans l'en-tête lors des requêtes futurs. Pour limiter les risques de sécurité on peut donner un temps de "vie" à ce jeton plus ou moins long.

#### 4.3 Threads concurrents

Dans ce cas, nous pensons qu'il pourrait intervenir deux problèmes principaux :

- Il est nécessaire de coordonner ces deux threads, dans le sens que le thread de réception doit savoir la réponse de quelle requête il est en train de traiter. Pour faire cela on peut ajouter des informations dans l'en tête de la requête ou on peut ajouter un mécanisme de communication entre les deux threads.
- Un second problème qui pourrai intervenir serait au niveau des performances. Il ne faut pas que le thread de réception prenne trop de temps pour traiter les données. Si c'est le cas, on peut imaginer que le thread qui envoie les données le fait de manière très régulière et le thread de réception qui n'arrive pas à suivre la cadence. Dans ce cas il y aurait une surcharge du réseau et on n'arriverait pas à traiter toutes les réponses dans un temps satisfaisant.

#### 4.4 Ecriture différée

L'ouverture d'une connexion par transmission différée n'est pas une mauvaise idée mais pourrai devenir difficile à gérer dans le cas ou il y aurai un grand nombre de message car cela implique autant  de tâche asynchrone que de message. Cependant si une réponse du serveur est attendu se serai sûrement la façon de faire la plus adéquat.

Le multiplexage de toutes les connexions vers un même serveur permet de lancer un seul tâche asynchrone s'occupant de gérer les messages les un après les autres. Cette solution permet de limiter drastiquement le nombre de tâche à lancer mais complexifie quelque peut la réception de messages de  retour spécifique à chaque requête.

On pense qu'il serai possible de gérer se cas de figure de la manière suivante:

- Identifier chaque message de manière unique au moment de l'envoie ainsi le serveur peut se servir de cette identifiant lors de sa réponse. Cela requière la définition d'un protocole entre le client et le serveur. 
- Attendre la réponse du serveur entre chaque envoie de message. Cette solution est viable mais le principe de multiplexage perd de son sens et le temps d'envoie de toutes les requêtes sera grandement rallongé.

#### 4.5 Transmission d'objets

a: Il est du coup nécessaire de faire le test des différents valeurs "à la main" dans l'application pour être sûr que tous les champs nécessaires ont été renseigné. L'avantage est que le protocole s'en trouve généralement plus léger et plus flexible. Plus flexible dans le sens que l'on peut laisser certains champs non renseigné et cela nous permet de gérer la présence ou non de certaines données de la manière que l'on désire. Dans le cas d'un protocole qui évole (plusieurs versions) le JSON peut être plus simple à maintenir une retocompatibilité. Comme cette gestion se fait au niveau applicative. Pour le XML il serait nécessaire de garder un modèle de toutes les DTD qui correspondent à chaque version du protocole. Cela deviendrait compliquer à maintenir.

b: Oui cela semble complètement envisageable d'utiliser un mécanisme de type protocole buffer à travers HTTP. Les avantages sont : le stream généré est plus petit que si l'on utilise XML ou JSON. Le parseurs de données est plus rapide, le mécanisme permet de générer un API spécifique pour chaque type que de données que l'on veut transférer. cependant, les protocoles buffers semblent plus compliquer à mettre en place qu'un format XML, dans le sens qu'il faut faire une étape de préparation plus importante pour décrire les structures. Mais cela semble plus simple à utiliser dans un second temps. Cela semble moins répandu que le XML ou le JSON mais le langage de description sur lequel il se base est soutenu par Google.

c: Une des améliorations possible pour l'utilisation sur mobile serai de pouvoir faire des requêtes par tranche d'id. Cela aurai permis de ne pas charger tous les auteurs à afficher en une fois ou de devoir charger id après id au fils de défilement de la liste (ce qui prend du temps) mais de les charger par paquet de 10 ou 20 afin d'avoir un juste milieu

#### 4.6 Transmission compressée 

**Observation des trames avec wireshark :**

**Envoie de 5 objets JSON :**

**non compressé:**

![sym8](/Users/Maxime/Desktop/sym8.png)

![sym1](/Users/Maxime/Desktop/sym1.png)



**compressé:**

![sym2](/Users/Maxime/Desktop/sym2.png)

![sym3](/Users/Maxime/Desktop/sym3.png)

**Envoie de 10 objets JSON :**

**non compressé:**

![sym4](/Users/Maxime/Desktop/sym4.png)

![sym5](/Users/Maxime/Desktop/sym5.png)

**compressé:**

![sym6](/Users/Maxime/Desktop/sym6.png)

![sym7](/Users/Maxime/Desktop/sym7.png)

##### Résumé:

|                   | Non compressé | Compressé | Gain  |
| ----------------- | ------------- | --------- | ----- |
| 5 objets POST     | 2466          | 124       | 19.89 |
| 5 objets Réponse  | 3273          | 592       | 5.53  |
| 10 objets POST    | 3741          | 145       | 25.8  |
| 10 objets Réponse | 4518          | 618       | 7.31  |

