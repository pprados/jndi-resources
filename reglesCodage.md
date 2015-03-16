# Les règles de codage #

Si vous ajoutez de nouvelles ressources ou de nouveaux serveurs d'applications,
vous devez suivre les règles suivantes :
  * Le package d'installation doit être auto-suffisant. Il ne doit pas exiger la présence de [Maven](http://maven.apache.org), ou d'autres utilitaires qui ne sont pas disponibles par défaut dans les serveurs d'applications.
  * Tous les fichiers nécessaires à l'exécution du script d'installation doivent être recopiés dans le package d'installation (feuilles XSLT, etc.)
  * Le JDK nécessaire ne doit pas être supérieur à 1.4, afin de permettre une installation avec de vieux serveurs, toujours en activité.
  * Les nouvelles variables doivent suivre les conventions utilisées pour les autres ressources. Si une variable peut avoir une valeur spécifique, mais également une valeur générique, déclarez la variable spécifique avec comme valeur, le nom de la variable générique ( `jdbc.myds.username=${jdbc.username}` )
  * Partagez vos transformations avec la communauté !

Voir aussi : [Le moteur d'installation](moteurInstallation.md), [Le moteur de configuration](moteurConfiguration.md), [Nouvelle ressource pour Tomcat](extensionTomcat.md), [Nouvelle ressource pour JBoss](extensionJBoss.md)