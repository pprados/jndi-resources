# Moteur d'installation #

**jndi-install** est un moteur d'installation très simple. Il se base sur une structure
de répertoires, des fichiers aux extensions particulières et des paramètres.

Un script d'installation est décrit à partir d'un répertoire racine que nous appelons
`$PACKAGES` dans ce document. Dans ce répertoire, il doit y avoir un répertoire
par serveur d'applications cible.

Pour chaque serveur d'applications, il doit y avoir autant de répertoires que de
répertoires majeurs du serveur d'applications. Par exemple, pour Tomcat, il
existe deux répertoires majeurs : `catalina.home` et `catalina.base`. Pour
JBoss, il existe d'autres répertoires. Les noms sont libres.

![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/packages.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/packages.png)

Les fichiers présents dans ces répertoires seront recopiés tels quels dans les
répertoires correspondant du serveur d'applications. Des paramètres de la
[ligne de commande](ligneDeCommandes.md) permettent d'associer un répertoire majeur
à un répertoire du serveur d'applications (paramètre `--dest`).

Le fichier `$PACKAGES/version.xml`, s'il est présent, permet d'associer plusieurs
versions de serveurs d'applications aux mêmes modèles.
```
<versions
	xmlns="http://jndi-resources.googlecode.com/1.0/versions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/versions tools/xsd/jndi-resources-versions.xsd"
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
Ainsi, lors de l'installation, l'utilisateur n'a pas à connaître le modèle de
paramètres à utiliser.

Pour utiliser cela, il faut lancer la ligne de commande suivante :
```
jndi-install --package $PACKAGE \
  --appsrv jboss --version 3.2 \
  --dest jboss.server.home=/opt/jboss-3.2
  --dest jboss.server.conf=/opt/jboss-3.2/server/default \
```
Cela a pour effet d'analyser le fichier `version.xml` pour identifier un
modèle de paramètres à installer. Dans le cas présent, il s'agit d'utiliser
le répertoire `$PACKAGE/jboss3_x`.

Ce répertoire possède deux répertoires : `jboss.server.conf` et
`jboss.server.home`. Il est donc nécessaire d'associer ces répertoires aux
répertoires du serveur à paramétrer. Les deux paramètres `--dest` s'en chargent.

Pratiquement tous les fichiers du répertoire
`$PACKAGE/jboss3_x/jboss.server.home` sont copiés dans le répertoire
`/opt/jboss-3.2`. Puis, pratiquement tous les fichiers du répertoire
`$PACKAGE/jboss3_x/jboss.server.conf` sont copiés dans le répertoire
`/opt/jboss-3.2/server/default`.

Les fichiers qui possèdent les extensions `.jndi` ou `.link` subissent
un traitement particulier.

Les fichiers `.link` permettent de déclarer un lien vers un autre fichier,
à l'aide d'un lien relatif. Le contenu doit être un texte dont la première
ligne indique où trouver l'original du fichier. La première ligne est lu par
le programme, puis le nom du fichier source est calculé avant d'être copié
à la place du fichier `.link`. Cette fonctionnalité permet de partager des
fichiers entres les différents serveurs d'applications. Par exemple, un
driver Oracle sera présent dans le répertoire `$PACKAGE/lib` et référencé
dans les différents modèles à l'aide de fichiers d'extension `.link`.

Les fichiers d'extension `.jndi` sont plus complexes. Ils permettent de
manipuler les fichiers XML lors de l'installation. En effet, les serveurs
d'applications utilisent généralement des fichiers au format XML. Ces
fichiers vont posséder les paramètres dont il faut encore valoriser
certaines [variables](variables.md) comme l'utilisateur et le mot de passe d'une source
de données. Il peuvent également posséder des extraits XML à insérer dans
d'autres fichiers XML du serveur d'applications.

Pour traiter ces fichiers, le moteur d'installation commence par y extraire
les `processing-instructions` XML du type `jndi-stylesheet` ou `xml-stylesheet`.
Ces deux instructions sont équivalentes.
```
<?jndi-stylesheet href="xslt/save.xslt" target="deploy/default-ds.xml"?>
```
Puis le paramètre `href` est analysé pour connaître la feuille de style à
appliquer. La base de répertoire correspond à `$PACKAGE`. Il doit alors exister
une feuille dans `$PACKAGE/xslt/save.xslt`. Les feuilles de styles sont au
format XSL 2.0. S'il existe d'autres paramètres, ils sont valorisés dans le
moteur XSL avant de lancer la transformation. Par exemple, le paramètre `target`
de l'instruction valorise la variable XSL correspondante.
```
<xsl:variable name="target">deploy/default-ds.xml</xsl:variable>
```
Note : il est interdit d'ajouter des paramètres aux instructions `xml-stylesheet`.
Voilà pourquoi il existe un alias sous le nom `jndi-stylesheet`.

Il est possible d'enchaîner les transformations, en indiquant plusieurs
instructions à la suite. Les modifications seront organisées sous la forme d'un tube.
```
<?jndi-stylesheet href="xslt/update-tomcat-server.xslt"?>
<?jndi-stylesheet href="xslt/save.xslt" target="conf/server.xml"?>
```
Le résultat de la première transformation sert d'entrée à la deuxième transformation.

Avant d'appliquer les transformations aux fichiers `.jndi`, toutes les
variables au format `${...}` sont converties en leurs valeurs respectives.
Les feuilles de styles manipulent alors des fichiers textes sans aucune variable.

Deux variables XSLT sont valorisées et peuvent être exploitées par les
feuilles de style d'installation :
| **Variable** | **Valeur** |
|:-------------|:-----------|
| version | La version de l'utilitaire jndi-resources. |
| targetdir | Le répertoire de destination du serveur d'application.|

Vous pouvez ajouter d'autres variables à l'aide de la
[ligne de commande](ligneDeCommandes.md) avec le paramètre `--xsl`.

L'utilitaire **jndi-config** se charge d'[alimenter](moteurConfiguration.md)
le répertoire `$PACKAGE` à partir des descriptions des ressources, mais vous
n'êtes pas obligé de l'utiliser pour exploiter le moteur d'installation.
Vous pouvez également enrichir le résultat proposé en ajoutant d'autres fichiers
à recopier dans la configuration des différents serveurs d'applications, ou
d'autres fichiers `.jndi` suivant le modèle de transformation proposé.

Cela permet, par exemple, d'adapter des paramètres spécifique au serveur
d'applications lors de l'installation du composant. Cela présente des risques
d'effet de bord sur les autres composants déjà présents dans le même serveur.
Un paramètre d'un serveur à un impact sur tous les composants hébergés par ce dernier.
Ceci est possible, mais pas recommandé, car cela peut entraîner une
spécialisation du serveur d'applications à une application particulière. Il
est alors plus difficile de mutualiser les ressources.

Voir aussi : [Moteur de configuration](moteurConfiguration.md), [Nouvelle ressource pour Tomcat](extensionTomcat.md), [Nouvelle ressource pour JBoss](extensionJBoss.md), [Les règles de codage](reglesCodage.md)