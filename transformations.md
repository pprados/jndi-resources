# Les modèles de transformations #

Les [modèles](http://www.prados.fr/mvn/jndi-resources/cmd-line/templates/) de
transformations proposent de gérer les serveurs suivants :
  * `jboss` de la version 3.0 à la dernière à ce jour ( la version 5.x ou suivante si la compatibilité ascendante est conservée).
  * `jboss-jms-srv`, un serveur JBoss jouant le rôle de serveur JMS pour les versions de 3.0 à 5.x.
  * `jboss-jndi-srv`, un serveur [JBoss](JBoss.md) jouant uniquement le rôle de serveur d'annuaire JNDI pour les versions de 3.2 à 5.x.
  * `jboss-jndi-cli` de la version 3.2 à la version 5.x. Un serveur [JBoss](JBoss.md) jouant le rôle d'un client JNDI.
  * `tomcat` de la version 4.0 à la dernière à ce jour, la version 6.x

Certains serveurs peuvent jouer plusieurs rôles simultanément.
Il faut dans ce cas indiquer plusieurs rôles d'installation.
Par exemple, si un serveur JBoss joue le rôle de serveur d'application web et
simultanément de serveur JMS, il faut lancer les installations suivantes :
```
>$JNDI_HOME/bin/jndi-install \
	--appsrv jboss,jboss-jms-srv --version 5.0 \
	--package ./packages \
	--dest jboss.server.conf=/opt/jboss-5.0/server/default \
	--dest jboss.server.home=/opt/jboss-5.0
```

Par défaut, les ressources ne sont pas [publiques](rappelJNDI.md) car portées par
des clefs JNDI de la branche `java:`.
Si les ressources d'un applicatif doivent être disponibles aux clients JNDI, et qu'en plus,
l'instance doit également servir de serveur JMS, vous pouvez installer les
rôles `jboss-jndi-cli`, `jboss-jndi-srv` et `jboss-jms-srv`
dans la même instance JBoss.
```
>$JNDI_HOME/bin/jndi-install \
	--appsrv jboss-jndi-cli,jboss-jndi-srv,jboss-jms-srv --version 5.0 \
	--package ./packages \
	--dest jboss.server.conf=/opt/jboss-5.0/server/default \
	--dest jboss.server.home=/opt/jboss-5.0
```

Les sources des transformations sont [ici](http://www.prados.fr/mvn/jndi-resources/cmd-line/templates/)

D'autres serveurs viendrons s'ajouter à la liste. Vous pouvez participer à
l'[évolution de l'outil](etendre.md)
en proposant des scripts pour d'autres serveurs.

Précédent : [architecture](architecture.md)
Suite : [Description des ressources](jndiResourcesXML.md)