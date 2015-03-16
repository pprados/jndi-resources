# Exemple rapide #

Avant de commencer l'exemple, il faut [installer](installation.md) **jndi-resources**
et avoir une instance de [Jboss](http://www.jboss.org) ou de
[Tomcat](http://tomcat.apache.org) disponible. Un rapide [rappel](rappelJNDI.md) sur
JNDI peut également être nécessaire.

Prenons l'exemple d'une archive WAR possédant le fichier `WEB-INF/web.xml` suivant :
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE web-app
  PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
         "http://java.sun.com/dtd/web-app_2_3.dtd">
<web-app
	id="jndi-web-sample"
>
	<display-name>jndi-resources-web-sample</display-name>

        <servlet>
        	<servlet-name>HelloServlet</servlet-name>
        	<display-name>Hello Servlet</display-name>
        	<servlet-class>org.sample.HelloJNDIServlet</servlet-class>
        	<load-on-startup>1</load-on-startup>
        </servlet>
	<servlet-mapping>
		<servlet-name>HelloServlet</servlet-name>
		<url-pattern>/</url-pattern>
	</servlet-mapping>

	<welcome-file-list>
		<welcome-file>/</welcome-file>
	</welcome-file-list>

	<resource-ref>
		<description>La base de données</description>
		<res-ref-name>jdbc/DB</res-ref-name>
		<res-type>javax.sql.DataSource</res-type>
		<res-auth>Application</res-auth>
		<res-sharing-scope>Shareable</res-sharing-scope>
	</resource-ref>
</web-app>
```

Notez la valeur du paramètre `id` du marqueur `<web-app/>`. Il correspond
généralement au path de déploiement de l'archive.

Nous souhaitons bénéficier de la normalisation de packaging du composant, à
savoir, la possibilité de l'utiliser sur tous les serveurs d'applications,
indépendamment de sa marque, de sa version ou de son architecture. C'est
bien l'objectif de la normalisation WAR.

Conformément aux [spécifications](comment.md), ce fichier déclare, dans
`<resource-ref/>`,  le besoin d'une ressource de type `javax.sql.DataSource`
pour la clef JNDI `java:comp/env/jdbc/DB`.

Suivant les serveurs d'applications, il faut ajouter [différents](transformerWebXML.md)
fichiers permettant d'associer la ressource `java:comp/env/jdbc/DB` avec une autre clef JNDI.
Pour JBoss, il faut ajouter le fichier `WEB-INF/jboss-web.xml` suivant :
```
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web>
	<resource-ref>
		<res-ref-name>jdbc/DB</res-ref-name>
		<res-type>javax.sql.DataSource</res-type>
		<jndi-name>java:web-sample/jdbc/DB</jndi-name>
	</resource-ref>
</jboss-web>
```

Pour Tomcat, le fichier `META-INF/context.xml` suivant :
```
<?xml version="1.0" encoding="UTF-8"?>
<Context 
	path="/jndi-web-sample"
	reloadable="true"
	antiResourceLocking="true" 
	antiJARLocking="true"
	privileged="false"
>
	<ResourceLink name="jdbc/DB"
                      global="/web-sample/jdbc/DB"
                      type="javax.sql.DataSource"/>
</Context>
```
Ces deux fichiers permettent d'associer la clef [locale](rappelJNDI.md)
`java:comp/env/jdbc/DB` avec la clef [JVM](rappelJNDI.md) `java:/web-sample/jdbc/DB`.

Par [conventions](conventions.md), les clefs locales de la branche `java:comp/env` doivent
être associées aux clefs JVM de la branche `java:`, en préfixant par l'`id`
présent dans le fichier `web.xml`. Cela permet de lever les ambiguïtés
lorsque plusieurs composants sont installés sur le même serveur d'applications.
Mais, vous pouvez utiliser d'autres conventions.

Ces règles de transformations étant systématiques, il est facile de générer ces
fichiers à partir du fichier `web.xml`. Des [scripts XSLT](transformerWebXML.md)
sont proposées dans le répertoire `$JNDI_HOME/xslt`.

En ajoutant ces deux fichiers, notre composant peut être installé sous Tomcat ou JBoss.

Il faut ensuite ajouter le fichier `META-INF/jndi-resources.xml`. C'est un
[nouveau fichier](jndiResourcesXML.md) permettant de décrire sémantiquement
(et non techniquement) les exigences des ressources du composant applicatif.
```
<?xml version="1.0" encoding="UTF-8"?>

<resources
	xmlns="http://jndi-resources.googlecode.com/1.0/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd"
	id="web-sample"
>
	<resource name="jndi-web-sample/jdbc/DB" familly="jdbc/default" />
</resources>
```
Ce fichier décrit simplement que nous souhaitons une ressouce pour la clef
`web-sample/jdbc/DB`, et y publier la base de données par défaut, associée au
serveur d'applications. Pour JBoss et Tomcat, il s'agit d'une base HSQLDB.

Vous pouvez [télécharger](http://code.google.com/p/jndi-resources/downloads/list)
l'archive d'exemple pour tester l'utilisation des différents outils.
```
>wget http://jndi-resources.googlecode.com/files/web-sample.war
```
ou bien tester l'EAR possédant le composant WAR et un EJB.
```
>wget http://jndi-resources.googlecode.com/files/ejb-sample.ear
```

Ce dernier exploite plus de ressources :
  * Une source de donnée [jdbc](jdbc.md) ;
  * une connexion [javamail](mail.md) ;
  * un [email](mail.md) complémentaire ;
  * une [url](url.md) ;
  * et un nom de [host](host.md).

Vous pouvez consulter le fichier
[META-INF/jndi-resources.xml](http://code.google.com/p/jndi-resources/source/browse/trunk/jndi-resources/web-sample/src/main/webapp/META-INF/jndi-resources.xml)
et le fichier
[WEB-INF/web.xml](http://code.google.com/p/jndi-resources/source/browse/trunk/jndi-resources/web-sample/src/main/webapp/WEB-INF/web.xml)
du composant.

Les exemples utilisent une syntaxe Unix. Sous Windows, vous devez adapter
les slashs or utiliser [Cygwin](http://www.cygwin.com).

Nous désirons générer tous les fichiers de configurations pour tous les serveurs
d'applications. L'utilitaire **jndi-config** s'en charge.
```
>$JNDI_HOME/bin/jndi-config \
	--war jndi-web-sample.war \
	--package ./package
```
Après quelques dizaines de secondes, cela génère un répertoire `./packages`,
avec des modèles de fichiers de configurations pour tous les serveurs
d'applications. C'est ce répertoire ainsi que `jndi-resources-<version>-install.(tar.gz|zip)`
qu'il faudra utiliser pour installer le composant sur n'importe quel serveur d'applications.
Le premier lancement est toujours plus long, car il faut télécharger les
différentes archives pour les ressources utilisés par le composants.
Pour avoir plus de détail sur les étapes de la génération, ajoutez le
paramètre `-l` ou `-ll` pour augmenter le niveau de trace.

Nous pouvons maintenant installer le composant sur différents serveurs d'applications.
Mais il faut auparavant, valoriser différentes variables en conformité
avec le déploiement du composant. C'est le rôle de l'installateur du composant
de valoriser ces variables. Ce n'est surtout pas le rôle du développeur ou
du projet. En effet, ces derniers ne doivent en aucun cas avoir une connaissance
de la plate-forme d'exécution.

| **Variable** | **Description** |
|:-------------|:----------------|
|jdbc.username|Le nom de l'utilisateur de la base de données.|
|jdbc.password|Le mot de passe de l'utilisateur de la base de données.|
|mail.from|Le e-mail utilisé lors de l'envoie de messages.|
|mon.e-mail|Un e-mail.|
|mail.host|Le host du serveur de mail.|
|url.default|Une URL.|
|host.default|Un nom de host.|

Ces [variables](variables.md) peuvent être décrite dans un fichier de propriétés `plateform.properties` à créer.
```
jdbc.username=sa 
jdbc.password=
mail.from=no-reply@ici.org 
mon.e-mail=moi@ici.org
mail.host=locahost
url.default=http://localhost
host.default=localhost
```

Installez le composant sur un serveur Jboss en suivant la commande ci-dessous.
Adaptez la suivant le numéro de version que vous utilisez.
```
>$JNDI_HOME/bin/jndi-install \
	--appsrv jboss --version 5.0 \
	--package ./package \
	--dest jboss.server.home=/opt/jboss-5.0
	--dest jboss.server.conf=/opt/jboss-5.0/server/default \
        -P plateform.properties
```

Il ne reste plus qu'a lancer le serveur d'application et à tester le composant.
```
>/opt/jboss-5.0/bin/run.sh
```
L'URL à consulter est généralement la suivante `http://localhost:8080/web-sample/`.
Elle peut être différente suivant le port utilisé par votre version de JBoss.
Vous constatez que la page de résultat récupère bien les instances de l'annuaire JNDI.
Une consultation de la console JMX de JBoss, et particulièrement du JNDIView, permet
de confirmer que l'installation c'est bien effectuée.

Pour Tomcat, il faut également indiquer le nom du fichier de la base de donnée pour
HSQLDB. Indiquons le dans la ligne de commande, avec le paramètre `-D`. Voici une
installation sur un Tomcat 4.1:

```
>$JNDI_HOME/bin/jndi-install \
	--appsrv tomcat --version 4.1 \
	--package ./package \
	--dest catalina.home=/opt/apache-tomcat-4.1 \
	--dest catalina.base=/opt/apache-tomcat-4.1 \
	-P plateform.properties \
        -Djdbc.hsqldb.file=MaDB
```

Vous pouvez procéder le même, avec toutes les versions des serveurs
d'applications [supportées](VersionsSupportes.md) par **jndi-resources**.

L'étape de configuration et l'étape d'installation peuvent être combinées avec
l'invocation de **jndi-resources**.

Voici l'installation sur un serveur Tomcat 4.1 directement à partir du composant WAR.
```
>$JNDI_HOME/bin/jndi-resources \
	--war jndi-web-sample.war \
	--appsrv tomcat --version 4.1 \
	--dest catalina.home=/opt/apache-tomcat-4.1 \
	--dest catalina.base=/opt/apache-tomcat-4.1 \
	-P plateform.properties \
        -Djdbc.hsqldb.file=MaDB
```

Il ne reste plus qu'a lancer le serveur d'application Tomcat et à tester le composant.
```
>/opt/apache-tomcat-4.1/bin/catalina.sh run
```
L'URL à consulter est généralement la suivante `http://localhost:8080/web-sample/`.
Elle peut être différente suivant le port utilisé par votre version de Tomcat.

L'outil gère différentes [cibles](transformations.md) et différentes
[architectures](architecture.md) pour les serveurs d'applications. Les [EJB](EJB.md) sont
également pris en compte, sans que les composants aient d'adhérence avec la
plate-forme d'exécution.

Pour comprendre comment tous cela fonctionne, il est fortement conseillé de
regarder le répertoire `package` généré par **jndi-config**.

Suivez également le [guide de migration](guideMigration.md) pour faire évoluer
votre application en conformité avec les
[spécifications](http://java.sun.com/j2ee/j2ee-1_4-fr-spec.pdf) JEE.

Précédent : [installation](installation.md)
Suite : [EJB](EJB.md)