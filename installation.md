# Installation #

Le programme est [disponible](http://code.google.com/p/jndi-resources/downloads/list)
sous la forme d'une archive `tar.gz` ou `zip` qu'il faut déplier dans un répertoire.

L'archive `jndi-resources-<version>.(tar.gz|zip)` possède tous les utilitaires.

L'archive `jndi-resources-<version>-install.(tar.gz|zip)` ne possède que le
nécessaire pour installer les ressources d'un composant JEE dans un serveur
d'applications. C'est une version plus économe en place disque.

  * Le répertoire `$JNDI_HOME/bin` possède trois scripts : `jndi-config`, `jndi-install` et `jndi-resources`.
  * Le répertoire `$JNDI_HOME/lib` possède les librairies Java nécessaires aux utilitaires.
  * Le répertoire `$JNDI_HOME/templates` possède les règles de productions des fichiers de paramétrage pour les différents serveurs d'applications.
  * Le répertoire `$JNDI_HOME/xslt` possède, à titre d'illustration, des scripts XSTL pour [transformer](transformerWebXML.md) les fichiers `web.xml` et `ejb-jar.xml` vers les fichiers nécessaires aux différents serveurs d'applications (`jboss-web.xml`, `context.xml`, etc.).

Les répertoires `templates` et `xslt` ne sont pas présents dans l'archive
`jndi-resources-<version>-install.(tar.gz|zip)` car ils ne sont pas nécessaires
lors de l'installation des composants.

Si des drivers sont nécessaires à la publication de ressources, il faut utiliser
un repository [Maven](http://maven.apache.org) et déclarer un repository local
(généralement dans `~/.m2/repository`). S'il n'existe pas, le repository est
construit automatiquement lors de la première exécution.

Maven n'a pas besoin d'être installé pour utiliser **jndi-resource**. Une petite partie
est inclus dans l'outil, pour gérer le téléchargement depuis le repository central.
Par contre, si une archive n'est pas disponible dans le repository central de Maven,
généralement pour des raisons de licences, il faut l'installer à la main via
[Maven](http://maven.apache.org). Une exception spécifique indique la commande à utiliser.

Le _repository_ local est nécessaire lors de la phase de paramétrage,
mais pas lors de la phase d'installation du composant.
Les différents _drivers_ sont récupérés du repository Maven et
recopiés dans le repository local. Puis du repository local vers les scripts
d'installations.

Précédent : [Comment faire ?](comment.md)
Suite: [Exemple Rapide](exemple.md)