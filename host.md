# Famille host #

La famille `host` permet d'identifier une machine par son nom ou son adresse IP v4 ou v6.
Elle permet d'identifier l'environnement d'exécution, lorsqu'il n'existe pas d'alternative
à l'aide d'[URL](url.md), de [Javamail](mail.md), etc.

## Déclaration JEE ##
La ressource doit être déclarée dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-ref>
   <res-ref-name>host/default</res-ref-name>
   <res-type>java.net.InetAddress</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```

## Déclaration `jndi-resource` ##
Les métas-données pour décrire les exigences de la ressource doivent être décrite
dans le fichier [jndi-resources.xml](jndiResourcesXML.md). Par exemple :
```
...
<resource name="jndi-web-sample/host/default" familly="host/default" >
  <property name="host" value="${host.default}"/>
</resource>
...
```

## Propriétés ##
Voici la propriété obligatoire que vous devez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).

| **Famille host** | |
|:-----------------|:|
| **Propriété** | **Description** |
| **`host`** || Ce paramètre permet d'indiquer une variable décrivant le serveur|
qui devra être valorisé lors de l'installation du composant. 

## Extensions et limitations ##
Il n'y a pas d'extension possible pour ce type de ressource.

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `host/default` | Déclare un host à l'aide de la propriété `host`. |

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont
les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| `${`_Utilisateur_`}` | Vous devez déclarer vos propres variables pour permettre une valorisation lors de l'installation du composant. |

[Les autres ressources](ressources.md)