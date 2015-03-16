# Paramètres de la ligne de commandes #

Il existe trois utilitaires : l'un se charge de générer les fichiers de
configuration à partir de fichiers [jndi-resources.xml](jndiResourcesXML.md),
le deuxième se charge d'installer les fichiers pour un serveur d'applications
et le troisième est un enchaînement des deux premiers.

**jndi-config** accepte différents paramètres :
```
Usage: jndi-config [-h] (-w <war|ear> | -j <jndi-resources.xml>) \
                   [-t <templates>] -p <destination>
(-w|--war) <war|ear file>     : The jndi-resources descriptions in META-INF
(-j|--jndi-file) <url[#id]>   : The jndi-resources descriptions fragment.
(-t|--templates) <dir>        : The templates transformations to use.
(-p|--packages) <destination> : The destination directory.
-l                            : Log info.
-ll                           : Log debug.
(-h|--help)                   : This help
```
Vous pouvez indiquer un fichier [jndi-resources.xml](jndiResourcesXML.md) à l'aide du
paramètre `-j`, indiquer une archive `.war` ou `.ear` possédant déjà un fichier
`META-INF/jndi-resources.xml` ou une combinaison des deux paramètres.
Si vous utilisez le paramètre `-w`, le composant sera également installé dans
le serveur d'application. Sinon, seule les ressources seront publiées.

Pour demander uniquement la publication des ressources nécessaires à un composant,
sans le composant lui-même, utilisez une URL de type `jar:file:` pour le paramètre `-j`.
```
jndi-config -j jar:file:application.war!/META-INF/jndi-resources.xml ...
```

Cet utilitaire exploite les _repositories_ [Maven](http://maven.apache.org) pour y
extraire les _drivers_ nécessaires à l'application à installer. Un _repository_ local
est créé dans `~/.m2/repository` s'il n'en existe pas. Si vous n'utilisez que des ressources présentes dans le _repository_ centrale, il n'est pas nécessaire d'installer Maven. Sinon, vous devez l'installer pour pouvoir ajouter les archives manquantes ( généralement à cause des licences) dans votre _repository_ local.

**jndi-install** accepte les paramètres suivant :
```
Usage: jndi-install [-h] -d <appsrvconfdir=dir>
                    [-D<key>=<value>|xpath:<[ns,]xpath>]* \
                    [--xsl key=value>|xpath:<[ns,]xpath>]* \
                    [-P <url>]* -p <sourcepackage> -a <jboss|...>[,...]*
(-d|--dest) <key>=<value>              : Define destination directories
-D<key>=<value>|xpath:<[ns,]xpath>     : Define property
--xsl <key>=<value>|xpath:...          : Define XSL variable
(-P|--properties) <url>                : List of properties
(-p|--package) <sourcepackage>         : Sources product with JNDIConfig
(-a|--appsrv) <jboss|tomcat...>[,...]* : Familly of application server
(-v|--version)                         : Application server version to use
-l                                     : Log info.
-ll                                    : Log debug.
(-h|--help)                            : This help
```

**jndi-resources** est une combinaison des deux premiers utilitaires.
Il n'est alors plus nécessaire d'indiquer le paramètre `--package` car
un répertoire temporaire est utilisé. Vous pouvez continuer à l'utiliser
si vous souhaitez jeter un œil sur les fichiers générés.
```
Usage: jndi-resources [-h] (-w <war|ear> | -j <jndi-resources.xml>) \
                      [-t <templates>] [-p <destination>] -d <appsrvconfdir=dir> \
                      [-D<key>=<value>|xpath:<[ns,]xpath>]* \
                      [--xsl key=value>|xpath:<[ns,]xpath>]* \
                      [-P <url>]* -p <sourcepackage> \
                      [-a <jboss|...>[,...]* -v <version>
(-w|--war) <war|ear file>              : The jndi-resources descriptions in META-INF
(-j|--jndi-file) <url[#id]>            : The jndi-resources descriptions fragment.
(-t|--templates) <dir>                 : The templates transformations to use.
(-p|--packages) <destination>          : The temporary configuration directory.
(-d|--dest) <key>=<value>              : Define destination directories
-D<key>=<value>|xpath:<[ns,]xpath>     : Define property
--xsl <key>=<value>|xpath:...          : Define XSL parameter
(-P|--properties) <url>                : List of properties files
(-a|--appsrv) <jboss|tomcat...>[,...]* : Familly of application server
(-v|--version)                         : Application server version to use
-l                                     : Log info.
-ll                                    : Log debug.
(-h|--help)                            : This help
```

Il est possible d'utiliser **jndi-config** sous Windows et **jndi-install** sous unix
ou l'inverse. Le répertoire `package` est cross système d'exploitation.

Précédent : [Les conventions](conventions.md)
Suite : [Désinstallation des ressources](desinstallation.md)