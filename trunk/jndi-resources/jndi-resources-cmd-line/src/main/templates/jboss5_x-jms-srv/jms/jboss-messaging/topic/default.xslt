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
<xsl:stylesheet
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		 http://www.jndi-resources.org/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jndi="http://www.jndi-resources.org/1.0/"
	xmlns:tools="http://www.jndi-resources.org/1.0/java/org.jndiresources.tools.XSLTools"
	exclude-result-prefixes="#all"
	>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>

<xsl:variable  name="basefilename"><xsl:value-of select="concat('deploy/',$currentid,'/',$currentid,'_srv_topic-service')"/></xsl:variable>
<xsl:variable  name="filename"><xsl:value-of select="concat($targetdir,'jboss.server.conf/',$basefilename,'.jndi')"/></xsl:variable>

<xsl:template match="jndi:resources[@id=$currentid]">
<xsl:value-of select="tools:info(concat('Generate ',$filename))"/>
<xsl:result-document  href="{$filename}">
<xsl:text>&#xA;</xsl:text>
<xsl:comment>Generated by jndi-resources v<xsl:value-of select="$version"/></xsl:comment>
<xsl:text>&#xA;</xsl:text>
<xsl:processing-instruction name="jndi-stylesheet">href="xslt/save.xslt" target="<xsl:value-of select="concat($basefilename,'.xml')"/>"</xsl:processing-instruction>
<!-- <xsl:processing-instruction name="jndi-stylesheet">href="xslt/merge-jboss-server.xslt" target="<xsl:value-of select="concat($basefilename,'_jms-service.xml')"/>"</xsl:processing-instruction>-->
<xsl:text>&#xA;</xsl:text>
<server>
	<xsl:apply-templates />
</server>
</xsl:result-document>
</xsl:template>

<xsl:template match="jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]">

	<xsl:text>&#xA;</xsl:text>
	<xsl:text>&#xA;   </xsl:text>
	<xsl:comment>Register JMS Topic for key <xsl:value-of select="@name"/></xsl:comment>
	<xsl:text>&#xA;   </xsl:text>


	<mbean code="org.jboss.jms.server.destination.TopicService"
		name="{$currentid}:service=Topic,name={@name}"
		xmbean-dd="xmdesc/Queue-xmbean.xml">
		<depends optional-attribute-name="ServerPeer">jboss.messaging:service=ServerPeer</depends>
		<depends>jboss:service=Naming</depends>
		<depends>jboss.messaging:service=PostOffice</depends>
		<attribute name="JNDIName"><xsl:value-of select="concat('${jndi-prefix}',@name)" /></attribute>
		
		<xsl:if test="./jndi:property[@name='dead-letter-queue']">
			<depends><xsl:value-of
					select="concat($currentid,':service=Queue,name=',jndi:property[@name='dead-letter-queue']/@value)" /></depends>
			<attribute name="DLQ"><xsl:value-of
					select="concat($currentid,':service=Queue,name=',jndi:property[@name='dead-letter-queue']/@value)" /></attribute>
		</xsl:if>
		
		<xsl:if test="./jndi:property[@name='expiry-queue']">
			<depends><xsl:value-of
					select="concat($currentid,':service=Queue,name=',jndi:property[@name='expiry-queue']/@value)" /></depends>
			<attribute name="ExpiryQueue"><xsl:value-of
					select="concat($currentid,':service=Queue,name=',jndi:property[@name='expiry-queue']/@value)" /></attribute>
		</xsl:if>
		<!-- TODO : Bug -->
		<xsl:call-template name="attribute">
			<xsl:with-param name="name">max-size</xsl:with-param>
			<xsl:with-param name="alias">MaxSize</xsl:with-param>
			<xsl:with-param name="default">${jms.full-size}</xsl:with-param>
		</xsl:call-template>
		
		
		<xsl:call-template name="attribute">
			<xsl:with-param name="name">redelivery-delay</xsl:with-param>
			<xsl:with-param name="alias">RedeliveryDelay</xsl:with-param>
			<xsl:with-param name="default">${jms.redelivery-delay}</xsl:with-param>
		</xsl:call-template>

 
		<xsl:call-template name="attribute">
			<xsl:with-param name="name">message-counter-history-day-limit</xsl:with-param>
			<xsl:with-param name="alias">MaxDeliveryAttempts</xsl:with-param>
			<xsl:with-param name="default">${jms.message-counter-history-day-limit}</xsl:with-param>
		</xsl:call-template>

		<xsl:copy-of select="jndi:extends[@appsrv=$appsrv]/*" copy-namespaces="no"/>
	</mbean>
</xsl:template>

<xsl:template name="attribute">
<xsl:param name="name"/>
<xsl:param name="alias"/>
<xsl:param name="default"/>
<xsl:param name="value"/>
	    <xsl:choose>
	    	<xsl:when test=".//jndi:property[@name=$name]">
	    		<attribute name="{$alias}">
    				<xsl:value-of select=".//jndi:property[@name=$name]/@value"/>
	    		</attribute>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<attribute name="{$alias}">
    				<xsl:value-of select="$default"/>
	    		</attribute>
	    	</xsl:otherwise>
	    </xsl:choose>
</xsl:template>

<xsl:template match="text()|comment()" />

</xsl:stylesheet>
