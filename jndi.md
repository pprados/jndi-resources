# Famille JNDI #

La famille `jndi` permet de lier plusieurs annuaires. Elle est généralement utilisé
à l'insu du composant, et exploité via des liens entres clefs JNDI. Par exemple,
pour référencer un EJB déclaré dans un autre annuaire JNDI.

## Déclaration JEE ##
La ressource doit être déclarée dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-env-ref>
   <description>Un Context</description>
   <resource-env-ref-name>jndi/default</resource-env-ref-name>
   <resource-env-ref-type>javax.naming.Context</resource-env-ref-type>
</resource-env-ref>
...
```

## Déclaration jndi-resource ##
Les métas-données pour décrire les exigences de la ressource doivent être
exprimées dans le fichier [jndi-resources.xml](jndiResourcesXML.md). Par exemple :
```
...
<resource name="jndi-web-sample/jndi/default" familly="jndi/default" />
<resource name="jndi-web-sample/jndi/default" familly="jndi/jboss">
java.naming.provider.url=
jnp.partitionName=${jndi.jboss.partitionName}
</resource>
...
```

## Propriétés ##
Voici les propriétés optionnelles que vous pouvez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).

| **Famille JNDI** | |
|:-----------------|:|
| **Propriété** | **Description** |
| `archetype` | Indique l'archétype [Maven](http://maven.apache.org) du _driver_ à utiliser. La valeur doit suivre le format suivant : `<groupe>:<archetype>:<version>`. La valeur par défaut dépend de la famille utilisée. |

## Extensions et limitations ##
Pour ajouter des paramètres spécifiques à un serveur d'applications, il faut
ajouter à la déclaration de ressources, des informations complémentaires dont voici des exemples.
```
...
<resource name="jndi-web-sample/jndi/jboss" familly="jndi/jboss">
  <extends appsrv="jboss4_x">
java.naming.factory.url.pkgs=org.jnp.interfaces
jnp.partitionName=jndi.jboss.partitionName
  </extends>
</resource>
...
```

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `jndi/default` | Propose un lien vers un annuaire JNDI de la même famille que celle du serveur d'application. |
| `jndi/cos` | Propose un lien vers un annuaire d'objets CORBA. |
| `jndi/dns` | Permet un accès à un serveur DNS. |
| `jndi/file` | Permet un accès à un répertoire comme annuaire.|
| `jndi/jboss` | Offre la manipulation d'un annuaire JNDI JBoss distant. |
| `jndi/ldap` | Manipule un annuaire LDAP. |
| `jndi/rmi` | Manipule un annuaire RMI. |
| `jndi/activemq` | Manipule un annuaire JNDI publié par [ActiveMQ](ActiveMQ.md). |

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| **`${jndi.url}`** | L'URL de connexion à un annuaire de la même marque que le serveur d'application. La valeur par défaut dépend de chaque serveur. |
| `${jndi.jboss.url}` | L'URL de connexion à un annuaire JBoss. La valeur par défaut est : `${jndi.url}` |
| `${jndi.jboss.partitionName}` | Indique le nom de la partition. La valeur par défaut est : `DefaultPartition` |
| `${jndi.dns.authoritative}` | Permet de vérifier que chaque réponse DNS émane de l'autorité compétente, et non d'un cache. La valeur par défaut est : `false` |
| `${jndi.dns.lookup.attr}` | « _Before JNDI invokes an object factory on a DNS context, it by default reads and passes to the factory any internet TXT attributes of the context. This property, if set, names an alternate attribute identifier to use._ » La valeur par défaut est : `true`|
| `${jndi.dns.recursion}` | Effectue une requête DNS récursive. La valeur par défaut est : `true`|
| `${jnbi.cos.batchsize}` | Indique le nombre d'objet maximum retourné lors d'une requête à un référentiel CORBA. La valeur par défaut est 10. |
| **`${jndi.file.url}`** | L'URL référençant la racine du répertoire à consulter. Il n'y a pas de valeur par défaut. |
| **`${jndi.ldap.url}`** | L'URL de connexion à un annuaire LDAP. Il n'y a pas de valeur par défaut. |
| `${jndi.ldap.security.authentication}` | Le type d'authentification a l'annuaire LDAP. La valeur par défaut est : `simple`|
| **`${jndi.ldap.security.principal}`** | L'identité de connexion a l'annuaire LDAP. Il n'y a pas de valeur par défaut.|
| **`${jndi.ldap.security.credentials}`** | Le mot de passe pour la connexion LDAP. Il n'y a pas de valeur par défaut.|
| `${jndi.ldap.batchsize}` | Indique le nombre d'objet maximum retourné lors d'une requête à un référentiel CORBA. La valeur par défaut est 10.|
| `${jndi.activemq.url}` | Indique l'URL de connection au serveur ActiveMQ. La valeur par défaut est [${jms.activemq.url}](jms.md) |

[Les autres ressources](ressources.md)