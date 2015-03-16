# Création d'une nouvelle ressource pour Tomcat #

Pour ajouter une nouvelle ressource, sans possibilité de paramétrage autre que
des [variables](variables.md), il faut créer un simple fichier XSLT dans un répertoire.

Nous désirons utiliser une ressource de la nouvelle famille `jdbc/mydatasource`.
Nous souhaitons, dans un premier temps, générer la ressource pour un serveur
Tomcat, version 6.0 et supérieur.

Consultons le fichier `templates/versions.xml`.
```
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="../xslt/xslt-to-xhtml.xslt" ?>
<versions
  xmlns="http://jndi-resources.googlecode.com/1.0/versions"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/versions 
                      tools/xsd/jndi-resources-versions.xsd"
>
  <appsrv name="jboss"          versions="[3.0,4.0)"   target="jboss3_x"/>
  <appsrv name="jboss"          versions="[4.0,)"      target="jboss4_x"/>
  <appsrv name="jboss-jms-srv"  versions="[3.0,5.0)"   target="jboss3_x-jms-srv"/>
  <appsrv name="jboss-jms-srv"  versions="[5.0,)"      target="jboss5_x-jms-srv"/>
  <appsrv name="jboss-jndi-srv" versions="[3.2,4.0)"   target="jboss3_x-jndi-srv"/>
  <appsrv name="jboss-jndi-srv" versions="[4.0,)"      target="jboss4_x-jndi-srv"/>
  <appsrv name="jboss-jndi-cli" versions="[3.2,)"      target="jboss3_x-jndi-cli"/>
  <appsrv name="tomcat"         versions="[4.0,5.5)"   target="tomcat4_0"/>
  <appsrv name="tomcat"         versions="[5.5,6.0)"   target="tomcat5_5"/>
  <appsrv name="tomcat"         versions="[6.0,)"      target="tomcat6_0"/>
</versions>
```
Ce fichier indique que la production pour le serveur Tomcat, version 6.0+ est
portée par le répertoire `templates/tomcat6_0`. Nous créons alors un
répertoire `templates/tomcat6_0/jdbc/mydatasource`. Dans ce répertoire, nous
devons rédiger un fichier `process.xslt` qui se chargera de produire le fichier
de paramètres.

Commençons par rédiger une version « à la main » du fichier à produire. Nous
souhaitons injecter un fragment XML dans le fichier `conf/server.xml`.
Par exemple, le fichier `mydatasource.xml` ne reprend que le fragment et
sa localisation (le marqueur racine indique où injecter le fragment).
```
<Server>
  <GlobalNamingResources>
    <Resource name="${jndi-prefix}JNDI-test/jdbc/MyDS"
              auth="Container"
              type="javax.sql.DataSource"
              driverClassName="${jdbc.myds.driver}"
              url="${jdbc.myds.url.default}"
              username="${jdbc.myds.username}"
              password="${jdbc.myds.password}"
              maxIdle="${jdbc.min-pool-size}"
              maxActive="${jdbc.max-pool-size}"
              maxOpenPreparedStatements="${jdbc.prepared-statement-cache-size}"
              validation-query="${jdbc.myds.validation-query}"
              maxwait="${jdbc.max-wait}"/>
  </GlobalNamingResources>
</Server>
```
Nous utilisons des [variables](variables.md) pour les différents champs afin de permettre
une adaptation de la ressource lors de l'installation.

Renommons ce fichier en `process.xslt`. Ensuite, nous devons ajouter quelques lignes.

Commençons par ajouter au tout début, les déclarations pour indiquer que
le fichier est un filtre XSLT et ajoutons les différents espaces de noms.
Nous déclarons également les différentes variables XSL accessibles dans le filtre.
```
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        version="2.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:sch="http://www.w3.org/2001/XMLSchema"
        xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
        xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
        exclude-result-prefixes="#all"
>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:param name="version"/>
<xsl:param name="appsrv"/>
<xsl:param name="familly"/>
<xsl:param name="currentid"/>
<xsl:param name="targetdir"/>
...
```

