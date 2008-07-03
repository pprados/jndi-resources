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
		 http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/ejb-jar_2_1.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:j2ee="http://java.sun.com/xml/ns/j2ee"
	exclude-result-prefixes="#all"
>

<xsl:output method="xml" 
    doctype-public="-//JBoss//DTD JBOSS 3.0//EN"
    doctype-system="http://www.jboss.org/j2ee/dtd/jboss_3_0.dtd"
    encoding="UTF-8"
    xml:lang="en" 
    indent="yes" />
<xsl:preserve-space elements="xsl:text"/>

<xsl:template match="*:ejb-jar">
	<jboss>
		<enterprise-beans>
			<xsl:apply-templates />
		</enterprise-beans>
	</jboss>
</xsl:template>

<xsl:template match="*:session">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<session>
		<ejb-name><xsl:value-of select="*:ejb-name"/></ejb-name>
		<jndi-name><xsl:value-of select="concat(ancestor::*:ejb-jar/@id,'/ejb/',*:ejb-name)"/></jndi-name>
		<xsl:apply-templates />
	</session>
</xsl:template>

<xsl:template match="*:entity">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<entity>
		<ejb-name><xsl:value-of select="*:ejb-name"/></ejb-name>
		<jndi-name><xsl:value-of select="concat(ancestor::*:ejb-jar/@id,'/',*:ejb-name)"/></jndi-name>
		<xsl:apply-templates />
	</entity>
</xsl:template>

<xsl:template match="*:resource-ref">
	<xsl:if test="*:description">
		<xsl:text>&#xA;         </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;         </xsl:text>
	</xsl:if>
	<resource-ref>
		<res-ref-name><xsl:value-of select="*:res-ref-name"/></res-ref-name>
		<jndi-name><xsl:value-of select="concat('java:/',ancestor::*:ejb-jar/@id,'/',*:res-ref-name)"/></jndi-name>
	</resource-ref>
</xsl:template>

<xsl:template match="*:resource-env-ref">
	<xsl:if test="*:description">
		<xsl:text>&#xA;         </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;         </xsl:text>
	</xsl:if>
	<resource-ref>
		<res-ref-name><xsl:value-of select="*:resource-env-ref-name"/></res-ref-name>
		<res-type><xsl:value-of select="*:resource-env-ref-type"/></res-type>
		<jndi-name><xsl:value-of select="concat('java:/',ancestor::*:ejb-jar/@id,'/',*:resource-env-ref-name)"/></jndi-name>
	</resource-ref>
</xsl:template>

<xsl:template match="*:ejb-ref">
	<xsl:if test="*:description">
		<xsl:text>&#xA;         </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;         </xsl:text>
	</xsl:if>
	<ejb-ref>
		<ejb-ref-name><xsl:value-of select="*:ejb-ref-name"/></ejb-ref-name>
		<jndi-name><xsl:value-of select="concat('java:/',ancestor::*:ejb-jar/@id,'/',*:ejb-name)"/></jndi-name>
	</ejb-ref>
</xsl:template>

<xsl:template match="text()|comment()|processing-instruction()"/>

</xsl:stylesheet>
