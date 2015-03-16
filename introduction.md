# Introduction #

Les composants JEE ne doivent pas dépendre d'une architecture de déploiement, ni
avoir connaissance des localisations des différentes ressources qu'ils utilisent.
Ils doivent être indépendants du type de la base de donnée, de sa localisation,
de l'utilisation d'un annuaire [JNDI localisé ou centralisé](architecture.md), du
[type](transformations.md) de serveur d'applications d'accueil (JBoss, Tomcat ou autre), etc.

Les [spécifications](http://java.sun.com/j2ee/j2ee-1_4-fr-spec.pdf) JEE offrent
pour cela une norme de packaging (WAR, EAR, etc.) et une technologie :
[JNDI](rappelJNDI.md). Il s'agit d'un annuaire hiérarchiques d'objets.
Les composants doivent indiquer les ressources dont ils ont besoins ; une
[source de données](jdbc.md), une file [JMS](jms.md), un serveur de [mail](mail.md), une [url](url.md),
un [annuaire](jdbc.md), etc. Le serveur d'applications doit être paramétré pour
offrir les ressources nécessaires au composant.

Mais, comme le paramétrage de ces ressources n'est pas normalisé, chaque serveur
utilise une approche qui lui est propre. Il faut alors écrire des scripts
d'installations spécifique à chaque serveur, voir à chaque version de chaque
serveur. Sinon, le composant JEE ne fonctionne pas, en l'absence des ressources
nécessaires.

Il y a différentes
[familles](http://www.prados.fr/Langage/Java/Parametres/Les%20parametres%20en%20java.pdf)
de paramètres avec Java. Pour respecter les spécifications J2EE, garantir un déploiement
souple, seule les paramètres de déploiements doivent être présent dans l'annuaire JNDI,
mais **tous** les paramètres de déploiements doivent y être.

Les fichiers `web.xml` et `ejb-jar.xml` permettent d'indiquer les ressources nécessaires
aux composants. Il est possible d'indiquer une clef JNDI [locale](rappelJNDI.md) au
composant et une classe Java. Le serveur doit alimenter les clefs avec des objets
répondants aux classes.
```
...
<resource-ref>
   <description>La base de donnée</description>
   <res-ref-name>jdbc/Default</res-ref-name>
   <res-type>javax.sql.DataSource</res-type>
   <res-auth>Application</res-auth>
   <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```

Dans le monde réel, les exigences des projets sont plus complexes. En effet,
le composant doit préciser qu'il désire une source de donnée de type Oracle,
un pool de connexion de telle taille, etc. Mais, il n'a pas besoin d'indiquer
l'utilisateur et le mot de passe de la base de donnée. Pour des raisons de sécurité,
il doit même les ignorer. Ces informations sont du ressort de l'assembleur de
composants, comme décrit dans les [spécifications JEE](http://java.sun.com/j2ee/j2ee-1_4-fr-spec.pdf).

Pour combler ces lacunes, des fichiers README indiquent généralement comment
paramétrer chaque ressource pour chaque serveur. Souvent, cela exige une
intervention manuelle. Ou bien, des scripts d'installations sont proposés,
mais limités à certains serveurs, certaines versions ou à certaines
[architectures](architecture.md). Ils interviennent si profondément, qu'il n'est
plus possible d'installer plusieurs composants sur le même serveur. Le 's' de
serveur d'applications est alors inutile. Chaque serveur ne gère qu'une
seule application.

Vingt-cinq à quarante pour-cent du temps consacré aux déploiements des composants
est perdu à corriger des problèmes de paramètres. Dans un _cluster_, souvent un
serveur fonctionne mais pas l'autre. Où est l'erreur ?

Alors que les salles informatiques cherchent à rationaliser les ressources
(pour des raisons écologiques, financières, surface au sol, limite électrique),
il est difficile de le faire, à cause d'une adhérence trop forte entre les
composants et les serveurs d'applications. Au niveau écologique, ce n'est pas
une solution satisfaisante.

Nous proposons de traiter ce problème, afin d'améliorer et d'uniformiser les
installations des composants, par un paramétrage simplifié des différents
serveurs d'applications.

Les bénéfices attendus de la démarche sont les suivants :
  * Respect du principe : « _Écrit une fois, exécuté partout_ »
  * Indépendance des composants vis-à-vis des serveurs d'applications ;
  * Migration immédiate d'un serveur à un autre ou d'une version à la suivante ;
  * Normalisation de l'installation des composants, quel que soit le serveur d'applications ;
  * Qualifications des paramètres de déploiements ;
  * Normalisation de l'utilisation des ressources de l'entreprise ;
  * Possibilité de mutualiser les ressources au niveau serveurs d'applications, instances de serveurs d'applications ou machines virtuelles ;

Les fondamentaux ayant justifiés la création des composants standards WAR et EAR
seront enfin pris en compte. Sans une démarche cohérente, les archives n'ont de
standard que le nom. Elles ne sont pas capable d'être déployées sur tous serveurs
d'applications, et n'ont alors, plus de raison d'être.

Références :
  * [Les paramètres en Java](http://www.prados.fr/Langage/Java/Parametres/Les%20parametres%20en%20java.pdf) publié en 2003 par Philippe Prados.
  * [Pourquoi utiliser JNDI ?](http://www.prados.fr/Langage/Java/JNDI/JNDI.pdf) publié en 2006 par Philippe Prados.

Suite : [A quoi cela sert ?](aQuoiCelaSert.md)