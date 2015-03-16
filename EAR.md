# Utilisation des EAR #

Les composants EAR peuvent agréger différents composants : plusieurs EJB-JAR,
plusieurs WAR, etc. L'attribut `<ejb-link/>` des spécifications JEE permet de
lier les composants entres eux dans le même EAR.

Pour lier les différents composants avec des ressources ou des EJB externes,
il faut rédiger un fichier [META-INF/jndi-resources.xml](jndiResourcesXML.md) et
le placer éventuellement dans l'EAR. Ce fichier doit faire la synthèse des
besoins en ressources des différents composants de l'EAR.

En respectant les [conventions](conventions.md) de mapping des clefs locales (`java:comp/env`),
en utilisant par exemple [les filtres XSLT proposés](transformerWebXML.md),
plusieurs stratégies d'intégrations sont possibles.

Chaque composant doit posséder un identifiant présent dans l'attribut `id`
du marqueur racine de sa description JEE ([&lt;web-app/&gt;](exemple.md) ou
[&lt;ejb-jar/&gt;](EJB.md)). Soit tous les composants de l'EAR partagent le même
identifiant, soit ils utilisent des identifiant différents.

## Partage de l'identifiant ##
S'ils partagent le même identifiant, les composants partagent naturellement
les mêmes ressources. Par exemple, un WAR et un EJB peuvent partager
la même datasource.

Par exemple, avec l'identifiant unique `id="MonApp"`, le fichier
[jndi-resources.xml](jndiResourcesXML.md) ressemble à ceci :
```
<resources 
	xmlns="http://jndi-resources.googlecode.com/1.0/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd"
	id="web-sample"
>
  <resource name="MonApp/jdbc/Default" familly="jdbc/default" />
</resources>

```

Les mappings des clefs locales à chaque composant (`java:comp/env/jdbc/Default`)
pointent vers la même clef globale (`java:MonApp/jdbc/Default`) référençant la
source de données.

## Identifiant spécifique ##
Si chaque composant utilise un identifiant spécifique, le fichier
[jndi-resources.xml](jndiResourcesXML.md) d'intégration doit publier les
ressources pour chaque composant.

Par exemple, un EAR possède un EJB-JAR avec `id="MonApp-ejb"` et un
WAR avec `id="MonApp-war"`. Le fichier [jndi-resources.xml](jndiResourcesXML.md)
ressemble à ceci :
```
<resources 
	xmlns="http://jndi-resources.googlecode.com/1.0/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd"
	id="web-sample"
>
  <!-- Datasource pour l'EJB -->
  <resource name="MonApp-ejb/jdbc/Default" familly="jdbc/default" />

  <!-- Datasource pour le WAR -->
  <resource name="MonApp-war/jdbc/Default" familly="jdbc/default" />
</resources>
```

Deux DataSources sont proposées, sur des clefs JNDI différentes.

Si l'intégrateur de composant souhaite partager les deux datasources afin de
réduire le nombre de connexion à la base de donnée et la consommation mémoire
des caches, il peut rédiger le fichier [jndi-resources.xml](jndiResourcesXML.md) différemment :
```
<resources 
	xmlns="http://jndi-resources.googlecode.com/1.0/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd"
	id="web-sample"
>
  <!-- Datasource pour l'EJB -->
  <resource name="MonApp-ejb/jdbc/Default" familly="jdbc/default" />

  <!-- Lien vers la datasource de l'EJB -->
  <resource name="MonApp-war/jdbc/Default" familly="link/default">
    <property name="link" value="java:MonApp-ejb/jdbc/Default"/>
  </resource>
</resources>
```
Ou bien, pour ne pas privilégier un composant par rapport à un autre :
```
<resources 
	xmlns="http://jndi-resources.googlecode.com/1.0/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd"
	id="web-sample"
>
  <!-- Datasource pour l'EAR -->
  <resource name="MonApp/jdbc/Default" familly="jdbc/default" />

  <!-- Lien vers la datasource de l'EAR -->
  <resource name="MonApp-ejb/jdbc/Default" familly="link/default">
    <property name="link" value="java:MonApp/jdbc/Default"/>
  </resource>

  <!-- Lien vers la datasource de l'EAR -->
  <resource name="MonApp-war/jdbc/Default" familly="link/default">
    <property name="link" value="java:MonApp/jdbc/Default"/>
  </resource>
</resources>
```


Voir aussi : [EJB](EJB.md), [Conventions](conventions.md)