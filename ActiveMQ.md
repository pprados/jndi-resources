_TODO : Il faut revoir la liste des paramètres pour les files.
Déplacer le dead-letter-queues en paramètre spécifique ?
Voir les paramètres de ActiveMQ. Facile à modifier dans l'URL ou les properties._

Il existe plusieurs architectures pour utiliser un serveur [ActiveMQ](http://activemq.apache.org).
  * Utilisation d'un RAR pour intégrer ActiveMQ directement dans le serveur d'application ;
  * Utilisation d'un serveur ActiveMQ autonome et l'annuaire JNDI spécifique publié par ActiveMQ ;

# Utilisation d'un RAR #
ActiveMQ publie un composant RAR, permettant une intégration d'une queue ActiveMQ
dans le serveur d'application. Ce composant lance une tâche s'occupant des queues
et publie des ConnectionFactories pour permettre l'intégration standards des files dans
le serveur d'application, en partageant la JVM.

### Utilisation sous JBoss ###
Avec JBoss, les ressources
exposées par un composant RAR ne peuvent être visibles que dans le contexte `java:`.
Il n'est donc pas possible de publier les ressources ActiveMQ dans un annuaire JNDI centralisé.

Pour contourner la difficulté, chaque client doit également être serveur ActiveMQ.
Pour pouvoir faire cela, il faut suivre la procédure indiqué
[ici](http://activemq.apache.org/integrating-apache-activemq-with-jboss.html).
Suivez les étapes jusqu'a "**Configuring JBoss**". Il n'est pas nécessaire de construire le fichier
`activemq-jms-ds.xml` puisqu'il sera généré par **jndi-resources** suivant les besoins.

Vous pouvez ensuite utiliser la [famille](jms.md) `jms/activemq`.
Rien ne sera généré pour les [cibles](transformations.md) `jboss-jms-srv` et `jboss-jndi-srv`.
Par contre, les cibles `jboss` et `jboss-jndi-cli` génèrent la publication des ressources dans le contexte `java:`.

Les serveurs JMS sont découvert dynamiquement par ActiveMQ.

# Utilisation de l'annuaire JNDI d'ActiveMQ #

La famille `jms/remote-activemq` utilise l'annuaire JNDI d'ActiveMQ pour référencer les ressources.
Sous la clef `_remoteActiveMQ`, est montée le contexte de l'annuaire JNDI distant.
Puis, des alias de clefs permettent de publier les ressources de l'annuaire ActiveMQ,
directement dans l'annuaire du serveur d'application. Ainsi, il est facile de mapper
les clefs de contexte `java:`, vers des clefs d'ActiveMQ. Cela en toute transparence de l'application.

Ainsi, les ressources publiés par ActiveMQ peuvent être référencées par un annuaire JNDI global.

Vous pouvez simuler le même comportement à l'aide du fichier [jndi-resources.xml](jndiResourcesXML.md).
```
<resource 
  name="jndi-web-test/jms/_remoteActiveMQ" 
  familly="jndi/activemq"/>

<resource name="jndi-web-test/jms/ConnectionFactory" familly="link/default">
 <property name="link" value="java:jndi-web-test/jms/_remoteActiveMQ/ConnectionFactory"/>
</resource>
<resource name="jndi-web-test/jms/Queue" familly="link/default">
 <property name="link" value="java:jndi-web-test/jms/_remoteActiveMQ/Queue"/>
</resource>
<resource name="jndi-web-test/jms/Topic" familly="link/default">
 <property name="link" value="java:jndi-web-test/jms/_remoteActiveMQ/Topic"/>
</resource>
```