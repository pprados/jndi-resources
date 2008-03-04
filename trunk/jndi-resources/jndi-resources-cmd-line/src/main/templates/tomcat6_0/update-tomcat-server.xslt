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
	xmlns:tools="http://www.jndi-resources.org/java/org.jndiresources.tools.XSLTools"
>
<xsl:output method="xml" indent="yes"/>
<xsl:preserve-space elements="*"/>
<xsl:param name="version"/>
<xsl:param name="targetdir"/>
<xsl:param name="id"/> <!-- Node to replace -->

<xsl:variable  name="filename"><xsl:value-of select="concat($targetdir,'conf/server.xml')"/></xsl:variable>
<xsl:variable  name="nodes-to-insert" select="/Server/GlobalNamingResources/*"/>
<xsl:variable  name="nodes-id" select="Server/GlobalNamingResources/Resource/@name" />

<xsl:template match="/">
	<xsl:apply-templates mode="update" select="document($filename)" />
</xsl:template>

<!-- Identity copy -->
<xsl:template match="@*|*|processing-instruction()|comment()" mode="update">
	<xsl:copy>
		<xsl:apply-templates mode="update" select="*|@*|text()|processing-instruction()|comment()"/>
	</xsl:copy>
</xsl:template>


<!-- Remove older -->
<xsl:template match="/Server/GlobalNamingResources/Resource[@name=$nodes-id]|ResourceParams[@name=$nodes-id]" mode="update"/>


<!-- Insert new -->
<xsl:template match="/Server/GlobalNamingResources" mode="update">
	<xsl:copy>
		<xsl:copy-of select="$nodes-to-insert"/>
		<xsl:apply-templates mode="update" select="*|@*|text()|processing-instruction()|comment()"/>
	</xsl:copy>
</xsl:template>
</xsl:stylesheet>
