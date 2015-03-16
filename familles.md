# Familles de ressources #
Le tableau suivant décrit les différentes familles de ressources gérés par l'outil. Il s'agit des types de ressources que peut générer l'outil. Bien entendu, d'autres ressources peuvent être nécessaire. Elles apparaîtront au fur et à mesure des évolutions de l'outil. Vous pouvez également l'[étendre](etendre.md) pour générer vos propres ressources.

| **Famille** | **Description** |
|:------------|:----------------|
| [host/default](host.md) | Déclare un host dans l'arbre JNDI. |
| [jdbc/default](jdbc.md) | Utilise la base de données par défaut du serveur d'applications. |
| [jdbc/hsqldb](jdbc.md) | Utilise une base de données de type [HSQLDB](http://www.hsqldb.org).|
| [jdbc/mysql](jdbc.md) | Utilise une base de données [MySql](http://www.mysql.com). |
| [jdbc/oracle](jdbc.md) | Utilise une base de données [Oracle](http://www.oracle.com). |
| [jms/default](jms.md) | Utilise les serveur JMS par défauts du serveur d'applications.|
| [jms/jboss](jms.md) | Utilise les drivers du serveur JMS du JBoss courant (JBoss MQ ou JBoss Messaging suivant les cas). |
| [jms/jbossmq](jms.md) | Utilise spécifiquement un serveur JMS JBoss MQ. |
| [jms/jboss-messaging](jms.md) | Utilise spécifiquement un serveur JMS JBoss Messaging. |
| [jndi/default](jndi.md) | Propose un lien vers un annuaire JNDI de la même famille que celle du serveur d'application. |
| [jndi/cos](jndi.md) | Propose un lien vers un référentiel d'objets CORBA. |
| [jndi/dns](jndi.md) | Permet un accès à un serveur DNS |
| [jndi/file](jndi.md) | Permet un accès à un répertoire.|
| [jndi/jboss](jndi.md) | Offre la manipulation d'un annuaire JNDI JBoss distant.|
| [jndi/ldap](jndi.md) | Manipule un serveur LDAP |
| [jndi/rmi](jndi.md) | Consulte une référentiel RMI. |
| [mail/default](mail.md) | Une session Javamail.|
| [mail/email](mail.md) | Une adresse email.|
| [url/default](url.md) | Déclare une URL dans l'annuaire JNDI. Utile pour les [EJB](EJB.md) ou [EAR](EAR.md).|
| [link/default](link.md) | Déclare un lien entre clefs JNDI.|
| [jaas/default](jaas.md) | Authentifications par défaut de JBoss.|
| [jaas/intranet](jaas.md) | Authentification Intranet pour JBoss.|
| [jaas/internet](jaas.md) | Authentification Internet pour JBoss.|

Précédent : [Exemple rapide](exemple.md)
Suite : [Les conventions](conventions.md)