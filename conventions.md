# Conventions de mapping des clefs JNDI #

**jndi-resources** propose d'utiliser certaines conventions pour les clefs JNDI utilisées.
Ces liens successifs permettent le maximum de souplesse lors du déploiement.
Il est possible de choisir tardivement si une ressource doit être
[locale ou globale](architecture.md), si elle doit être partagée avec une autre application, etc.

L'idée principale est de mapper les clefs `java:comp/env/...` sur des clefs `java:<idapp>/...`.

Pour vous rafraîchir la mémoire, consultez le [rappel sur JNDI](rappelJNDI.md) pour
comprendre la différence entre les branches java:comp/env, java: et /.

## Utilisation d'un annuaire JNDI local au serveur d'applications ##

| **Clef** | **Valeur** | **Description** |
|:---------|:-----------|:----------------|
| `java:comp/env/`_<Ma clef>_ | `java:`

&lt;idapp&gt;

_`/`_<Ma clef>| Mapping des clefs locales aux composants vers des clefs globales JVM préfixées par un identifiant. |
| `java:`

&lt;idapp&gt;

_`/`_<Ma clef>| La ressource sauf pour les files JMS. | La ressource est directement publiée ici, sauf pour les clefs JMS. |
| `java:`

&lt;idapp&gt;

_`/jms/`_<Ma clef>| `/`

&lt;idapp&gt;

_`/jms/`_<Ma clef>| Les _queues_ et _topics_ JMS servent à plusieurs serveurs et doivent alors être publiés globalement. |
| `/`

&lt;idapp&gt;

_`/jms/`_<Ma clef>| La ressource JMS. | La ressource JMS visible par tous. |

Par exemple, pour la clef `java:comp/env/jdbc/MaDatabase` d'une application
d'identité `MonApp`, le chemin suivit est le suivant :

`java:comp/env/jdbc/MaDatabase` => `java:MonApp/jdbc/MaDatabase` => La ressource.

Pour la clef `java:comp/env/jms/MaQueue` le chemin est le suivant :

`java:comp/env/jms/MaQueue` => `java:MonApp/jms/MaQueue` => `/MonApp/jms/MaQueue` => La ressource publiée par la file JMS.

## Utilisation d'un annuaire JNDI pour publier les ressources ##

| **Clef** | **Valeur** | **Description** |
|:---------|:-----------|:----------------|
| `java:comp/env/`_<Ma clef>_ | Sans objet | Les composants ne sont pas publiés sur ce serveur. |
| `/`

&lt;idapp&gt;

_`/`_<Ma clef>| La ressource. | Toute les ressources sont publiques et visibles par tous.|

Pour la clef `java:comp/env/jdbc/MaDatabase` d'une application d'identité `MonApp`,
cliente de l'annuaire, le chemin suivi est le suivant :

`java:comp/env/jdbc/MaDatabase` => `java:MonApp/jdbc/MaDatabase` => `/MonApp/jdbc/MaDataBase` [_sur l'annuaire_] => La ressource [_sur l'annuaire_]

## Utilisation d'un annuaire JNDI distant ##
| **Clef** | **Valeur** | **Description** |
|:---------|:-----------|:----------------|
| `java:comp/env/`_<Ma clef>_ | `java:`

&lt;idapp&gt;

_`/`_<Ma clef>| Mapping des clefs locales aux composants vers une clef globale JVM. |
| `java:`

&lt;idapp&gt;

_`/`_<Ma clef>| `/`

&lt;idapp&gt;

_`/`_<Ma clef>| Les ressources sont toujours liées à une clef globale. |

Pour la clef `java:comp/env/jdbc/MaDatabase` d'une application d'identité `MonApp`, cliente de l'annuaire, le chemin suivit est le suivant :

`java:comp/env/jdbc/MaDatabase` [_local_] => `java:MonApp/jdbc/MaDatabase` [_local_]=> `/MonApp/jdbc/MaDataBase` [_distant_] => La ressource


## Publication de queues JMS ##
| **Clef** | **Valeur** | **Description** |
|:---------|:-----------|:----------------|
| `/`

&lt;idapp&gt;

_`/jms/`_<Ma clef>| La ressource JMS. | La ressource visible par tous.|


**jndi-resource** ne publie des ressources que sous le scope `java:` ou `/`.
Vous pouvez alors suivre les conventions que vous souhaitez pour mapper les clefs `java:comp/env`.

Précédent : [Les familles de ressources](familles.md)
Suite: [Lignes de commandes](ligneDeCommandes.md)