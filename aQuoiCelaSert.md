# À quoi cela sert ? #

**jndi-resources** est un utilitaire permettant d'installer facilement et uniformément
des composants JEE sur différents serveurs d'applications. Il permet de combler le
maillon manquant des [spécifications JEE](http://java.sun.com/j2ee/j2ee-1_4-fr-spec.pdf),
entraînant que malgré les efforts de normalisation, les paquetages standards WAR, EAR, etc.
ne sont pas utilisables tels quels par les serveurs d'applications.

Sans **jndi-resources**, le déploiement s'effectue à la main.

![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/schema-sans.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/schema-sans.png)

Avec **jndi-resources**, le déploiement est facilité, par la génération des fichiers
de paramétrages pour déclarer les ressources dans l'annuaire JNDI.

![http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/schema-avec.png](http://jndi-resources.googlecode.com/svn/trunk/jndi-resources/src/doc/schema-avec.png)

Les technologies JEE cherchent à attendre le concept "_Écrit une fois, utilisé partout_".
Cela n'est pas tous à fait complet. Il manque le maillon : "_Écrit une fois, installé partout_".
Voilà ce que propose **jndi-resources**.

Précédant : [introduction](introduction.md)
Suite : [Comment faire ?](comment.md)