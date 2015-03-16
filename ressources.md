# Les ressources globales #

Voici, pour chaque type de ressources, les différentes propriétés prises en compte,
les différentes déclinaisons disponibles et les variables utilisées.

Avant tout, il existe une variable globale, indépendante des ressources utilisées.

| **Variables** | **Description** |
|:--------------|:----------------|
| `${jndi.prefix}` | Valeur par défaut : `""`. Cette variable permet d'ajouter un préfixe aux clefs JNDI utilisées pour déclarer les ressources. Cela permet d'organiser comme vous le voulez, l'arbre JNDI. |

Les familles possibles :
  * [host](host.md)
  * [jaas](jaas.md)
  * [jdbc](jdbc.md)
  * [jms](jms.md)
  * [jndi](jndi.md)
  * [mail](mail.md)
  * [url](url.md)
  * [link](link.md)

Les différents drivers sont récupéré d'un repository [Maven](http://maven.apache.org).
S'il s ne sont pas disponible, il faut les ajouter à la main.
Par exemple, les librairies d'accès aux files JMS ne peuvent être publié dans le
repository Maven, car Sun l'interdit. Si la librairie est nécessaire à l'installation
de la ressource, les outils vont produire une exception indiquant à la démarche à suivre.
```
Try downloading the file manually from the project website.

Then, install it using the command: 
    mvn install:install-file -DgroupId=javax.jms -DartifactId=jms -Dversion=1.1 -Dpackaging=jar -Dfile=/path/to/file

Alternatively, if you host your own repository you can deploy the file there: 
    mvn deploy:deploy-file -DgroupId=javax.jms -DartifactId=jms -Dversion=1.1 -Dpackaging=jar -Dfile=/path/to/file -Durl=[url] -DrepositoryId=[id]
```
Téléchargez l'archive officielle sur le de site, puis utilisez Maven pour l'installer
dans votre repository.

Précédent : [jndi-resources.xml](jndiResourcesXML.md)
Suite : [Utilisation avancée](utilisationAvancee.md)