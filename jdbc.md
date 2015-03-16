# Famille JDBC #

La famille `jdbc` permet de décrire une source de données de type SQL.

## Déclaration JEE ##
La ressource doit être déclarée dans le fichier `web.xml` ou `ejb-jar.xml`.
```
...
<resource-ref>
  <description>Source de données SQL</description>
  <res-ref-name>jdbc/MaDataSource</res-ref-name>
  <res-type>javax.sql.DataSource</res-type>
  <res-auth>Application</res-auth>
  <res-sharing-scope>Shareable</res-sharing-scope>
</resource-ref>
...
```

## Déclaration jndi-resource ##
Les métas-données pour décrire les exigences de la ressource doivent être
décrites dans le fichier [jndi-resources.xml](jndiResourcesXML.md). Par exemple :
```
...
<resource
  name="jndi-web-sample/jdbc/MaDataSource"
  familly="jdbc/default">
  <property name="archetype" value="hsqldb:hsqldb:1.8.0.7"/>
  <property name="xa" value="no"/>
  <property name="min-pool-size" value="5"/>
  <property name="max-pool-size" value="20"/>
  <property name="prepared-statement-cache-size" value="32"/>
</resource>
...
```

## Propriétés ##
Voici les propriétés optionnelles que vous pouvez ajouter dans le fichier
[jndi-resources.xml](jndiResourcesXML.md) pour décrire la ressource.
| **Famille jdbc** | |
|:-----------------|:|
| **Propriété** | **Description** |
| `archetype` | Indique l'archétype [Maven](http://maven.apache.org) du _driver_ à utiliser. La valeur doit suivre le format suivant : `<groupe>:<archetype>:<version>`. La valeur par défaut dépend de la famille utilisée. |
| `xa` | Permet d'indiquer l'impact de la transaction courante sur la source de données. Trois valeurs sont possibles : `local` pour utiliser une transaction spécifique lors de l'utilisation de la ressource ; `no` pour exclure la ressource de la transaction courante ; `yes` pour inclure la ressource dans la transaction courante. La valeur par défaut est `local`. Notez que certains serveurs ne savent pas traiter les transactions et ne gèrent que le mode `local`. |
| `connection-url` | Permet d'indiquer l'URL de connections. Il n'est pas recommandé de valoriser cette propriété avec des valeurs en dur, car cela crée une adhérence avec la plate-forme de déploiement. Il est préférable d'utiliser des variables. La valeur par défaut dépend du type de base de données utilisée. |
| `username` | Le nom de l'utilisateur de la base de données. Il n'est pas recommandé de valorisé cette propriété. La valeur par défaut est `${jdbc.username}`. |
| `password` | Le mot de passe de l'utilisateur de connexions à la base de données. Il n'est pas recommandé de valoriser cette propriété. La valeur par défaut est `${jdbc.password}`. |
| `min-pool-size` | Indique la taille minimum du pool de connexion. La valeur par défaut est `${jdbc.min-pool-size}`. |
| `max-pool-size` | Indique la taille maximum du pool de connexion. La valeur par défaut est `${jdbc.max-pool-size}`. |
| `prepared-statement-cache-size` | Indique la taille du cache de prepared-statements. La valeur par défaut est `${jdbc.prepared-statement-cache-size}` |
| `max-wait` | Indique le temps maximum avant de considérer la requête comme échouée. La valeur par défaut est `${jdbc.max-wait}` |
| `validation-query` | Indique la requête SQL à invoquer pour vérifier la connexion à la base de données. La valeur par défaut dépend du type de la base de donnée. |

## Extensions et limitations ##
Pour ajouter des paramètres spécifiques à un serveur d'applications, il faut
ajouter à la déclaration de ressources, des informations complémentaires dont voici un exemple.
```
...
<resource
  name="jndi-web-sample/jdbc/MaDataSource"
  familly="jdbc/oracle"
>
  <property name="xa" value="no"/>
    <extends appsrv="tomcat5_5">
      <attribute name="removeAbandonedTimeout">60</attribute>
    </extends>
    <extends appsrv="jboss3_x">
      <backgroundValidationFrequencySeconds>
      900
      </backgroundValidationFrequencySeconds>
    </extends>
  </extends>
</resource>
...
```

