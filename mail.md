# Famille Mail #

La famille `mail` permet de demander l'installation de sessions Javamail pour envoyer
ou recevoir des mails avec les protocoles SMTP, POP3 et IMAP.

## Déclaration JEE ##
La ressource doit être déclarée dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-ref>
   <description>Accès mail</description>
   <res-ref-name>mail/MonMail</res-ref-name>
   <res-type>javax.mail.Session</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```

## Déclaration jndi-resource ##
Les métas-données pour décrire les exigences de la ressource doivent être décrite
dans le fichier [jndi-resources.xml](jndiResourcesXML.md). Par exemple :
```
...
<resource
  name="jndi-web-sample/mail/MonMail"
  familly="mail/default"
>
  <property name="store.protocol" value="imap"/>
  <property name="transport.protocol" value="smtp"/>
</resource>
<resource name="jndi-web-sample/mail/email" familly="mail/email" >
  <property name="email" value="${myemail}"/>
</resource>
...
```

## Propriétés ##
Voici les propriétés optionnelles que vous pouvez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).

| **Famille Mail** | |
|:-----------------|:|
| **Propriété** | **Description** |
| `user` | L'utilisateur des connexions SMTP, IMAP et POP3. Cela n'est pas équivalent au mail de l'utilisateur. La valeur par défaut est : `${mail.user}`|
| `password` | Le mot de passe de l'utilisateur pour ouvrir les connexions des protocoles. La valeur par défaut est : `${mail.password}`|
| `from` | L'email source lors de l'envoie de messages. La valeur par défaut est : `${mail.from}`|
| `host` | Le nom du poste accueillant les protocoles SMTP et IMAP ou POP3. La valeur par défaut est : `${mail.host}`|
| `debug` | Pour activer le déverminage de Javamail. La valeur par défaut est : `${mail.debug}` |
| `store.protocol` | Le protocole à utiliser pour la boite aux lettres. Valeurs possibles  : `imap` ou `pop3` La valeur par défaut est : `${mail.store.protocol}` |
| `store.host` | Le host du protocole de gestion de la boite aux lettres. La valeur par défaut est : `${mail.store.host}` |
| `store.port` | Le port du protocole de gestion de la boite aux lettres. La valeur par défaut est : `${mail.store.port}`|
| `store.user` | L'utilisateur de connexion pour accéder à la boite aux lettres. La valeur par défaut est : `${mail.store.user}`|
| `store.connectiontimeout` | Le délais maximum pour ouvrir la connexion d'accès à la boite aux lettres. La valeur par défaut est : `${mail.store.connectiontimeout}`|
| `store.timeout` | Le délais maximum pour effectuer une action sur la boite aux lettres. La valeur par défaut est : `${mail.store.timeout}`|
| `transport.protocol` | Le protocole de transport. La valeur par défaut est : `${mail.transport.protocol}`|
| `transport.host` | Le nom du serveur du protocole de transport. La valeur par défaut est : `${mail.transport.host}`|
| `transport.port` | Le port du protocole de transport. La valeur par défaut est : `${mail.transport.port}`|
| `transport.user` | L'utilisateur de connexion pour le protocole de transport. La valeur par défaut est : `${mail.transport.user}`|
| `transport.connectiontimeout` | Le délais maximum pour ouvrir la connexion avec le protocole de transport. La valeur par défaut est : `${mail.transport.connectiontimeout}`|
| `transport.timeout` | Le délais maximum pour effectuer une action avec le protocole de transport. La valeur par défaut est : `${mail.transport.timeout}` |


## Extensions et limitations ##
Pour ajouter des paramètres spécifiques à un serveur d'application, il faut ajouter
à la déclaration de ressources, des informations complémentaires dont voici des exemples.
```
...
<resource name="jndi-web-sample/mail/MonMail" familly="mail/default">
  <property name="store.protocol" value="imap"/>
  <property name="transport.protocol" value="smtp"/>
  <extends appsrv="tomcat6_0">
    <attribute name="mail.smtp.user">moi@chezmoi.maison</attribute>
  </extends>
  <extends appsrv="jboss3_x">
    <attribute name="mail.smtp.user">moi@chezmoi.maison</attribute>
  </extends>
