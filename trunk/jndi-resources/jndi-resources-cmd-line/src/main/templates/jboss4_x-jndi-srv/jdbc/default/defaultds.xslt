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

<?xml-stylesheet type="text/xsl" href="../../../../xslt/xslt-to-xhtml.xslt" ?>
<xsl:stylesheet
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		 http://www.jndi-resources.org/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jndi="http://www.jndi-resources.org/1.0/"
	xmlns:tools="http://www.jndi-resources.org/1.0/java/org.jndiresources.tools.XSLTools"
	exclude-result-prefixes="#all"
>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:include href="../../../templates.xslt"/>

<xsl:variable name="jdbc.username">${jdbc.hsqldb.username}</xsl:variable>
<xsl:variable name="jdbc.password">${jdbc.hsqldb.password}</xsl:variable>

<xsl:variable name="jdbc.driver">${jdbc.hsqldb.driver}</xsl:variable>
<xsl:variable name="jdbc.url.default">jdbc:hsqldb:hsql://${jboss.bind.address}:${jdbc.hsqldb.port}</xsl:variable>
<xsl:variable name="jdbc.url.prefix"/>
<xsl:variable name="jdbc.url.suffix"/>

<xsl:variable name="jdbc.xa.driver"/>
<xsl:variable name="jdbc.xa.url.default"/>
<xsl:variable name="jdbc.xa.url.prefix"/>
<xsl:variable name="jdbc.xa.url.suffix"/>
<xsl:variable name="jdbc.xa.addon"/>

<xsl:variable name="jdbc.addon">
	<track-statements/>
	<metadata>
		<type-mapping>Hypersonic SQL</type-mapping>
	</metadata>
	<depends><xsl:value-of select="$currentid"/>:service=Hypersonic,database=${jdbc.hsqldb.file},host=${jboss.bind.address}</depends>
</xsl:variable>

<xsl:variable name="jdbc.validation-query">${jdbc.hsqldb.validation-query}</xsl:variable>

<xsl:template name="footer">
   <xsl:text>&#xA;   </xsl:text>
   <xsl:text>&#xA;   </xsl:text>
   <mbean
     code="org.jboss.jdbc.HypersonicDatabase"
     name="{$currentid}:service=Hypersonic,database=${{jdbc.hsqldb.file}},host=${{jboss.bind.address}}">
     <attribute name="Port">${jdbc.hsqldb.port}</attribute>
     <!-- <attribute name="BindAddress">${jboss.bind.address}</attribute>-->
     <attribute name="Silent">true</attribute>
     <attribute name="Database">default</attribute>
     <attribute name="Trace">false</attribute>
     <attribute name="No_system_exit">true</attribute>
   </mbean>

	<xsl:call-template name="install-drivers">
		<xsl:with-param name="familly" select="'jdbc/hsqldb'"/>
		<xsl:with-param name="alias" select="hsqldb.jar"/>
	</xsl:call-template>
</xsl:template>

<xsl:variable  name="basefilename"><xsl:value-of select="concat('deploy/',$currentid,'/',$currentid,'_default-ds')"/></xsl:variable>
<xsl:variable  name="filename"><xsl:value-of select="concat($targetdir,'jboss.server.conf/',$basefilename,'.jndi')"/></xsl:variable>

</xsl:stylesheet>
