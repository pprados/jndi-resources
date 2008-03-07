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
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:j2ee="http://java.sun.com/xml/ns/j2ee"
	exclude-result-prefixes="#all"
>
<xsl:output method="xml" 
    doctype-public="-//JBoss//DTD Web Application 2.3//EN"
    doctype-system="http://www.jboss.org/j2ee/dtd/jboss-web_3_0.dtd"
    encoding="UTF-8"
    xml:lang="en" 
    indent="yes" />
<xsl:preserve-space elements="xsl:text"/>

<xsl:template match="web-app|j2ee:web-app">
	<jboss-web >
		<xsl:apply-templates select="resource-ref[starts-with(res-ref-name,'jaas/')][1]|j2ee:resource-ref[starts-with(j2ee:res-ref-name,'jaas/')][1]" mode="security"/>
		<xsl:apply-templates />
	</jboss-web>
</xsl:template>

<xsl:template match="resource-ref[starts-with(res-ref-name,'jaas/')]|j2ee:resource-ref[starts-with(j2ee:res-ref-name,'jaas/')]" mode="security">
	<xsl:if test="description|j2ee:description">
		<xsl:comment><xsl:value-of select="description|j2ee:description"/></xsl:comment>
	</xsl:if>
	<security-domain>java:/jaas/JBossWS</security-domain>
</xsl:template>

<xsl:template match="resource-ref|j2ee:resource-ref">
	<xsl:if test="description|j2ee:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="description|j2ee:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<resource-ref>
		<res-ref-name><xsl:value-of select="res-ref-name|j2ee:res-ref-name"/></res-ref-name>
		<jndi-name><xsl:value-of select="concat('java:/',../@id,'/',res-ref-name|j2ee:res-ref-name)"/></jndi-name>
	</resource-ref>
</xsl:template>

<xsl:template match="resource-env-ref|j2ee:resource-env-ref">
	<xsl:if test="description|j2ee:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="description|j2ee:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<resource-ref>
		<res-ref-name><xsl:value-of select="resource-env-ref-name|j2ee:resource-env-ref-name"/></res-ref-name>
		<res-type><xsl:value-of select="resource-env-ref-type|j2ee:resource-env-ref-type"/></res-type>
		<jndi-name><xsl:value-of select="concat('java:/',../@id,'/',resource-env-ref-name|j2ee:resource-env-ref-name)"/></jndi-name>
	</resource-ref>
</xsl:template>

<xsl:template match="text()|comment()|processing-instruction()"/>

</xsl:stylesheet>
