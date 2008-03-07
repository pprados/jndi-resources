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
		 http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
	xmlns:sch="http://www.w3.org/2001/XMLSchema"
	xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
	exclude-result-prefixes="#all">
	<xsl:strip-space elements="*" />
	<xsl:output method="xml" indent="yes" encoding="utf-8"
		omit-xml-declaration="no" />

	<xsl:variable name="basefilename">
		<xsl:value-of
			select="concat('deploy/',$currentid,'/',$currentid,'_hosts-service')" />
	</xsl:variable>
	<xsl:variable name="filename">
		<xsl:value-of
			select="concat($targetdir,'jboss.server.conf/',$basefilename,'.jndi')" />
	</xsl:variable>

	<xsl:template match="text()|comment()" />

	<xsl:template match="jndi:resources[@id=$currentid]">
		<xsl:value-of
			select="tools:fileCopy('../../../tools/xslt/save.xslt',concat($targetdir,'../xslt/'))" />
		<xsl:value-of
			select="tools:info(concat('Generate ',$filename))" />
		<xsl:result-document href="{$filename}">
			<xsl:text>&#xA;</xsl:text>
			<xsl:comment>Generated by jndi-resources v<xsl:value-of select="$version" /></xsl:comment>
			<xsl:text>&#xA;</xsl:text>
			<xsl:processing-instruction name="jndi-stylesheet">href="xslt/save.xslt" target="<xsl:value-of select="concat($basefilename,'.xml')" />"</xsl:processing-instruction>
			<xsl:text>&#xA;</xsl:text>
			<server>
				<xsl:apply-templates />
			</server>
		</xsl:result-document>
	</xsl:template>

	<xsl:template
		match="jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]">

		<mbean code="com.googlecode.jndiresourcesmbean.ReferenceService"
			name="{$currentid}:service=Host,jndiName={@name}"
		>
			<xsl:if test="not(jndi:property[@name='host'])">
				<xsl:value-of
					select="error(sch:QName('error9999'),concat('The property host must be set for resource ',@familly))" />
			</xsl:if>

			<xsl:choose>
				<xsl:when test="$use-java-context='true'">
					<attribute name="JNDIName">
						<xsl:value-of select="concat('java:',@name)" />
					</attribute>
				</xsl:when>
				<xsl:otherwise>
					<attribute name="JNDIName">
						<xsl:value-of select="@name" />
					</attribute>
				</xsl:otherwise>
			</xsl:choose>
			<attribute name="Type">java.net.InetAddress</attribute>
			<attribute name="Factory">com.googlecode.jndiresourcesfactory.InetAddressFactory</attribute>
			<attribute name="Configuration">
				<configuration>
					<property name="value"
						value="{jndi:property[@name='host']/@value}" />
				</configuration>
			</attribute>
			<depends>jboss:service=Naming</depends>
		</mbean>

	</xsl:template>

</xsl:stylesheet>

