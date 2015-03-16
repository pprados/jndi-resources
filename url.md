# Famille URL #

La famille `url` permet d'utiliser une URL vers un répertoire (protocole `file:`),
un service Web (protocole `http:`) ou une ressources de la JVM (protocole `jar:`).

## Déclaration JEE ##
La ressource doit être déclarée dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-ref>
   <res-ref-name>url/intranet</res-ref-name>
   <res-type>java.net.URL</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```
Jboss 3.x impose de déclarer les URL dans l'archive WAR si le type de la
ressource est `java.net.URL`. Soit vous souhaitez n'utiliser qu'une version
supérieur et vous pouvez indiquer le type réel de l'instance, soit vous devez
utilisez `java.lang.Object` pour contourner la limitation de Jboss 3.x.
```
...
<resource-ref>
   <res-ref-name>url/intranet</res-ref-name>
   <res-type>java.lang.Object</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```

## Déclaration jndi-resource ##
Les métas-données pour décrire les exigences de la ressource doivent être décrite
dans le fichier [jndi-resources.xml](jndiResourcesXML.md). Par exemple :
```
...
<resource name="jndi-web-sample/url/default" familly="url/default" >
  <property name="url" value="${url.default}"/>
</resource>
...
```

## Propriétés ##
Voici la propriété obligatoire que vous devez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).

| **Famille URL** | |
|:----------------|:|
| **Propriété** | **Description** |
| **`url`** || Ce paramètre permet d'indiquer une variable décrivant l'URL|
qui devra être valorisée lors de l'installation du composant. Cette URL est
par défaut de type `file:` et peut alors référencer un répertoire partagé ou
un répertoire relatif au serveur d'application.

## Extensions et limitations ##
Il n'y a pas d'extension possible pour ce type de ressource.

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `url/default` | Déclare une URL à l'aide de la propriété `url`. |

D'autres familles - _à écrire_ - peuvent donner l'accès à un annuaire UDDI.

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| `${`_Utilisateur_`}` |  Vous devez déclarer vos propres variables pour permettre une valorisation lors de l'installation du composant. |

[Les autres ressources](ressources.md)