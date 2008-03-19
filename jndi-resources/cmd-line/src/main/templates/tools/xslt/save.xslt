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

<xsl:stylesheet 
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		 http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:tools="http://www.jndi-resources.org/java/com.googlecode.jndiresources.tools.XSLTools"
>
<xsl:output method="xml" indent="yes"/>
<xsl:preserve-space elements="*"/>
<xsl:param name="targetdir"/>
<xsl:param name="target"/>
<xsl:param name="debug"/>

<xsl:template match="/">
<xsl:value-of select="tools:info(concat('Generate ',$targetdir,$target))"/>
	<xsl:if test="$debug">
		<xsl:apply-templates />
	</xsl:if>
	<xsl:if test="not($debug)">
		<xsl:result-document  href="{$targetdir}{$target}">
			<xsl:apply-templates />
		</xsl:result-document>
	</xsl:if>
</xsl:template>

<xsl:template match="@*|*|comment()">
	<xsl:copy>
		<xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()"/>
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>
