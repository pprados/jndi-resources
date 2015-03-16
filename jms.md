# Famille JMS #

La famille `jms` permet de demander l'installation de _factories_, de _queue_ ou de
_topic_ pour envoyer ou recevoir des messages.

## Déclaration JEE ##
Les ressources doivent être déclarées dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-ref>
   <description>Serveurs de queues</description>
   <res-ref-name>jms/ConnectionFactory</res-ref-name>
   <res-type>javax.jms.ConnectionFactory</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>

<resource-ref>
   <description>Serveurs de queues</description>
   <res-ref-name>jms/XAConnectionFactory</res-ref-name>
   <res-type>javax.jms.XAConnectionFactory</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>

<resource-env-ref>
   <description>Une queues</description>
   <resource-env-ref-name>jms/Queue</resource-env-ref-name>
   <resource-env-ref-type>javax.jms.Queue</resource-env-ref-type>
</resource-env-ref>

<resource-env-ref>
   <description>Un Topic</description>
   <resource-env-ref-name>jms/Topic</resource-env-ref-name>
   <resource-env-ref-type>javax.jms.Topic</resource-env-ref-type>
</resource-env-ref>
...
```

## Déclaration jndi-resource ##

Les métas-données pour décrire les exigences de la ressource doivent être décrite
dans le fichier [jndi-resources.xml](jndiResourcesXML.md).
Il existe plusieurs déclinaisons de ressources. Le suffixe doit être `cf`, `queue` ou `topic`.

Par exemple :
```
...
<resource name="JNDI-test/jms/ConnectionFactory" familly="jms/default/cf"/>

<resource name="JNDI-test/jms/XAConnectionFactory" familly="jms/default/cf">
	<property name="xa" value="true"/>
</resource>

<resource name="JNDI-test/jms/DeadLetterQueue" familly="jms/default/queue"/>
<resource name="JNDI-test/jms/ExpiryQueue"     familly="jms/default/queue"/>

<resource name="JNDI-test/jms/Queue"           familly="jms/default/queue"/>
<resource name="JNDI-test/jms/QueueSpec"       familly="jms/default/queue">
	<property name="dead-letter-queue"     value="JNDI-test/jms/DeadLetterQueue"/>
	<property name="expiry-queue"          value="JNDI-test/jms/ExpiryQueue"/>
	
	<property name="redelivery-delay"      value="0"/>
	<property name="max-delivery-attempts" value="-1"/>
	<property name="max-size"              value="-1"/>
	<property name="message-counter-history-day-limit" value="-1"/>
	<property name="preserve-ordering"     value="false"/>
</resource>
	
<resource name="JNDI-test/jms/QueueSpec"       familly="jms/default/queue">
	<property name="dead-letter-queue"     value="JNDI-test/jms/DeadLetterQueue"/>
</resource>

<resource name="JNDI-test/jms/Topic"           familly="jms/default/topic"/>
...
```

## Propriétés ##
Voici les propriétés optionnelles que vous pouvez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md).
| **Famille jms/_xxx_/cf |**|
|:|:|
| **Propriété** | **Description** |
| `archetype` | Indique l'archétype [Maven](http://maven.apache.org) du _driver_ à utiliser. La valeur doit suivre le format suivant : `<groupe>:<archetype>:<version>`. La valeur par défaut dépend de la famille utilisée. |
| `xa` | Indique si la manipulation de la ressource fait partie d'une transaction ou non. La valeur par défaut est : `false`. |

| **Famille jms/_xxx_/topic |**|
|:|:|
Les propriétés optionelles pour topic :
| **Propriété** | **Description** |
| `dead-letter-queue` | La référence à une queue pour mémoriser les messages n'ayant pu être délivrés. Il n'y a pas de valeur par défaut. Dans ce cas, la stratégie du serveur JMS s'applique. |
| `expiry-queue` | La référence à une queue pour mémoriser les messages trop anciens. Il n'y a pas de valeur par défaut. Dans ce cas, la stratégie du serveur JMS s'applique. |
| `redelivery-delay` | Le délais avant de tenter de livrer le message. La valeur par défaut est : `${jms.redelivery-delay}` |
| `message-counter-history-day-limit` | La durée de vie maximum des messages. La valeur par défaut est : `${jms.message-counter-history-day-limit}`|
| `full-size` | Le nombre maximum de messages dans la queue. La valeur par défaut est : `${jms.full-size}`|

## Extensions et limitations ##
Pour ajouter des paramètres spécifiques à un serveur d'application, il faut
ajouter à la déclaration de ressources, des informations complémentaires dont
voici des exemples.
```
...
<resource name="jndi-web-sample/jms/Queue" familly="jms/default">
  <property name="xa" value="true"/>
  <extends appsrv="jboss5_x-jms-srv">
    <attribute name="SecurityConfig">
      <security>
        <role name="guest" read="true" write="true" />
        <role name="publisher" read="true" write="true"
              create="false" />
        <role name="durpublisher" read="true" write="true"
              create="true" />
      </security>
    </attribute>
  </extends>
</resource>
...
```
Notez que les files JbossMQ ne savent pas gérer des files de messages morts
spécifiques à chaque files. Le paramètre `dead-letter-queue` est alors ignoré.

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `jms/default` | Utilise les _drivers_ par défauts du serveur d'application. |
| `jms/jboss` | Utilise les _drivers_ du serveur JMS du JBoss courant (JBoss MQ ou JBoss Messaging suivant les cas). |
| `jms/jbossmq` | Utilise spécifiquement un serveur JMS JBoss MQ. |
| `jms/jboss-messaging` | Utilise spécifiquement un serveur JMS JBoss Messaging. |
| `jms/activemq` | Utilise spécifiquement un serveur JMS inclus dans le serveur d'application [ActiveMQ](ActiveMQ.md). |
| `jms/remote-activemq` | Utilise spécifiquement un serveur JMS [ActiveMQ](ActiveMQ.md) via son annuaire JNDI. |

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| `${jms.full-size}` | Le nombre maximum de message dans la queue. Valeur par défaut : 200000 |
| `${jms.redelivery-delay}` | Le délais avant de ré-émettre le message. Valeur par défaut : 0 |
| `${jms.message-counter-history-day-limit}` | La durée de vie maximum des messages dans la queue. Valeur par défaut : -1 |
| `${jms.activemq.url}` | L'URL de connexion. Valeur par défaut : `vm://localhost,tcp://${jms.activemq.host}:${jms.activemq.port}` |
| `${jms.activemq.port}` | Le port du serveur. Valeur par défaut : 61616 |
| `${jms.activemq.host}` | Le host du serveur ActiveMQ. Valeur par défaut : localhost |
| `${jms.activemq.url.vm}` | L'URL de connexion locale. Valeur par défaut : vm://localhost |
| `${jms.activemq.url.tcp}` | L'URL de connexion tcp. Valeur par défaut : tcp://${jms.activemq.host}:${jms.activemq.port} |
| `${jms.activemq.url}` | L'URL de connexion. Valeur par défaut : ${jms.activemq.url.vm},${jms.activemq.url.tcp} |

Références :
  * http://labs.jboss.com/file-access/default/members/jbossmessaging/freezone/docs/userguide-1.4.0.SP2/html/configuration.html#conf.destination.queue
  * http://wiki.jboss.org/wiki/Wiki.jsp?page=ConfigTopic

[Les autres ressources](ressources.md)