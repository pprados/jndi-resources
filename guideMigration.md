# Migration d'un paramètre de déploiement #

Si un composant utilise un fichier de propriétés ou une syntaxe XML regroupant
des paramètres de déploiement et des paramètres applicatif, il faut le faire
évoluer pour tenir comptes des spécifications JEE.

Par exemple, le fichier de propriété suivant intègre des paramètres fonctionnels
et des paramètres de déploiement.
```
tva=10.5
host=monserveur
```
La première étape consiste à modifier la valeur de la propriété `host` par une clef
JNDI commençant par `java:comp/env`. Par exemple : `java:comp/env/host/MonHostAContacter`
```
tva=10.5
host=java:comp/env/host/MonHostAContacter
```

Déclarez la clef dans le fichier `web.xml` ou `ejb-jar.xml`.
```
<web-app id="mon-application">
  ...
  <resource-ref >
      <description>Mon host à contacter</description>
      <res-ref-name>host/MonHostAContacter</res-ref-name>
      <res-type>java.net.InetAddress</res-type>
      <res-auth>Application</res-auth>
  </resource-ref>
  ...
</web-app>
```
Ajoutez également le [nécessaire](transformerWebXML.md) dans les fichiers spécifiques
aux serveurs d'applications (`context.xml`, `jboss-web.xml`, etc.)

Modifiez le code exploitant cette information. Par exemple, dans le cas de la lecture
d'une propriété pour obtenir le nom d'un host, la ligne suivante :
```
InetAddress host=InetAddress.getByName(prop.getProperty("host"));
```
doit être modifiée en
```
InetAddress host=new InitialContext().lookup(prop.getProperty("host"));
```

Pour garder une compatibilité ascendante, vous pouvez détecter l'évolution.
```
String hostname=prop.getProperty("host");
InetAddress host;
if (hostname.startWith("java:comp/env")
{
    host=new InitialContext().lookup(hostname);
}
else
{
    log.warn("Deprecated usage of host name in configuration file. Use JNDI key.");
    host=InetAddress.getByName(hostname);
}
```
La dernière étape consiste à [paramétrer](transformerWebXML.md) le serveur d'application
pour qu'il publie cette ressource. C'est ici qu'intervient **jndi-resources**.
Ajoutez dans le fichier [jndi-resources.xml](jndiResourcesXML.md) les lignes suivantes:
```
...
<resource name="mon-application/host/MonHostAContacter" familly="host/default" >
  <property name="host" value="${host.MonHostAContacter}"/>
</resource>
...
```

Lors de l'installation, il faudra valoriser la variable `${host.MonHostAContacter}`
dans la [ligne de commande](ligneDeCommandes.md) ou dans un fichier de propriétés.
```
>$JNDI_HOME/bin/jndi-install ...\
        -Dhost.MonHostAContacter=localhost
```