# Famille link #

La famille `link` permet déclarer un lien vers une autre clef JNDI.

## Déclaration JEE ##
La ressource doit être déclarée dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-ref>
   <res-ref-name>link/default</res-ref-name>
   <res-type>java.lang.Object</res-type><!-- La classe de la ressource cible -->
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```

## Déclaration `jndi-resource` ##
Les métas-données pour décrire les exigences de la ressource doivent être
décrite dans le fichier [jndi-resources.xml](jndiResourcesXML.md). Par exemple :
```
...
<resource name="jndi-web-sample/link/default" familly="link/default" >
  <property name="link" value="${mylink}"/>
</resource>
...
```

## Propriétés ##
Voici la propriété obligatoire que vous devez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).

| **Famille link** | |
|:-----------------|:|
| **Propriété** | **Description** |
| **`link`** | La variable possédant la valeur du lien ou la valeur du lien. |

## Extensions et limitations ##
Il n'y a pas d'extension possible pour ce type de ressource.

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `link/default` | Déclare un lien à l'aide de la propriété `link`. |

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| `${`_Utilisateur_`}` | Vous devez déclarer vos propres variables pour permettre une valorisation lors de l'installation du composant. |

[Les autres ressources](ressources.md)