Différents filtres XSLT 2.0 sont proposés pour transformer le fichier `web.xml` ou
`ejb-jar.xml` en fichiers nécessaires aux différents serveurs d'applications ou
d'autres transformations.

| **Filtre** | **Conversion** |
|:-----------|:---------------|
| ejb-jar-to-jboss.xslt | ejb-jar.xml vers jboss.xml (Pour JBoss) |
| web-to-jboss-web.xslt | web.xml vers jboss-web.xml (Pour JBoss) |
| web-to-tomcat-context.xslt | web.xml vers context.xml (Pour Tomcat)|
| jndi-resources-to-web2.4.xslt | [jndi-resources.xml](jndiResourcesXML.md) vers web.xml|
| web-to-jndi-resources.xslt | web.xml vers jndi-resources.xml |
| xslt-to-xhtml.xslt | xslt vers xhtml (avec navigation des instructions 

&lt;xsl:include/&gt;

|

# Transformations de web.xml #

Avec [Maven](http://maven.apache.org), il suffit d'indiquer dans le fichier POM, le plugin suivant :
```
<build>
<plugins>
...
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>xml-maven-plugin</artifactId>
	<executions>
		<execution>
			<goals>
				<goal>transform</goal>
			</goals>
		</execution>
	</executions>
	<dependencies>
		<dependency>
			<groupId>net.sf.saxon</groupId>
			<artifactId>saxon</artifactId>
			<version>8.7</version>
		</dependency>
	</dependencies>
	<configuration>
		<transformationSets>
			<!-- JBoss transformation -->
			<transformationSet>
				<dir>
					${project.build.warSourceDirectory}/WEB-INF
				</dir>
				<includes>
					<include>web.xml</include>
				</includes>
				<stylesheet>
					xslt/web-to-jboss-web.xslt
				</stylesheet>
				<outputDir>
					target/${artifactId}-${version}/WEB-INF
				</outputDir>
				<fileMappers>
					<fileMapper
						implementation="org.codehaus.plexus.components.io.filemappers.MergeFileMapper">
						<targetName>
							jboss-web.xml
						</targetName>
					</fileMapper>
				</fileMappers>
			</transformationSet>

			<!-- Tomcat transformation -->
			<transformationSet>
				<dir>
					${project.build.warSourceDirectory}/WEB-INF
				</dir>
				<includes>
					<include>web.xml</include>
				</includes>
				<stylesheet>
					xslt/web-to-tomcat-context.xslt
				</stylesheet>
				<outputDir>
					target/${artifactId}-${version}/META-INF
				</outputDir>
				<fileMappers>
					<fileMapper
						implementation="org.codehaus.plexus.components.io.filemappers.MergeFileMapper">
						<targetName>context.xml</targetName>
					</fileMapper>
				</fileMappers>
			</transformationSet>

		</transformationSets>
	</configuration>
</plugin>
...
</plugins>
</build>
```

Les transforamtions WAS ne fonctionnent qu'a condition d'avoir un attribut `id` pour chaque ressource.

# Transformations de ejb-jar.xml #

Avec [Maven](http://maven.apache.org), il suffit d'indiquer dans le fichier POM, le plugin suivant :
```
<build>
<plugins>
...
  <plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>xml-maven-plugin</artifactId>
    <executions>
      <execution>
        <goals>
          <goal>transform</goal>
        </goals>
      </execution>
    </executions>
    <dependencies>
      <dependency>
        <groupId>net.sf.saxon</groupId>
        <artifactId>saxon</artifactId>
        <version>8.7</version>
      </dependency>
    </dependencies>
    <configuration>
      <transformationSets>
        <transformationSet>
          <dir>${project.build.sourceDirectory}/../resources/META-INF</dir>
          <includes>
            <include>ejb-jar.xml</include>
          </includes>
          <stylesheet>ejb-jar-to-jboss.xslt</stylesheet>
          <outputDir>target/classes/META-INF</outputDir>
          <fileMappers>
            <fileMapper implementation="org.codehaus.plexus.components.io.filemappers.MergeFileMapper">
              <targetName>jboss.xml</targetName>
            </fileMapper>
          </fileMappers>
       </transformationSet>
    </transformationSets>
  </configuration>
</plugin>
...
</plugins>
</build>
```

Suite : [Exemple rapide](exemple.md)