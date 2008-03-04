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
		 http://www.jndi-resources.org/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:sch="http://www.w3.org/2001/XMLSchema"
	xmlns:jndi="http://www.jndi-resources.org/1.0/"
	xmlns:tools="http://www.jndi-resources.org/1.0/java/org.jndiresources.tools.XSLTools"
	exclude-result-prefixes="#all"
>
<xsl:include href="../drivers.xslt"/>

<xsl:template name="install-drivers">
	<xsl:param name="familly" select="$familly"/>
	<xsl:param name="artifactref" select="$familly"/>
	<xsl:param name="alias"/>
	<xsl:choose>
		<xsl:when test="//jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]/jndi:property[@name='artifact']">
			<xsl:call-template name="install-drivers2">
				<xsl:with-param name="artifact" select="//jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]/jndi:property[@name='artifact']/@value"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:when test="$drivers/familly[@name=$familly]/@artifact">
			<xsl:call-template name="install-drivers2">
					<xsl:with-param name="artifact" select="$drivers/familly[@name=$familly]/@artifact"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:for-each select="$drivers/familly[@name=$familly]/artifact">
				<xsl:call-template name="install-drivers2">
						<xsl:with-param name="artifact" select="."/>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="install-drivers2">
	<xsl:param name="artifact"/>
	<xsl:param name="alias"/>
 	<xsl:choose>
 		<xsl:when test="$alias=''">
		 	<xsl:variable name="url" select="tools:mavenCopy($artifact,concat($targetdir,'../lib/'))"/>
			<xsl:value-of select="tools:mkLink(concat($targetdir,'../lib/',$url),concat($lib,$url))"/>
		</xsl:when>
		<xsl:otherwise>
		 	<xsl:variable name="url" select="tools:mavenCopy($artifact,concat($targetdir,'../lib/'))"/>
			<xsl:value-of select="tools:mkLink(concat($targetdir,'../lib/',$url),concat($lib,$alias))"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


</xsl:stylesheet>