</resource>
...
```

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `mail/default` | Utilise les _drivers_ par défauts de Javamail, à savoir SMTP pour le transport et POP3 ou IMAP pour la sauvegarde des messages. |
| `mail/email` | Un email unique, complémentaire à l'e-mail de la session Javamail. |

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| **`${mail.host}`** | Le serveur gérant les protocoles de transport et de messagerie. Il n'y a pas de valeur par défaut. Ce paramètre doit être valorisé lors de l'installation.|
| **`${mail.from}`** | L'email de l'émetteur des messages. Il n'y a pas de valeur par défaut.|
| `${mail.user}` | Le mot de passe de l'utilisateur de connexion. Valeur par défaut : `""`|
| `${mail.debug}` | L'état de déverminage de Javamail. Valeur par défaut : `false` |
| `${mail.store.protocol}` | Le protocole de « store ». Les valeurs possibles sans _driver_ supplémentaires sont : `imap` et `pop3`. Valeur par défaut : `pop3` |
| `${mail.store.host}` | Le serveur de mail. Valeur par défaut : `${mail.host}` |
| `${mail.store.port}` | Le port du protocole sur le serveur de mail. Valeur par défaut : `${mail.${mail.store.protocol}.default.port}` |
| `${mail.store.user}` | L'utilisateur de connexion pour accéder à la messagerie. Valeur par défaut : `${mail.user}`|
| `${mail.store.password}` | Le mot de passe pour accéder à la messagerie. Valeur par défaut : `${mail.password}` |
| `${mail.store.connectiontimeout}` | Le délais maximum pour ouvrir la connexion à la messagerie. Valeur par défaut : -1 |
| `${mail.store.timeout}` | Le délais maximum pour un traitement sur la messagerie.  Valeur par défaut : -1 |
| `${mail.transport.protocol}` | Le protocole de transport par défaut. Valeur par défaut : `smtp`|
| `${mail.transport.host}` | Le serveur du protocole de transport. Valeur par défaut : `${mail.host}` |
| `${mail.transport.connectiontimeout}` | Le délais maximum pour la connexion au protocole de transport. Valeur par défaut : -1|
| `${mail.transport.timeout}` | Le délais maximum pour un traitement avec le protocole de transport. Valeur par défaut : -1|
| `${mail.pop3.user}` | L'utilisateur pour une connexion POP3. Valeur par défaut : `${mail.store.user}` |
| `${mail.pop3.host}` | Le serveur POP3. Valeur par défaut : `${mail.store.host}`|
| `${mail.pop3.port}` | Le port pour l'accès à POP3. Valeur par défaut : `${mail.store.port}`|
| `${mail.pop3.default.port}` | Le port par défaut pour le protocole POP3. Valeur : 110|
| `${mail.pop3.connectiontimeout}` | Valeur par défaut : `${mail.store.connectiontimeout}` |
| `${mail.pop3.timeout}` | Valeur par défaut : `${mail.store.timeout}` |
| `${mail.imap.user}` | Valeur par défaut : `${mail.store.user}` |
| `${mail.imap.host}` | Valeur par défaut : `${mail.store.host}` |
| `${mail.imap.port}` | Valeur par défaut : `${mail.store.port}` |
| `${mail.imap.default.port}` | Le port par défaut pour le protocole IMAP. Valeur : 143|
| `${mail.imap.connectiontimeout}` | Valeur par défaut : `${mail.store.connectiontimeout}` |
| `${mail.imap.timeout}` | Valeur par défaut : `${mail.store.timeout}` |
| `${mail.smtp.user}` | L'utilisateur pour la connexion SMTP. Valeur par défaut : `${mail.transport.user}` |
| `${mail.smtp.host}` | Le serveur SMTP. Valeur par défaut : `${mail.transport.host}` |
| `${mail.smtp.port}` | Le port d'accès au serveur SMTP. Valeur par défaut : `${mail.transport.port}` |
| `${mail.smtp.connectiontimeout}` | Valeur : `${mail.transport.connectiontimeout}` |
| `${mail.smtp.timeout}` | Valeur : `${mail.transport.timeout}`|

Référence : http://java.sun.com/products/javamail/javadocs/overview-summary.html

[Les autres ressources](ressources.md)