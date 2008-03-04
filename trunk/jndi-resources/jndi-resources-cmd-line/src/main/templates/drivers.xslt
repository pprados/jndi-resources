<?xml version="1.0" encoding="UTF-8"?>

<!-- 
 * Copyright 2008 Philippe Prados.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
-->

<?xml-stylesheet type="text/xsl" href="../../xslt/xslt-to-xhtml.xslt" ?>
<xsl:stylesheet
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
<xsl:variable name="drivers">

	<familly name="javamail">
		<artifact>javax.activation:activation:1.1</artifact>
		<artifact>javax.mail:mail:1.4</artifact>
	</familly>
	
	<familly name="jms" artifact="javax.jms:jms:1.1"/>
	

	<familly name="jdbc/hsqldb" artifact="hsqldb:hsqldb:1.8.0.7"/>
	<familly name="jdbc/oracle" artifact="ojdbc:ojdbc:14"/>
	<familly name="jdbc/mysql"  artifact="mysql:mysql-connector-java:5.1.5"/>

	<familly name="jms/jbossmq" artifact="jboss:jbossmq-client:4.0.2"/>

	<familly name="jndi/default">  
		<artifact>org.jndi-resources:jndi-resources-plugins:*</artifact>
	</familly>

	<familly name="jndi/file">
		<artifact>com.sun.jndi:fscontext:1.2-beta-3</artifact>
		<artifact>com.sun.jndi:providerutil:1.2</artifact>
	</familly>   
	<familly name="jndi/jboss">
	  	<artifact>jboss:jboss-common:4.0.2</artifact>
	  	<artifact>jboss:jnp-client:4.0.2</artifact>
	</familly>

	<familly name="host/default">
	  	<artifact>org.jndi-resources:jndi-resources-plugins:*</artifact>
	 </familly>
	<familly name="mail/email">
	  	<artifact>org.jndi-resources:jndi-resources-plugins:*</artifact>
	</familly>

</xsl:variable>
</xsl:stylesheet>