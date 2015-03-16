# Moteur de configuration #

Pour bien comprendre le moteur de configuration, il faut auparavant avoir saisie
le fonctionnement du [moteur d'installation](moteurInstallation.md).

Comme pour l'[installation](moteurInstallation.md), la génération des différentes
configurations pour les différents serveurs d'applications s'appuie sur un moteur
et des transformations XSL 2.0.

Un modèle de transformation est proposé pour générer toutes les déclinaisons à
partir du fichier [jndi-resources.xml](jndiResourcesXML.md). Une implantation est
disponible par défaut dans le répertoire `$JNDI_HOME/templates`. Elle peut être
enrichie ou modifiée pour tenir compte des spécificités de votre entreprise. Cela
permet de garantir une approche uniforme dans la gestion des ressources des
composants JEE sur les différentes plate-formes de pré-intégration, intégration, etc.

Par exemple, vous pouvez décider que par défaut, les [sources de données](jdbc.md) sont
de type Oracle, même s'il existe d'autres choix dans le modèle original. Vous
pouvez également indiquer des valeurs par défaut pour les paramètres, différents
de ceux proposés dans le modèle livré avec l'utilitaire.

Le répertoire `$JNDI_HOME/templates/` est organisé comme ci-dessous.

![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/templates.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/templates.png)

Il est possible d'utiliser un répertoire différent, grâce à un paramètre de
la ligne de commande (`--templates`). Dans la suite de ce document, ce répertoire
est identifié par `$TEMPLATES`.

Chaque modèle de transformation est porté par un répertoire (`jboss3_x`,
`tomcat4_0`, `tomcat5_5`, etc.). Pour chacun des modèles, un répertoire est
proposé pour chaque famille de ressource décrite dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).

Le moteur exécute successivement différents fichiers XSL, avec le fichier
de ressources en entrée et différents paramètres permettant d'aider les traitements.

Le processus est le suivant :
  * exécution de `$TEMPLATES/process.xslt` ;
  * pour chaque répertoire présent dans `$TEMPLATES`, sauf pour `$TEMPLATES/tools` :
    * exécution de `$TEMPLATES/`_modèle_`/process.xslt`,
  * pour chaque famille trouvée dans le fichier [jndi-properties.xml](jndiResourcesXML.md) :
    * exécution de `$TEMPLATES/`_modèle_`/`_famille_`/process.xslt`

Par exemple, si le fichier ne possède que la famille `jdbc/default`, avec
uniquement les modèles `tomcat4_0` et `jboss3_x`, les fichiers suivants sont exécutés :
  * `$TEMPLATES/process.xslt`
  * `$TEMPLATES/jboss3_x/process.xslt`
  * `$TEMPLATES/jboss3_x/jdbc/default/process.xslt`
  * `$TEMPLATES/tomcat4_0/process.xslt`
  * `$TEMPLATES/tomcat4_0/jdbc/default/process.xslt`

Ces traitements ont pour vocation de générer les fichiers nécessaires au
[moteur d'installation](moteurInstallation.md).

Pour ajouter un nouveau serveur, il suffit d'ajouter un répertoire dans
la racine, de le déclarer dans le fichier `$TEMPLATES/version.xml` et de
s'inspirer des autres transformations.

Les paramètres que reçoivent les scripts `xslt` sont décrits dans le tableau suivant :
| **Variable** | **Valeur** |
|:-------------|:-----------|
| version | La version de l'utilitaire jndi-resources. |
| appsrv | Le nom du répertoire racine en cours de traitements. |
| familly | La famille en cours de traitements. |
| targetdir | Le répertoire de destination. |
| currentid | L'id courant identifiant le marqueur racine dans `jndi-resources.xml`.|
| parentwar | Le répertoire de l'archive war. |
| war | Le nom de l'archive war. |

Ensuite, tous dépend des transformations effectuées dans les modèles de
transformation. Consultez les sources pour avoir plus de précisions.

Les transformations étant généralement proches d'une version de serveur
à une autre, des `<xsl:include/>` permettent de récupérer le code décrit
pour une autre version. Par convention, la version de référence est la
plus récente. Les autres en dépendent généralement. Il est alors possible
de supprimer du modèle les anciennes versions qui ne sont plus maintenues,
par effacement des répertoires.

Voir aussi : [Le moteur d'installation](moteurInstallation.md), [Nouvelle ressource pour Tomcat](extensionTomcat.md), [Nouvelle ressource pour JBoss](extensionJBoss.md), [Les règles de codage](reglesCodage.md)