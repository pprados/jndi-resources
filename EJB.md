# Utilisation avec des EJB #
Les EJB ont deux facettes : le client et le serveur.
Les deux composants peuvent être installés dans le même serveur d'applications ou
dans des serveurs différents. Les annuaires des deux serveurs peuvent être
[séparées ou unifiés](architecture.md).

## Serveur EJB ##
Un composant EAR qui publie des EJBs doit les déclarer dans le fichier `ejb-jar.xml`.
```
<ejb-jar
  id="compta" 
>
  ...
  <display-name>jndi-ejb-sample</display-name>
  <enterprise-beans>
    <session>
      <display-name>Lookup JNDI</display-name>
      <ejb-name>Paye</ejb-name>
      <home>fr.prados.PayeHome</home>
      <remote>fr.prados.Paye</remote>
      <ejb-class>fr.prados.PayeSession</ejb-class>
      <session-type>Stateless</session-type>
      <transaction-type>Container</transaction-type>
    </session>
  <enterprise-beans>
<ejb-jar>
```
Notez la valeur du paramètre `id` du marqueur `<ejb-jar/>`. Il correspond
à l'identifiant du composant.

Nous souhaitons bénéficier de la normalisation du packaging du composant,
à savoir, la possibilité de l'utiliser sur tous les serveurs d'applications,
indépendamment de sa marque, de sa version ou de son architecture.
C'est bien l'objectif de la normalisation [EAR](EAR.md).

Suivant les serveurs d'applications, il faut ajouter [différents](transformerWebXML.md)
fichiers permettant d'associer la ressource `java:comp/env/ejb/Paye` avec une autre clef JNDI.

Pour JBoss, il faut ajouter le fichier `META-INF/jboss.xml` suivant :
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE jboss
  PUBLIC "-//JBoss//DTD JBOSS 3.0//EN" "http://www.jboss.org/j2ee/dtd/jboss_3_0.dtd">
<jboss>
   <enterprise-beans>
      <session>
         <ejb-name>Paye</ejb-name>
         <jndi-name>compta/ejb/Paye</jndi-name>
    </session>
  </enterprise-beans>
</jboss>
```

L'archive ainsi produite est alors packagée dans un [EAR](EAR.md).

## Client d'EJB ##
L'annuaire JNDI de la Compta est généralement distant de l'annuaire de l'Intranet
mais peut également être sur [le même](architecture.md).
Cela dépend de l'architecture choisie lors du déploiement, et des choix de
mutualisations des ressources.

Coté client, on retrouve dans le fichier `web.xml`, le besoin d'avoir une référence sur l'EJB.
```
<web-app id="intranet">
...
  <ejb-ref>
    <description>L'EJB de paye</description>
    <ejb-ref-name>ejb/Paye</ejb-ref-name>
    <ejb-ref-type>Session</ejb-ref-type>
    <home>fr.prados.PayeHome</home>
    <remote>fr.prados.Paye</remote>
  </ejb-ref>
</web-app>
```

Les fichiers spécifiques aux différents serveurs d'applications peuvent être
générés à l'aide de [filtre XSLT](transformerWebXML.md).
L'idée est de généré un mapping systématique entre un nom de la branche `java:comp/env/...`
vers un nom de la forme `java:<id>....` Par exemple, le fichier `jboss-web.xml` peut être le suivant :
```
<jboss-web>
...
   <ejb-ref>
      <ejb-ref-name>ejb/Paye</ejb-ref-name>
      <jndi-name>java:/intranet/ejb/Paye</jndi-name>
   </ejb-ref>
</jboss-web>
```

Il faut maintenant déclarer les besoins en ressources dans le fichier `jndi-resources.xml`.
Nous souhaitons un lien vers l'annuaire distant de la Compta, puis un lien entre la clef
locale `java:/intranet/ejb/Paye` et l'EJB de Paye.
```
<!-- Lien vers l'annuaire JNDI de la compta -->
<resource name="/ServiceCompta" familly="jndi/default">
java.naming.provider.url=${jndi-compta}
</resource>

<!-- Lien d'une clef locale vers la clef de l'annuaire de la compta -->
<resource name="intranet/ejb/Paye" familly="link/default">
 <property name="link" value="/ServiceCompta/compta/ejb/LookupJndi"/>
</resource>
```

Rien n'indique la location du serveur de la compta, s'il y a utilisation du
[même annuaire ou de deux annuaires](architecture.md). Lors de l'installation les
choix de l'architecture de déploiement seront
effectué. Par exemple, lors de l'installation du client de l'EJB, il faut
valoriser la variable `jndi-compta`.
```
>$JNDI_HOME/bin/jndi-install \
        --appsrv jboss --version 5.0 \
        --package ./package \
        --dest jboss.server.home=/opt/jboss-5.0
        --dest jboss.server.conf=/opt/jboss-5.0/server/default \
        -P platform.properties
        -D jndi-compta=jnp://comptahost:1099
```

Avec les sources, vous trouverez un exemple d'utilisation d'EJB en combinaison avec **jndi-resources**.

Voir aussi : [EAR](EAR.md)

Précédent : [Exemple](exemple.md)
Suite : [Les familles de ressources](familles.md)