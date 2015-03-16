![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/jndi-resources.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/jndi-resources.png)

# "Écrit une fois, installé partout" #

## Vous avez des difficultés à paramétrer les Datasources ou les files JMS pour JBoss ou Tomcat ? Vous ne comprenez rien aux paramétrages des serveurs d'applications ?  JNDI-Resources est fait pour vous. ##


**jndi-resources** est un utilitaire qui permet aux composants JEE d'[exprimer](jndiResourcesXML.md) leurs besoins en terme de [ressources](ressources.md) [JNDI](architecture.md) ([DataSource](jdbc.md), [JMS](jms.md), [URL](url.md), [Javamail](mail.md), [Host](host.md), [annuaires](jndi.md), etc.) et de [générer](ligneDeCommandes.md) automatiquement tous les fichiers de configuration permettant leurs publications dans [tous les serveurs d'applications](transformations.md). Ainsi, les composants WAR ou EAR peuvent être déployés automatiquements sur tous les serveurs, sans entrer dans les documentations techniques souvent incomplètes ou dans la génération de script d'installation spécifique à une marque de serveur particulier.

Après le concept "_Écrit une fois, utilisé partout_", nous ajoutons le maillon : "_Écrit une fois, installé partout_". Voilà ce que propose **jndi-resources**.

Les bénéfices attendus de la démarche sont les suivants :
  * Indépendance des composants vis-à-vis des serveurs d'applications ;
  * Migration immédiate d'un serveur à un autre, ou d'une version à la suivante ;
  * Normalisation de l'utilisation des ressources de l'entreprise ;
  * Qualifications des paramètres de déploiements ;
  * Normalisation de l'installation des composants, quelques soit le serveur d'applications ;
  * Possibilité de mutualiser les ressources au niveau serveurs d'applications, instances de serveurs d'applications ou machines virtuelles ;

Vous développez sous Tomcat et déployé sous JBoss ? Vous avez des difficultés lors des déploiements des composants, de la mutualisation des ressources ? **jndi-resource** est fait pour vous.

Suivez l'[introduction](introduction.md) pour avoir plus d'informations,
pratiquez [l'exemple rapide](exemple.md)
ou consultez le fonctionnement des moteurs d'[installation](moteurInstallation.md) et de [configuration](moteurConfiguration.md).

Le tableau suivant reprend les différentes ressources gérées par **jndi-resources**.
![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/matrice.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/matrice.png)

  * HSQLDB n'est pas compatible XA. Une approche est d'utiliser [Atomikos](http://www.atomikos.com/products/transactionsJTA/overview.html) ;
  * JBoss 3.2 ne sait pas déclarer une DataSource sur un annuaire JNDI distant ;
  * JBossMQ doit pouvoir être utilisé avec les anciennes versions de JBoss, mais ce n'est pas intégré pour le moment ;