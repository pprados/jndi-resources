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
	xmlns="http://java.sun.com/xml/ns/j2ee"
	xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
 	exclude-result-prefixes="jndi"
>
<!-- 	exclude-result-prefixes="#all"-->
<xsl:output method="xml" indent="yes"/>
<xsl:preserve-space elements="*"/>

<xsl:template match="/">
	<xsl:apply-templates />
</xsl:template>

<xsl:template match="jndi:resources">
	<web-app
		version="2.4"
	>
		<xsl:attribute name="id">
			<xsl:value-of select="substring-before(jndi:resource[1]/@name,'/')"/>
		</xsl:attribute>
		<xsl:apply-templates />
	</web-app>
</xsl:template>

<xsl:template match="jndi:resource">
	<resource-ref>
		<xsl:if test="preceding-sibling::comment()[1]">
			<description><xsl:value-of select="normalize-space(preceding-sibling::comment()[1])"/></description>
		</xsl:if>
		<res-ref-name><xsl:value-of select="substring-after(@name,'/')"/></res-ref-name>
		<res-type>
		<xsl:choose>
			<xsl:when test="starts-with(@familly,'jdbc/')">javax.sql.DataSource</xsl:when>
			
			<xsl:when test="starts-with(@familly,'jms/') and ends-with(@familly,'Topic')">javax.jms.Topic</xsl:when>
			<xsl:when test="starts-with(@familly,'jms/') and ends-with(@familly,'Queue')">javax.jms.Queue</xsl:when>
			<xsl:when test="starts-with(@familly,'jms/') and ends-with(@familly,'XAFactory')">javax.jms.XAConnectionFactory</xsl:when>
			<xsl:when test="starts-with(@familly,'jms/')">javax.jms.ConnectionFactory</xsl:when>
			
			<xsl:when test="starts-with(@familly,'mail/') and ends-with(@familly,'Service')">javax.mail.Service</xsl:when>
			<xsl:when test="starts-with(@familly,'mail/')">javax.mail.Session</xsl:when>
<xsl:when test="starts-with(@familly,'mail/') and ends-with(@familly,'Address')">javax.mail.Address</xsl:when>

			<xsl:when test="starts-with(@familly,'url/')">java.net.URL</xsl:when>

			<xsl:when test="starts-with(@familly,'host/')">java.net.InetAddress</xsl:when>

			<xsl:when test="starts-with(@familly,'jaas/')">org.jboss.security.AuthenticationManager</xsl:when>
			
			<xsl:when test="starts-with(@familly,'eis/')">javax.resource.cci.ConnectionFactory</xsl:when>
			<xsl:when test="starts-with(@familly,'jndi/')">javax.naming.Context</xsl:when>
			<xsl:when test="starts-with(@familly,'services/')">javax.xml.ws.Service</xsl:when>
			<xsl:otherwise>CAN NOT RESOLVE TYPE FOR <xsl:value-of select="@familly"/></xsl:otherwise>
		</xsl:choose>
		</res-type>
		<res-auth>Application</res-auth>
		<res-sharing-scope>Shareable</res-sharing-scope>
	</resource-ref>
</xsl:template>

<xsl:template match="text()|comment()|processing-instruction()"/>
</xsl:stylesheet>
