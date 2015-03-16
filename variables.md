# Valorisation des variables de déploiements #

Il est nécessaire de valoriser quelques variables lors de l'installation comme, par
exemple, l'utilisateur et le mot de passe d'une source de données.

Les variables sont de la forme `${...}` dans les fichiers de paramètres générés
par **jndi-config**.

**jndi-install** s'occupe d'installer les fichiers de configurations après
valorisation des variables. Ainsi, les fichiers sont spécialisés à une
plate-forme lors de l'installation et non lors de la génération.

Les variables sont initialisées à l'aide de fichiers de propriétées de la forme `clef=valeur`.
```
include=~/.jndi-resources/default.properties
jdbc.username=sa
jdbc.password=password
jdbc.host=localhost
```

Si une propriété `include` est présente, le fichier référencé sera
également chargé pour alimenter les propriétés. Cela permet de structurer
les propriétés dans différents fichiers, inclus les uns les autres.

Il est également possible de valoriser les propriétés à partir d'extractions
XPath. Pour cela, la valeur de la propriété doit commencer par `xpath:`.
Chaque référence XPath suit la syntaxe suivante :
  * un préfixe optionnel pour déclarer un espace de noms par défaut ;
  * une requête XPath débutant par `document('`_url_`')`

La syntaxe de chaque ligne est la suivante :
```
<key>=xpath:[xmlns:<namespace>="<uri>",]document(<url>)<xpath>
```

Si un alias d'espace de nom est déclaré dans le fichier, il est directement
utilisable dans l'expression XPath.

Par exemple, pour récupérer l'identifiant présent dans le fichier `web.xml` :
```
id1=xpath:document('web.xml')/*[namespace-uri()='http://java.sun.com/xml/ns/j2ee' and local-name()='web-app']/@id
```
ou bien, en déclarant l'espace de nom :
```
id2=xpath:xmlns:j2ee="http://java.sun.com/xml/ns/j2ee",\
  document('web.xml')/j2ee:web-app/@id
```

Pour récupéré l'auteur d'une page XHTML :
```
user=xpath:xmlns:xhtml="http://www.w3.org/1999/xhtml",\
   document('http://www.prados.fr')//xhtml:html/xhtml:head/xhtml:meta[@name='Author']/@content
```

Pour récupérer la valeur de la cellule [2,1] présente dans un fichier
Tableur [OpenOffice.org](http://www.openoffice.org/), vous pouvez utiliser une requête de ce type :
```
user=document('jar:file:/tableur.ods!/content.xml')  \
  //table:table/table:table-row[2]/table:table-cell[1]
```

La ligne de commande de `jndi-install` permet d'indiquer une URL de
propriétés à utiliser à l'aide du paramètre `‑‑properties`. Un nom relatif
correspond à une URL de type `file:`. Vous pouvez également récupérer
les propriétés via le net avec une URL de type `http:` ou sur un serveur de
fichier via une URL de type `ftp:`. Ce paramètre peut être présent plusieurs
fois sur la ligne de commande si nécessaire.

Il est également possible d'ajouter des paramètres ponctuels dans la ligne de
commande de type propriété (`-D`). La syntaxe XPath est également acceptée.

Les propriétés sont assemblées dans un ordre précis, avant d'effectuer les
transformations lors de l'installation. Le tableau suivant indique l'ordre
de chargement. L'étape suivante peut modifier une propriété en la revalorisant.

| **Ordre de chargement des propriétés** |
|:-----------------------------------------|
| Les propriétés systèmes (Voir la classe [java.lang.System](http://java.sun.com/j2se/1.3/docs/api/java/lang/System.html)) |
| `/var/share/jndi-resources/plateform.properties` |
| `$PACKAGES/$APPSRV/templates.properties` |
| `~/.jndi-resources/plateform.properties` |
| Les paramètres de la ligne de commande. |

Cette organisation propose la démarche suivante :
  * Il existe des paramètres spécifiques à une plate-forme. Il seront présent dans `/var/share/jndi-resources/`. Par exemple, le compte utilisateur et le mot de passe par défaut pour les bases de données.
  * Des paramètres par défaut peuvent faciliter l'utilisation de l'installation. Ils seront présent dans `$PACKAGES/$APPSRV/` s'ils sont spécifique à une famille de serveur.
  * Des paramètres spécifiques à l'utilisateur, correspondant à ses choix par défauts pour ses bases de données par exemples, seront présent dans le répertoire `~/.jndi-resources/`.
  * Enfin, des modifications de dernières minutes ou les quelques informations manquantes seront indiquées dans la ligne de commande.

Précédent : [Désinstallation](desinstallation.md)
Suite : [Architecture  des serveurs d'applications](architecture.md)