## Déclinaisons ##
Les déclinaisons possibles pour le paramètre [familly](familles.md) du fichier
[jndi-resources.xml](jndiResourcesXML.md) sont les suivantes :
| **Famille** | **Description** |
|:------------|:----------------|
| `jdbc/default` | Utilise la base de données par défaut du serveur d'applications. Pour JBoss, il s'agit de la base décrite sous `java:DefaultDS`. C'est une base HSQLDB, entièrement codée en Java et s'exécutant dans le même processus. Pour l'architecture JBoss avec un annuaire JNDI déporté, une base HSQLDB est lancée en mode serveur à coté de l'annuaire JNDI de référence. Cette base est utilisée par les clients de l'annuaire JNDI. Pour Tomcat, il s'agit également d'une base HSQLDB dont il faut préciser l'utilisateur, le mot de passe et le fichier. |
| `jdbc/hsqldb` | Utilise une base de données de type [HSQLDB](http://www.hsqldb.org) in process. Pour l'architecture JBoss avec un annuaire JNDI déporté, une base HSQLDB est lancée en mode serveur à coté de l'annuaire JNDI de référence. Cette base est utilisée par les clients de l'annuaire JNDI. |
| `jdbc/mysqsl` | Utilise une base de données [MySql](http://www-fr.mysql.com/).|
| `jdbc/postgresql` | Utilise une base de données [PostgreSQL](http://www.postgresql.org/).|
| `jdbc/oracle` | Utilise une base de données [Oracle](http://www.oracle.com/global/fr/index.html) |

## Variables ##
Les variables à valoriser lors de l'installation pour gérer ces ressources sont les suivantes :
| **Variable** | **Description** |
|:-------------|:----------------|
| **`${jdbc.host}`** | Le host de la base de données. Il n'y a pas de valeur par défaut. Cette variable doit être valorisée lors de l'installation. |
| **`${jdbc.username}`** | L'utilisateur de connexion. Il n'y a pas de valeur par défaut. Cette variable doit être valorisée lors de l'installation. |
| **`${jdbc.password}`** | Le mot de passe de connexion. Il n'y a pas de valeur par défaut. Cette variable doit être valorisée lors de l'installation. |
| `${jdbc.min-pool-size}` | Valeur par défaut : 5 |
| `${jdbc.max-pool-size}` | Valeur par défaut : 20 |
| `${jdbc.prepared-statement-cache-size}` | Valeur par défaut : 32 |
| `${jdbc.max-wait}` | Valeur par défaut : -1 |
| `${jdbc._<db>_.username}` | Valeur par défaut : `${jdbc.username}` |
| `${jdbc._<db>_.password}` | Valeur par défaut : `${jdbc.password}` |
| `${jdbc._<db>_.driver}` | Le driver de la base de données associé (mysql, oracle, etc.) |
| `${jdbc._<db>_.port}` | Le port d'accès à la base de données. |
| `${jdbc._<db>_.url.default}` | L'URL de connexion par défaut. |
| `${jdbc.xa._<db>_.driver}` | Le driver de la base de données pour une connexion XA. |
| `${jdbc.xa._<db>_.url.default}` | L'URL de connexion par défaut en cas de connexion XA. |
| `${jdbc._<db>_.validation-query}` | La requête SQL de validation de la connexion. |

Chaque base de données définit ses variables.
| **HSQLDB** | |
|:-----------|:|
| **Variable** | **Description** |
| **`${jdbc.hsqldb.file}`** | Le fichier utilisé par la base HSQLDB. Il n'y a pas de valeur par défaut. Cette variable doit être valorisée lors de l'installation si nécessaire. |
| `${jdbc.hsqldb.driver}` | Valeur : `org.hsqldb.jdbcDriver` |
| `${jdbc.hsqldb.url.default}` | La valeur dépend du serveur d'application cible. Pour JBoss : `jdbc:hsqldb:file:${jboss.server.data.dir}${/}hypersonic${/}${jdbc.hsqldb.file}`|
| `${jdbc.hsqldb.port}` | Valeur : 1701 |
| `${jdbc.hsqldb.validation-query}` | Valeur : `/**/` |
| `${jdbc.hsqldb.username}` | Valeur : `${jdbc.username}` |
| `${jdbc.hsqldb.password}` | Valeur : `${jdbc.password}` |

| **MySQL** | |
|:----------|:|
| **Variable** | **Description** |
| **`${jdbc.mysql.schema}`** | Le schéma de la base [MySql](http://www.mysql.org). Il n'y a pas de valeur par défaut. Cette variable doit être valorisée lors de l'installation si nécessaire. |
| `${jdbc.mysql.driver}` | Valeur : `com.mysql.jdbc.Driver` |
| `${jdbc.mysql.port}` | Valeur : 3306 |
| `${jdbc.mysql.url.default}` | Valeur : `jdbc:mysql://${jdbc.host}:${jdbc.mysql.port}/${jdbc.mysql.schema}` |
| `${jdbc.mysql.username}` | Valeur : `${jdbc.username}` |
| `${jdbc.mysql.password}` | Valeur : `${jdbc.password}` |
| `${jdbc.mysql.xa.driver}` | Valeur : `com.mysql.jdbc.jdbc2.optional.MysqlXADataSource` |
| `${jdbc.mysql.xa.url.default}` | Valeur : `jdbc:mysql://${jdbc.host}:${jdbc.mysql.port}/${jdbc.mysql.schema}` |
| `${jdbc.mysql.validation-query}` | Valeur : `select '1'` |

| **PostgreSQL** | |
|:---------------|:|
| **Variable** | **Description** |
| **`${jdbc.postgresql.schema}`** | Le schéma de la base [PostgreSQL](http://www.postgresql.org). Il n'y a pas de valeur par défaut. Cette variable doit être valorisée lors de l'installation si nécessaire. |
| `${jdbc.postgresql.driver}` | Valeur : `org.postgresql.Driver` |
| `${jdbc.postgresql.port}` | Valeur : 5432 |
| `${jdbc.postgresql.url.default}` | Valeur : `jdbc:postgresql://${jdbc.host}:${jdbc.postgresql.port}/${jdbc.postgresql.schema}` |
| `${jdbc.postgresql.username}` | Valeur : `${jdbc.username}` |
| `${jdbc.postgresql.password}` | Valeur : `${jdbc.password}` |
| `${jdbc.postgresql.xa.driver}` | Valeur : `org.postgresql.Driver` |
| `${jdbc.postgresql.xa.url.default}` | Valeur : `jdbc:postgres://${jdbc.host}:${jdbc.postgres.port}/${jdbc.postgres.schema}` |
| `${jdbc.postgresql.validation-query}` | Valeur : `select '1'` |

| **Oracle** | |
|:-----------|:|
| **Variable** | **Description** |
| `${jdbc.oracle.driver}` | Valeur : `oracle.jdbc.driver.OracleDriver` |
| `${jdbc.oracle.port}` | Valeur : 1521 |
| `${jdbc.oracle.sid}` | Le « service name » Oracle. Valeur par défaut : XE. |
| `${jdbc.oracle.url.default}` | Valeur : `jdbc:oracle:thin:@(description=(address=(host=${jdbc.host})(protocol=tcp)(port=${jdbc.oracle.port}))(connect_data=(SERVICE_NAME=${jdbc.oracle.sid})))` |
| `${jdbc.oracle.username}` | Valeur : `${jdbc.username}` |
| `${jdbc.oracle.password}` | Valeur : `${jdbc.password}` |
| `${jdbc.oracle.xa.driver}` | Valeur : `oracle.jdbc.xa.client.OracleXADataSource` |
| `${jdbc.oracle.xa.url.default}` | Valeur : `jdbc:oracle:thin:@(description=(address=(host=${jdbc.host})(protocol=tcp)(port=${jdbc.oracle.port}))(connect_data=(SERVICE_NAME=${jdbc.oracle.sid})))`|
| `${jdbc.oracle.validation-query}` | Valeur : `select 1 from dual` |

[Les autres ressources](ressources.md)