# Utilisation avancée #

Les programmes [jndi-config](ligneDeCommandes.md) et [jndi-resources](ligneDeCommandes.md) manipulent
un fichier [jndi-resources.xml](jndiResourcesXML.md) dont la syntaxe est portée par un
[schéma](http://www.prados.fr/xsd/1.0/jndi-resources.xsd) et un espace de noms.

Les paramètres d'un composant applicatif ne sont qu'une partie nécessaire à
l'installation d'une application dans son ensemble. Il faut en effet installer
une ou plusieurs base de données, un ou plusieurs serveurs HTTP, un ou plusieurs
serveur d'applications, les paramétrer chacun de façon cohérente afin que l'ensemble
puisse fonctionner. Par exemple, le paramètre `${jdbc.host}` doit être consistant
avec la localisation de la base de données installée pour l'application.

D'autres formats XML peuvent décrire les différents composants d'une application
dans son ensemble, en indiquant les exigences en terme de ressources pour chaque
élément, les paramètres associés, etc.

Il est alors possible de générer des scripts d'installation permettant de produire
des fichiers `.rpm` ou `.deb`, des images virtuelles, etc. Chaque module se charge
d'installer un composant (un serveur JBoss, l'application dans le serveur, une base
de données, un serveur Apache, etc.)

Les exigences des composants JEE ne sont alors qu'une petite partie de cette
description globale. Afin d'éviter de multiplier les fichiers, il est possible
d'inclure les informations de [jndi-resources.xml](jndiResourcesXML.md) dans un
autre fichier XML. Le paramètre `id` du marqueur `<jndi:resources/>` permet
d'identifier la ressource à manipuler lors de l'invocation de
[jndi-config](ligneDeCommandes.md). Pour cela, l'URL indiquant où trouver le
fichier [jndi-resources.xml](jndiResourcesXML.md) doit posséder un signet.

Il est alors possible d'utiliser un fichier XML global, décrivant tous les
composants à installer. Si plusieurs composants WAR, EAR, etc. sont
nécessaires à l'application, chacun aura un identifiant différent. Plusieurs
marqueurs XML `<jndi:resources/>` seront présent, avec un identifiant différent.
Il faut alors invoquer plusieurs fois la génération des paramètres, avec
un `id` différent à chaque fois, empaqueter les résultats dans des `.rpm`
ou `.deb` différents et installer tous les composants. Pour identifier le
fragment à utiliser pour générer les fichiers de configuration, il faut
indiquer la référence dans l'URL de génération.

Par exemple, pour le fichier `all-components.xml` suivant,
```
<?xml version="1.0" encoding="UTF-8"?>

<AllComponents>
   <resources xmlns="http://jndi-resources.googlecode.com/1.0/"
	id="one">
	<resource name="comp_one/jdbc/Default" familly="jdbc/default" />
   </resources>

   <resources xmlns="http://jndi-resources.googlecode.com/1.0/"
	id="two">
	<resource name="comp_two/jdbc/Default" familly="jdbc/default" />
   </resources>
</AllComponents>
```

Les générations peut s'effectuer à l'aide de :
```
>$JNDI_HOME/bin/jndi-config --jndi-file all-components.xml#one --package ./packages_one
>$JNDI_HOME/bin/jndi-config --jndi-file all-components.xml#two --package ./packages_two
```
En conformité avec les règles des fichiers XML, le
[schéma jndi-resources](http://www.prados.fr/xsd/1.0/jndi-resources.xsd) accepte
d'accueillir d'autres marqueurs qu'il se contente d'ignorer.

Précédent : [ressources](ressources.md) Suivant : [Le moteur d'installation](moteurInstallation.md)