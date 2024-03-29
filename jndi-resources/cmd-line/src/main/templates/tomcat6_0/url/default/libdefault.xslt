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
<!-- jboss/jaas/default -->
<xsl:stylesheet
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		 http://jndi-resources.googlecode.com/1.0/ http://www.prados.eu/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
	xmlns:sch="http://www.w3.org/2001/XMLSchema"
	xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
	exclude-result-prefixes="#all"
>
<xsl:param name="war"/>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>

<xsl:variable  name="basefilename">
	<xsl:value-of select="concat('catalina.base/conf/',$currentid,'_urls-service')"/>
</xsl:variable>
<xsl:variable  name="filename">
	<xsl:value-of select="concat($targetdir,$basefilename,'.jndi')"/>
</xsl:variable>

<xsl:template match="text()|comment()" />

<xsl:template match="jndi:resources[@id=$currentid]">
<xsl:if test="not(ends-with($war,'.ear'))">
<xsl:value-of select="tools:fileCopy('../../../tools/xslt/save.xslt',concat($targetdir,'../xslt/'))"/>
<xsl:value-of select="tools:fileCopy('../../update-tomcat-server.xslt',concat($targetdir,'../xslt/'))"/>
<xsl:value-of select="tools:info(concat('Generate ',$filename))"/>
<xsl:result-document  href="{$filename}">
<xsl:text>&#xA;</xsl:text>
<xsl:comment>Generated by jndi-resources v<xsl:value-of select="$version"/></xsl:comment>
<xsl:text>&#xA;</xsl:text>
<xsl:processing-instruction name="jndi-stylesheet">href="xslt/update-tomcat-server.xslt"</xsl:processing-instruction>
<xsl:text>&#xA;</xsl:text>
<xsl:processing-instruction name="jndi-stylesheet">href="xslt/save.xslt" target="conf/server.xml"</xsl:processing-instruction>
<xsl:text>&#xA;</xsl:text>
	<Server>
		<GlobalNamingResources>
			<xsl:apply-templates />
		</GlobalNamingResources>
	</Server>
</xsl:result-document>
</xsl:if>
</xsl:template>

<xsl:template match="jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]">

	<Resource 
		auth="Container" 
		type="java.net.URL" 
		factory="com.googlecode.jndiresources.factory.URLFactory">
		<xsl:attribute name="name">
			<xsl:value-of select="concat('${jndi-prefix}',@name)"/>
		</xsl:attribute>
        <xsl:if test="not(jndi:property[@name='url'])">
			<xsl:value-of select="error(sch:QName('error9999'),concat('The property url must be set for resource ',@familly))"/>
        </xsl:if>
		<xsl:attribute name="value">
			<xsl:value-of select="jndi:property[@name='url']/@value"/>
		</xsl:attribute>
	</Resource>
      
 </xsl:template>

</xsl:stylesheet>

