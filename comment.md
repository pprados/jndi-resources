# Comment faire ? #

Les projets doivent enrichir la description des ressources dont ils ont besoin,
permettant une génération des différents fichiers de configuration des serveurs
d'applications. Ainsi, il sera possible d'installer les composants partout, sans
exprimer autre-chose que des besoins en terme de ressources.

Par exemple, si un composant désire une source de données de type Oracle, il
l'indique dans un [fichier XML](jndiResourcesXML.md) spécifique, et c'est tout. Les
utilitaires se chargent d'installer tous les fichiers, les drivers, etc. pour
permettre au composant d'être accueilli dans tous les serveurs d'applications.

Pour cela, l'application doit respecter rigoureusement les
[spécifications](http://java.sun.com/j2ee/j2ee-1_4-fr-spec.pdf) JEE pour la gestion
des paramètres de déploiement. Il faut utiliser systématiquement les ressources JNDI
pour tous les
[paramètres de déploiement](http://www.prados.fr/Langage/Java/Parametres/Les%20parametres%20en%20java.pdf).
Consultez le petit [rappel sur JNDI](rappelJNDI.md).
Si votre application ne respecte pas complètement les spécifications JEE pour
les paramètres de déploiement, consultez le [guide de migration](guideMigration.md).

Pour mémoire, le tableau suivant reprend les conventions JEE.

| **Clef JNDI** | **Type** | **Exemple** |
|:--------------|:---------|:------------|
| [java:comp/env/ejb/\*](EJB.md) | `javax.ejb.EJBObject` | Les EJBs s'il y en a. |
| [java:comp/env/jdbc/\*](jdbc.md) | `javax.sql.DataSource` | Une source de données SQL avec nom d'utilisateur et mot de passe. |
| [java:comp/env/jms/\*](jms.md) | `javax.jms.ConnectionFactory`, `javax.jms.XAConnectionFactory`, `javax.jms.Queue`, `javax.jms.Topic` | Une source de file JMS avec nom d'utilisateur et mot de passe. |
| [java:comp/env/mail/\*](mail.md) | `javax.mail.Session`, `javax.mail.Address`, `javax.mail.internet.InternetAddress` | Un serveur SMTP, IMAP4 ou POP3 ; Une adresse courriel.|
| [java:comp/env/url/\*](url.md) | `java.net.URL` | Un répertoire partagé ; Une branche FTP ; Une requête de PING vers un serveur HTTP.|
| [java:comp/env/services/\*](url.md) | `javax.xml.ws.Service` | Une référence à un service Web.|
| `java:comp/env/eis/*` | `javax.resource.cci.ConnectionFactory`, `javax.resource.Referenceable` | Les ressources exotiques JCA|
| `java:comp/UserTransaction` | `javax.transaction.UserTransaction` | Clef spéciale permettant d'avoir un accès au contexte transactionnel.|

D'autres clefs sont parfois utiles, même s'il n'existe pas de conventions pour cela.
| **Clef JNDI** | **Type** | **Exemple** |
|:--------------|:---------|:------------|
| [java:comp/env/jndi/\*](jndi.md) | `javax.naming.Context` | Pour lier d'autres annuaires JNDI tel que LDAP ou un annuaire JNDI distant par exemple.|
| [java:comp/env/host/\*](host.md) | `java.net.InetAddress` | Le nom DNS d'une machine ou d'une grappe (plusieurs IP pour le même nom), ou bien directement l'adresse IP mais ce n'est pas conseillé.|
| `java:comp/env/etc/*` | `java.lang.Object` | Autres ressources non prévues pour le moment dans ces spécifications.|

Après avoir déclaré les ressources dans le fichier `web.xml` ou `ejb-jar.xml`, il
faut ensuite décrire les exigences sur les ressources dans un fichier
[jndi-resources.xml](jndiResourcesXML.md) et le placer dans le répertoire `META-INF` du
composant ou bien en dehors de l'archive.

Un [premier utilitaire](ligneDeCommandes.md) permet alors, à partir de
[modèles de conversions](transformations.md), de générer tous les fichiers de
paramètres pour [différents serveurs d'applications](transformations.md), permettant
la publication de ressources répondant aux exigences de l'application. Ces
fichiers sont [partiellement](variables.md) valorisés, car à cette étape, il n'est pas
encore nécessaire d'indiquer les noms des machines et autres paramètres de déploiement.
Seuls les modèles de fichiers sont nécessaires.

Tous les modèles de déploiement peuvent alors être distribués sur différentes plate-formes d'accueil.

Un [deuxième utilitaire](ligneDeCommandes.md) se charge d'exploiter ces modèles de
déploiement pour une installation effective sur un ou plusieurs serveurs
d'applications cibles, pas nécessairement de la même marque ou de la même
version. Des fichiers de [propriétés](variables.md) permettent alors d'indiquer
les éléments spécifiques à la plate-forme d'accueil (les adresses des bases de
données, les mots de passe, etc).

Si nécessaire, ces deux étapes peuvent être regroupées
[en une seule](ligneDeCommandes.md), afin de partir du [fichier](jndiResourcesXML.md)
des exigences de ressources à une installation directe sur les serveurs d'applications.

Précédant : [À quoi cela sert ?](aQuoiCelaSert.md)
Suite : [Installation](installation.md)