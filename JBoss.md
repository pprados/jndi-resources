# Publication mutualisée de l'annuaire JNDI de JBoss #
JBoss, comme tout serveur d'applications, utilise un annuaire JNDI local. Il est également
capable d'utiliser un annuaire distant pour manipuler les objets de la racine de l'arbre JNDI,
la branche [java:](rappelJNDI.md) restant visible uniquement par l'instance JBoss courante.

Pour faire cela, il faut instancier un serveur JBoss qui ne fait que publier son annuaire.
Les autres instances de JBoss doivent s'y référer pour retrouver les ressources
dont elles ont besoin.

## Publication de l'annuaire ##
Commençons par paramétrer une instance de JBoss pour qu'elle ne soit qu'un annuaire JNDI.
Copiez le répertoire `$JBOSS_HOME/server/minimal` dans le répertoire `$JBOSS_HOME/server/jndi-srv`.
Pour être certains d'utiliser l'annuaire distant, nous allons lui modifier son port
par défaut. Pour cela, éditez le fichier $JBOSS\_HOME/server/jndi-srv/conf/jboss-services.xml.
Dans le paramétrage du `Mbean jboss:service=Naming`, modifiez l'attribut `Port` en `11099`
et l'attribut `RmiPort` en `11098`. De plus, cela permet de tester la configuration sur
la même machine, avec deux instances de JBoss simultanément.
```
<mbean code="org.jboss.naming.NamingService"
   name="jboss:service=Naming"
   xmbean-dd="resource:xmdesc/NamingService-xmbean.xml">
   <attribute name="Port">11099</attribute>
   <attribute name="RmiPort">1098</attribute>
   ...
</mbean>
```

Vous pouvez ensuite lancer une instance publiant un annuaire JNDI global.
```
$JBOSS_HOME/bin/run.sh -c jndi-srv
```

## Client de l'annuaire ##
Sur un nœud client de l'annuaire JNDI, copiez le répertoire `$JBOSS_HOME/server/default`
dans le répertoire `$JBOSS_HOME/server/jndi-cli`. ; modifiez le fichier
`$JBOSS_HOME/conf/jndi.properties` pour ajouter la ligne suivante, avec le nom du
host où est localisé l'annuaire JNDI.
```
java.naming.provider.url=jnp://localhost:11099
```
Vous pouvez ensuite lancer l'instance Jboss.
```
$JBOSS_HOME/bin/run.sh -c jndi-cli
```
Vous pouvez reproduire ceci avec différents nœuds. Ils partageront tous
l'annuaire JNDI global. Vous pouvez ensuite, raffiner le paramétrage pour adapter
la publication des ressources à ce qui est strictement nécessaire localement.

Dans la JMXConsole, le Mbean JNDIView consulte alors l'annuaire distant pour
la branche racine, et l'annuaire local pour la branche [java:](rappelJNDI.md).

Vous pouvez maintenant, publier des ressources JNDI dans l'annuaire global. Elles
seront visibles chez tous les clients JNDI. Utilisez pour cela, les
[cibles](transformations.md) adéquates.

Installation des ressources du composant sur l'annuaire JNDI :
```
>$JNDI_HOME/bin/jndi-install \
        --appsrv jboss-jndi-srv --version 5.0 \
        --package ./packages \
        --dest jboss.server.conf=/opt/jboss-5.0/server/jndi-srv \
        --dest jboss.server.home=/opt/jboss-5.0
```


Installation du composant sur un client de l'annuaire JNDI :
```
>$JNDI_HOME/bin/jndi-install \
        --appsrv jboss-jndi-cli --version 5.0 \
        --package ./packages \
        --dest jboss.server.conf=/opt/jboss-5.0/server/jndi-cli \
        --dest jboss.server.home=/opt/jboss-5.0
```

Voir aussi : [Les modèles de transformations](transformations.md)