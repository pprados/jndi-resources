# Famille JAAS, spécial JBoss #

Contrairement aux autres ressources, la famille `jaas` ne fonctionne qu'avec JBoss.
Elle permet de demander l'installation de driver JAAS dans le serveur.

## Déclaration JEE ##
La ressource doit être déclarée dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-ref>
   <res-ref-name>jaas/intranet</res-ref-name>
   <res-type>org.jboss.security.plugins.SecurityDomainContext</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```

## Déclaration jndi-resource ##
Les métas-données pour décrire les exigences de la ressource doivent être décrite dans
le fichier [jndi-resources.xml](jndiResourcesXML.md). Par exemple :
```
...
<resource name="jndi-web-sample/jaas/default" familly="jaas/default" />
...
```

## Propriétés ##
Voici les propriétés optionnelles que vous pouvez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).

| **Famille jaas** | |
|:-----------------|:|
| **Propriété** | **Description** |
| _N/A_ | Il n'existe pas de paramètres spécifique à cette famille. |

## Extensions et limitations ##
Il n'y a pas d'extension possible pour ce type de ressource.
Cette famille ne fonctionne qu'avec les serveurs JBoss.

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `jaas/default` | Utilise le module JAAS `java:jaas/jmx-console`. C'est à dire que les utilisateurs sont les mêmes que ceux ayant accès la console JMX. |
| `jaas/intranet` | Utilise un module JAAS dont le modèle est présent dans `$TEMPLATES/jboss?_?/jaas/intranet/pattern.xml`. En modifiant le fichier, vous pouvez normaliser l'accès aux authentifications de type intranet, par exemple, en utilisant un serveur Ldap ou une base de donnée. L'installation du module se fera automatiquement. |
| `jaas/internet` | Utilise un module JAAS dont le modèle est présent dans `$TEMPLATES/jboss?_?/jaas/internet/pattern.xml`. En modifiant le fichier, vous pouvez normaliser l'accès aux authentifications de type internet, par exemple, en utilisant un serveur Ldap ou une base de donnée. L'installation du module se fera automatiquement.|

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| _N/A_ | Cette famille n'a pas de besoins de variables particulières lors de l'installation. |

[Les autres ressources](ressources.md)