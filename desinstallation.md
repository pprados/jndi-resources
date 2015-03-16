# Désinstallation des ressources #

Les normes d'installations permettent une désinstallation facile du composant dans
le serveur d'application.

Pour le serveur JBoss, un répertoire est créé par composant, regroupant l'archive
et tous les fichiers de paramètres permettant de publier les ressources. Il porte
comme nom, l'`id` présent dans le fichier [jndi-resources.xml](jndiResourcesXML.md).
Il suffit d'effacer ce répertoire pour désinstaller le composant.

Pour le serveur Tomcat, il faut éditer le fichier `conf/server.xml` et supprimer
les marqueurs dans  `<GlobalNamingResources/>` ayant pour préfixe l'`id` présent
dans le fichier [jndi-resources.xml](jndiResourcesXML.md).

Précédent : [Ligne de Commandes](ligneDeCommandes.md)
Suite : [Valorisation des variables de déploiements](variables.md)