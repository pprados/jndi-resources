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
		 http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
	xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
	exclude-result-prefixes="#all"
>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:param name="version"/>
<xsl:param name="appsrv"/>
<xsl:param name="currentid"/>
<xsl:param name="targetdir"/>
<xsl:param name="parentwar"/>
<xsl:param name="war"/>

<xsl:variable name="warbasename"><xsl:value-of select="substring-before($war,'.war')"/></xsl:variable>
<xsl:variable name="wartargetfile"><xsl:value-of select="concat($targetdir,'catalina.base/webapps/',$warbasename,'.xml')"/></xsl:variable>

<xsl:template match="/">
	<xsl:if test="$war != ''">
		<xsl:value-of select="tools:mkLink(concat($parentwar,$war),concat($targetdir,'../',$war),concat($targetdir,'catalina.base/webapps/',$war))"/>
		<xsl:apply-templates select="document(concat('jar:file:',$parentwar,$war,'!/WEB-INF/web.xml'))" mode="web.xml"/>
	</xsl:if>
	<xsl:value-of select="tools:fileCopyIfExist('templates.properties',concat($targetdir,'templates.properties'))"/>
</xsl:template>

<xsl:template match="/" mode="web.xml">
	<xsl:value-of
		select="tools:info(concat('Generate ',$wartargetfile))" />
	<xsl:result-document href="{$wartargetfile}">
	<Context path="/{$warbasename}" docBase="{$war}" debug="0" reloadable="true"
	             crossContext="true">
	     <xsl:apply-templates mode="web.xml"/>
	</Context>
	</xsl:result-document>
</xsl:template>

<xsl:template match="resource-ref" mode="web.xml">
	<ResourceLink>
		<xsl:attribute name="name"><xsl:value-of select="res-ref-name"/></xsl:attribute>
		<xsl:attribute name="global"><xsl:value-of select="concat('/',../@id,'/',res-ref-name)"/></xsl:attribute>
		<xsl:attribute name="type"><xsl:value-of select="res-type"/></xsl:attribute>
	</ResourceLink>
</xsl:template>

<xsl:template match="resource-env-ref" mode="web.xml">
	<ResourceLink>
		<xsl:attribute name="name"><xsl:value-of select="resource-env-ref-name"/></xsl:attribute>
		<xsl:attribute name="global"><xsl:value-of select="concat('java:/',../@id,'/',resource-env-ref-name)"/></xsl:attribute>
		<xsl:attribute name="type"><xsl:value-of select="resource-env-ref-type"/></xsl:attribute>
	</ResourceLink>
</xsl:template>

<xsl:template match="text()|comment()|processing-instruction()" mode="web.xml" />

</xsl:stylesheet>
