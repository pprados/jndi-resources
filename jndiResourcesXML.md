# Description des ressources #

Un fichier `jndi-resources.xml` permet d'enrichir de méta-informations la description
des ressources des fichiers `web.xml` et `ejb-jar.xml`. Le format est très simple.
Un [schéma](http://www.prados.fr/xsd/1.0/jndi-resources.xsd) et un espace de noms
sont associés à la description.

Voici un premier exemple :
```
<?xml version="1.0" encoding="UTF-8"?>

<resources
  xmlns="http://jndi-resources.googlecode.com/1.0/"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd"
  id="jndi-web-sample"
>
	<resource name="jndi-web-sample/jdbc/Default" familly="jdbc/default" />
</resources>
```

Ce fichier décrit que pour la ressource `jndi-web-sample/jdbc/Default`, il faut
utiliser la [famille](familles.md) `jdbc/default`. Cela correspond à une source de
données par défaut, suivant le serveur d'applications utilisé. La clef
`java:/jndi-web-sample/jdbc/Default` sera valorisée par le serveur d'application,
ainsi éventuellement que la clef `/jndi-web-sample/jdbc/Default`.

En modifiant la [famille](familles.md) par `jdbc/oracle`, la source de donnée sera une
base de donnée de type Oracle. Le driver Java sera automatiquement installée dans
le serveur d'applications si nécessaire.

Il est possible d'indiquer plus de précisions pour déclarer une ressource.
```
<?xml version="1.0" encoding="UTF-8"?>

<resources xmlns="http://www.jndi-resources.org/1.0/"
	id="jndi-web-sample">
	<resource name="jndi-web-sample/jdbc/Default" familly="jdbc/oracle">
		<property name="min-pool-size" value="5"/>
		<property name="max-pool-size" value="20"/>
		<property name="prepared-statement-cache-size" value="32"/>
	</resource>
</resources>
```
En ajoutant des propriétés à la ressource, les fichiers de paramètres des serveurs
d'applications seront adaptés.

Chaque serveur utilisant des paramètres différents, les données indiquées dans le
fichier `jndi-resources.xml` sont converties suivant les exigences des serveurs
et des versions des serveurs.

Une liste limitée de paramètres peut être indiquée. Cela correspond aux paramètres
principaux, présents sur tous les serveurs. Pour les autres paramètres, plus spécifiques
à un serveur particulier, il est possible d'indiquer des compléments. Cela est
fortement déconseillé, car cela crée une adhérence avec une version particulière
d'un serveur. Un équivalent n'est pas toujours disponible sur d'autres serveurs.

Pour indiquer d'autres paramètres, il faut utiliser le marqueur `<extends/>` en
indiquant le [modèle](moteurConfiguration.md) de transformation à enrichir.
```
<?xml version="1.0" encoding="UTF-8"?>

<resources xmlns="http://www.jndi-resources.org/1.0/"
	id="jndi-web-sample">
	<resource name="jndi-web-sample/jdbc/Default" familly="jdbc/oracle">
		<property name="min-pool-size" value="5"/>
		<property name="max-pool-size" value="20"/>
		<property name="prepared-statement-cache-size" value="32"/>
		<extends appsrv="tomcat5_5">
			<attribute name="removeAbandonedTimeout">60</attribute>
		</extends>
		<extends appsrv="jboss3_x">
			<backgroundValidationFrequencySeconds>
			900
			</backgroundValidationFrequencySeconds>
		</extends>
	</resource>
</resources>
```

Toutes les propriétés et les extensions peuvent utiliser des [variables](variables.md) qui
seront valorisées lors de l'installation. Les variables sont de la forme `${...}`.

Précédent : [transformations](transformations.md)
Suite : [Les ressources globales](ressources.md)