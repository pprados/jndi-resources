# Petit rappel sur JNDI #

Les composants JEE doivent récupérer les objets dépendant du contexte d'exécution, à
l'aide de clefs et d'un annuaire JNDI. Les clefs suivent des conventions, permettant une
organisation des différentes ressources et un partage du serveur d'application.

Chaque composant ne peut, théoriquement, manipuler que des clefs de la branche
`java:comp`. C'est une branche publiant des objets visibles que par le composant.
Plus précisément, les objets sont généralement récupérés de la branche
`java:comp/env` qui est une branche spécifique à chaque thread. Cela correspond à
chaque composant (JAR ou WAR) dans un composant (EAR). Par exemple, plusieurs
archives WAR peuvent être présentes dans le même composant EAR. Chaque WAR peut
avoir des valeurs différentes pour les mêmes clefs de la branche `java:comp/env`.
En pratique, cela n'arrive jamais.

La branche `java:` est une branche spéciale, où les objets ne sont visibles que
par la JVM. Ils ne sont pas publiés à l'extérieur. Cela permet d'avoir des
paramètres locaux à une JVM, sans les exposer.

La branche racine est visible par tous. Les objets présent dans cette branche
peuvent être récupérée par tous code java.

Le tableau suivant, résume ces différents branches.

| **Branche** | **Visibilité** |
|:------------|:----------------|
| / | Visible par tous. |
| java: | Visible par la JVM. |
| java:comp | Visible par chaque composant (EAR, WAR, etc.). |
| java:comp/env | Visible par chaque thread (par chaque sous-composant) |


Pour mémoire, le tableau suivant reprend les conventions JEE.

| **Clef JNDI** | **Type** | **Exemple** |
|:--------------|:---------|:------------|
| `java:comp/env/ejb/*` | `javax.ejb.EJBObject` | Les EJBs s'il y en a. |
| [java:comp/env/jdbc/\*](jdbc.md) | `javax.sql.DataSource` | Une source de données SQL avec nom d'utilisateur et mot de passe. |
| [java:comp/env/jms/\*](jms.md) | `javax.jms.ConnectionFactory`, `javax.jms.XAConnectionFactory`, `javax.jms.Queue`, `javax.jms.Topic` | Une source de file JMS avec nom d'utilisateur et mot de passe. |
| [java:comp/env/mail/\*](mail.md) | `javax.mail.Session`, `javax.mail.Address`, `javax.mail.internet.InternetAddress` | Un serveur SMTP, IMAP4 ou POP3 ; Une adresse courriel.|
| [java:comp/env/url/\*](url.md) | `java.net.URL` | Un répertoire partagé ; Une branche FTP ; Une requête de PING vers un serveur HTTP.|
| [java:comp/env/services/\*](url.md) | `javax.xml.ws.Service` | Une référence à un service Web.|
| `java:comp/env/eis/*` | `javax.resource.cci.ConnectionFactory`, `javax.resource.Referenceable` | Pour les ressources JCA|
| `java:comp/UserTransaction` | `javax.transaction.UserTransaction` | Clef spéciale permettant d'avoir un accès au contexte transactionnel.|

Plusieurs [architectures](architecture.md) sont possible, d'un annuaire localisé
dans chaque serveur d'applications à une grappe d'annuaires centralisés,
utilisés par tous les serveurs d'applications.