L'espace de nom `jndi` correspond à l'espace de nom de la syntaxe de
l'utilitaire. L'espace de nom `tools` est un lien vers la classe
[XSLTools](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/cmd-line/src/main/java/com/googlecode/jndiresources/tools/XSLTools.java)
de l'utilitaire permettant des copies de fichiers, des traces, la récupération
des librairies à travers le repository [Maven](http://maven.apache.org), etc.

Déclarons ensuite deux variables pour le nom du fichier à produire.
```
<xsl:variable  name="basefilename">catalina.base/conf/<xsl:value-of select="$currentid"/>_myds-ds</xsl:variable>
<xsl:variable  name="filename"><xsl:value-of select="concat($targetdir,$basefilename,'.jndi')"/></xsl:variable>
```

Nous pouvons ensuite déclarer un premier template XSLT pour gérer toutes
les ressources de la mêmes famille à partir du même script.
```
...
<xsl:template match="jndi:resources[@id=$currentid]">
  <xsl:value-of select="tools:info(concat('Generate ',$filename))"/>
  <xsl:result-document  href="{$filename}">
  <xsl:processing-instruction name="jndi-stylesheet">href="xslt/update-tomcat-server.xslt"</xsl:processing-instruction>
  <xsl:processing-instruction name="jndi-stylesheet">href="xslt/save.xslt" target="conf/server.xml"</xsl:processing-instruction>

  <Server>
    <GlobalNamingResources>
	<xsl:apply-templates />
    </GlobalNamingResources>
  </Server>

  </xsl:result-document>
</xsl:template>

<xsl:template match="text()|comment()" />

...
```

Nous retrouvons les marqueurs du noeud d'injection, à savoir `<Server/>` et `<GlobalNamingResources/>`.

Notez la présence de deux `processing-instruction`. Elles permettront la
transformation des variables lors de l'[installation](moteurInstallation.md) effective
du fichier dans le serveur d'application ; l'injection du noeud dans le fichier
`server.xml` et la sauvegarde du résultat dans `conf/server.xml`.

Comme les instructions invoquent les [filtre](moteurInstallation.md) `update-tomcat-server.xslt`
et `save.xslt`, nous devons les copier dans le répertoire d'installation pour qu'ils soient disponible.

```
<xsl:template match="jndi:resources[@id=$currentid]">
  <xsl:value-of select="tools:fileCopy('../../../tools/xslt/save.xslt',concat($targetdir,'../xslt/'))"/>
  <xsl:value-of select="tools:fileCopy('../../update-tomcat-server.xslt',concat($targetdir,'../xslt/'))"/>
...
</xsl:template>
```

Ensuite, nous devons déclarer un template XSLT pour générer toutes les ressources de la famille.

```
<xsl:template match="jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]">
  <Resource name="${{jndi-prefix}}{@name}"
            auth="Container"
            type="javax.sql.DataSource"
            driverClassName="${{jdbc.myds.driver}}"
            url="${{jdbc.myds.url.default}}"
            username="${{jdbc.myds.username}}"
            password="${{jdbc.myds.password}}"
            maxIdle="${{jdbc.min-pool-size}}"
            maxActive="${{jdbc.max-pool-size}}"
            maxOpenPreparedStatements="${{jdbc.prepared-statement-cache-size}}"
            validation-query="${{jdbc.myds.validation-query}}"
            maxwait="${{jdbc.max-wait}}"/>
</xsl:template>
```

Nous y avons inclus le modèle de production, à l'exclusion du marqueur racine
`<GlobalNamingResources/>`. Nous injectons le nom de la ressource dans l'attribut
`name`. Nous devons également doubler les accolades pour les variables, afin
qu'elles restent présente dans le fichier généré.

Notre script est maintenant capable de produire le fichier de ressource pour le
serveur indiqué. Si un fichier [jndi-resources.xml](jndiResourcesXML.md) déclare la
famille `jdbc/mydatasource`, pour tous les autres serveurs, une alerte indique
que la ressource n'est pas gérées pour le moment. En reproduisant les mêmes
étapes pour les différents serveurs, vous pouvez faire sauter les alertes, et
générer la ressource pour les différents environnement d'exécution.

Le fichier complet est alors comme ceci :
```
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        version="2.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:sch="http://www.w3.org/2001/XMLSchema"
        xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
        xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
        exclude-result-prefixes="#all"
>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:param name="version"/>
<xsl:param name="appsrv"/>
<xsl:param name="familly"/>
<xsl:param name="currentid"/>
<xsl:param name="targetdir"/>

<xsl:variable  name="basefilename">catalina.base/conf/<xsl:value-of select="$currentid"/>_myds-ds</xsl:variable>
<xsl:variable  name="filename"><xsl:value-of select="concat($targetdir,$basefilename,'.jndi')"/></xsl:variable>

<xsl:template match="text()|comment()" />

<xsl:template match="jndi:resources[@id=$currentid]">
  <xsl:value-of select="tools:info(concat('Generate ',$filename))"/>
  <xsl:value-of select="tools:fileCopy('../../../tools/xslt/save.xslt',concat($targetdir,'../xslt/'))"/>
  <xsl:value-of select="tools:fileCopy('../../update-tomcat-server.xslt',concat($targetdir,'../xslt/'))"/>
  <xsl:result-document  href="{$filename}">
  <xsl:processing-instruction name="jndi-stylesheet">href="xslt/update-tomcat-server.xslt"</xsl:processing-instruction>
  <xsl:processing-instruction name="jndi-stylesheet">href="xslt/save.xslt" target="conf/server.xml"</xsl:processing-instruction>

  <GlobalNamingResources>
        <xsl:apply-templates />
  </GlobalNamingResources>

  </xsl:result-document>
</xsl:template>


<xsl:template match="jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]">
  <Resource name="${{jndi-prefix}}{@name}"
            auth="Container"
            type="javax.sql.DataSource"
            driverClassName="${{jdbc.myds.driver}}"
            url="${{jdbc.myds.url.default}}"
            username="${{jdbc.myds.username}}"
            password="${{jdbc.myds.password}}"
            maxIdle="${{jdbc.min-pool-size}}"
            maxActive="${{jdbc.max-pool-size}}"
            maxOpenPreparedStatements="${{jdbc.prepared-statement-cache-size}}"
            validation-query="${{jdbc.myds.validation-query}}"
            maxwait="${{jdbc.max-wait}}"/>
</xsl:template>

</xsl:stylesheet>
```

S'il est également nécessaire d'ajouter une librairie dans le serveur d'application,
il faut utiliser des utilitaires à cet effet. Ajoutons les deux lignes suivantes :
```
<xsl:include href="../../install-drivers.xslt"/>
<xsl:variable name="lib"><xsl:value-of select="concat($targetdir,'catalina.home/lib/')"/></xsl:variable>
```
Elles permettent de gérer la récupération d'une archive dans un repository
[Maven](http://maven.apache.org), et son installation future dans le serveur
d'application. La variable `lib` correspond à l'emplacement définitif de la librairie.

Nous devons invoquer le template `install-drivers` dans le template principal.
```
<xsl:template match="jndi:resources[@id=$currentid]">
  <xsl:value-of select="tools:info(concat('Generate ',$filename))"/>
  <xsl:result-document  href="{$filename}">
  <xsl:processing-instruction name="jndi-stylesheet">href="xslt/update-tomcat-server.xslt"</xsl:processing-instruction>
  <xsl:processing-instruction name="jndi-stylesheet">href="xslt/save.xslt" target="conf/server.xml"</xsl:processing-instruction>

  <Server>
     <GlobalNamingResources>
	<xsl:apply-templates />
     </GlobalNamingResources>
  </Server>

  </xsl:result-document>
  <xsl:call-template name="install-drivers"/>
</xsl:template>
```
Sans paramètre, le _driver_ à utiliser est récupérer dans le fichier `templates/drivers.xslt`.
```
<?xml-stylesheet type="text/xsl" href="../../xslt/xslt-to-xhtml.xslt" ?>
<xsl:stylesheet
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
<xsl:variable name="drivers">

	<familly name="javamail">
		<artifact>javax.activation:activation:1.1</artifact>
		<artifact>javax.mail:mail:1.4</artifact>
	</familly>
	
	<familly name="jms" artifact="javax.jms:jms:1.1"/>
	

	<familly name="jdbc/hsqldb" artifact="hsqldb:hsqldb:1.8.0.7"/>
	<familly name="jdbc/oracle" artifact="ojdbc:ojdbc:14"/>
	<familly name="jdbc/mysql"  artifact="mysql:mysql-connector-java:5.1.5"/>

	<familly name="jms/jbossmq" artifact="jboss:jbossmq-client:4.0.2"/>

	<familly name="jndi/default">  
		<artifact>com.googlecode.jndi-resources:jndi-resources-plugins:*</artifact>
	</familly>

	<familly name="jndi/file">
		<artifact>com.sun.jndi:fscontext:1.2-beta-3</artifact>
		<artifact>com.sun.jndi:providerutil:1.2</artifact>
	</familly>   
	<familly name="jndi/jboss">
	  	<artifact>jboss:jboss-common:4.0.2</artifact>
	  	<artifact>jboss:jnp-client:4.0.2</artifact>
	</familly>

	<familly name="host/default">
	  	<artifact>com.googlecode.jndi-resources:jndi-resources-plugins:*</artifact>
	 </familly>
	<familly name="mail/email">
	  	<artifact>com.googlecode.jndi-resources:jndi-resources-plugins:*</artifact>
	</familly>

</xsl:variable>
</xsl:stylesheet>
```

Ce fichier permet d'associer une ou plusieurs artifacts Maven avec une famille de
ressource. Vous pouvez ajouter votre nouvelle famille dans ce fichier.

Sinon, vous pouvez valoriser différents paramètres lors de l'invocation du template.

De même, vous pouvez proposer des valeurs par défaut pour les nouvelles [variables](variables.md)
dans le fichier `templates/templates.properties`.

Il faut procéder de même pour les différentes versions du serveur Tomcat.

Maintenant, vous pouvez générer la ressource en déclarant la famille dans le
fichier [jndi-resources.xml](jndiResourcesXML.md).
```
<resource name="jndi-web-sample/jdbc/MaDataSource" familly="jdbc/mydatasource"/>
```

Lors de la configuration à partir d'un fichier [jndi-resources.xml](jndiResourcesXML.md)
indiquant cette famille, le fichier `catalina.base/conf/mon-application_myds-ds.jndi`
est généré dans le répertoire `package`.

Voilà pour la description rapide de la production d'une ressource spécifique.
La meilleur documentation est le source lui-même. N'hésitez pas à y jeter un oeil
pour vous inspirer des différentes approches utilisées lors de la production des
différentes ressources.

Pour tenir compte des différents paramètres présent dans le fichier
[jndi-resources.xml](jndiResourcesXML.md), il faut adapter la production du fichier `.jndi`.


Voir aussi : [Moteur de configuration](moteurConfiguration.md), [Nouvelle ressource pour JBoss](extensionJBoss.md), [Les règles de codage](reglesCodage.md)