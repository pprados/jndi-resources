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
	xmlns="http://www.jndi-resources.org"
	exclude-result-prefixes="#all"
>
<xsl:output method="xml" indent="yes"/>
<xsl:preserve-space elements="xsl:text"/>

<xsl:template match="*:web-app">
	<resources>
		<xsl:if test="@id">
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
		</xsl:if>
		<xsl:apply-templates />
	</resources>
</xsl:template>

<xsl:template match="*:resource-ref">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description|description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<resource>
		<xsl:attribute name="name"><xsl:value-of select="concat(../@id,'/',*:res-ref-name)"/></xsl:attribute>
		<xsl:attribute name="familly">
		<xsl:choose>
			<xsl:when test="*:res-type='javax.sql.DataSource'">jdbc/default</xsl:when>

			<xsl:when test="*:res-type='javax.mail.Session'">mail/default</xsl:when>
<xsl:when test="*:res-type='javax.mail.Address'">mail/Mail</xsl:when>
<xsl:when test="*:res-type='javax.mail.internet.InternetAddress'">mail/Mail</xsl:when>

			<xsl:when test="*:res-type='java.net.URL'">url/default</xsl:when>

			<xsl:when test="*:res-type='javax.jms.ConnectionFactory'">jms/default/cf</xsl:when>
			<xsl:when test="*:res-type='javax.jms.XAConnectionFactory'">jms/default/cf</xsl:when>
			<xsl:when test="*:res-type='javax.jms.Queue'">jms/default/queue</xsl:when>
			<xsl:when test="*:res-type='javax.jms.Topic'">jms/default/topic</xsl:when>

			<xsl:when test="*:res-type='javax.resource.cci.ConnectionFactory'">eis/default</xsl:when>

			<xsl:when test="*:res-type='javax.naming.Context'">jndi/default</xsl:when>

			<xsl:when test="*:res-type='java.net.InetAddress'">host/default</xsl:when>

			<xsl:when test="*:res-type='org.jboss.security.plugins.SecurityDomainContext'">jaas/default</xsl:when>
			<xsl:when test="*:res-type='org.jboss.security.RealmMapping'">jaas/default</xsl:when>
			<xsl:when test="*:res-type='org.jboss.security.AuthenticationManager'">jaas/default</xsl:when>
			<xsl:when test="*:res-type='javax.xml.ws.Service'">services/default</xsl:when>
			<xsl:otherwise>CAN NOT CONVERT RESOURCE TYPE <xsl:value-of select="*:res-type"/></xsl:otherwise>
		</xsl:choose>
		</xsl:attribute>
		<xsl:choose>
			<xsl:when test="*:res-type='java.net.URL'">
				<property name="url" value="${{url}}"/>
			</xsl:when>
			<xsl:when test="*:res-type='java.net.InetAddress'">
				<property name="host" value="${{host}}"/>
			</xsl:when>
			<xsl:when test="starts-with(@res-type,javax.jms.XA)">
				<property name="xa" value="true"/>
			</xsl:when>
			<xsl:otherwise/>
		</xsl:choose>

	</resource>
</xsl:template>

<xsl:template match="*:resource-env-ref">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<resource>
		<xsl:attribute name="name"><xsl:value-of select="concat('java:',../@id,'/',*:resource-env-ref-name)"/></xsl:attribute>
		<xsl:attribute name="familly">
		<xsl:choose>
			<xsl:when test="*:resource-env-ref-type='javax.jms.Queue'">jms/DefaultQueue</xsl:when>
			<xsl:when test="*:resource-env-ref-type='javax.jms.Topic'">jms/DefaultTopic</xsl:when>
			<xsl:otherwise>CAN NOT CONVERT RESOURCE TYPE <xsl:value-of select="*:resource-env-ref-type"/></xsl:otherwise>
		</xsl:choose>
		</xsl:attribute>
	</resource>
</xsl:template>

<xsl:template match="text()|comment()|processing-instruction()"/>

</xsl:stylesheet>
