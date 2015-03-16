# Architectures et serveurs d'applications #

Différentes architectures permettent de gérer les ressources des composants JEE.
Il est possible d'utiliser un annuaire JNDI local à chaque serveur d'applications
(ils en fournissent généralement un par défaut) ou d'utiliser un annuaire JNDI
[centralisé](JBoss.md). Cette dernière approche permet d'éviter les situations
courantes en production où un nœud fonctionne mais pas l'autre dans un cluster.

![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/architecture-jndi.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/architecture-jndi.png)

L'architecture peut être plus complexe et hiérarchique, avec un annuaire par
serveur, un annuaire par application, un annuaire par département et un
annuaire par entreprise.
Les annuaires sont reliés les uns aux autres pour cacher cette organisation
aux composants applicatifs.

![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/arbre-annuaire-jndi.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/arbre-annuaire-jndi.png)

Certains publient des sources de données, d'autres la liste des utilisateurs
sous LDAP. Notez qu'il est possible de mémoriser tous les objets java dans un serveur LDAP.

**jndi-resources** sait prendre en charge ces architectures en [générant](transformations.md)
des fichiers de paramètres adaptés pour l'annuaire JNDI et d'autres pour les
clients des annuaires JNDI.
Ainsi, en modifiant un paramètre sur l'annuaire JNDI centralisé, tous les
clients en bénéficient.
Cette approche garantie une uniformisation des paramètres de déploiement,
dans une architecture à base de grappes ou de fermes. Il est plus facile
d'ajouter un nouveau nœud et de partager des informations entres plusieurs
nœuds (file JMS, tailles de pools, etc.). Cela réduit le risque qu'un nœud
fonctionne et pas un autre, alors qu'ils sont censés être identiques.

Voir aussi : [Le paramétrage JBoss](JBoss.md)

Précédent : [Variables](variables.md)
Suite : [Modèles de transformations](transformations.md)