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

<!-- TODO -->
<xsl:stylesheet 
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		 http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://geronimo.apache.org/xml/ns/j2ee/web-1.1"
	xmlns:sys="http://geronimo.apache.org/xml/ns/deployment-1.1"
	xmlns:naming="http://geronimo.apache.org/xml/ns/naming-1.1"
	xmlns:security="http://geronimo.apache.org/xml/ns/security-1.1"
	xmlns:dep="http://geronimo.apache.org/xml/ns/deployment-1.1"
	exclude-result-prefixes="#all"
>
<xsl:output method="xml" 
    encoding="UTF-8"
    xml:lang="en" 
    indent="yes" />
<xsl:preserve-space elements="xsl:text"/>

<xsl:template match="*:web-app">
	<web-app xmlns="http://geronimo.apache.org/xml/ns/j2ee/web-1.1" >
		<dep:environment>
     		<moduleId>
       			<groupId>geronimo</groupId>
       			<artifactId>HelloWorld</artifactId>
       			<version>1.1</version>
       			<type>war</type>
     		</moduleId>
	        <dependencies>
                <xsl:for-each select="*:resource-ref/*:res-ref-name[starts-with(text(),'jdbc/')]">
		            <dependency>
		                <groupId>console.dbpool</groupId>
	                	<artifactId>
	                		<xsl:call-template name="escape-slash">
	                			<xsl:with-param name="str"><xsl:value-of select="concat(ancestor::*:web-app/@id,'/',.)"/></xsl:with-param>
	                		</xsl:call-template>
	                	</artifactId>
		            </dependency>
                </xsl:for-each>

                <xsl:if test="*:resource-ref/*:res-ref-name[starts-with(text(),'mail/')]">
		            <dependency>
						<groupId>org.apache.geronimo.configs</groupId>
    	            	<artifactId>javamail</artifactId>
        	        	<!-- <version>2.1</version>-->
            	    	<type>car</type>
            		</dependency>
            	</xsl:if>
	        </dependencies>
   		</dep:environment>
   		
  		<context-root><xsl:value-of select="@id"/></context-root>

		<xsl:apply-templates />
	</web-app>
</xsl:template>

<xsl:template match="*:resource-ref[*:res-ref-name[starts-with(text(),'jdbc/')] ]">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<resource-ref> 
		<ref-name><xsl:value-of select="*:res-ref-name"/></ref-name> 
		<resource-link><xsl:value-of select="concat(ancestor::*:web-app/@id,'/',*:res-ref-name)"/></resource-link> 
	</resource-ref>
</xsl:template>

<!-- Voir http://www.ibm.com/developerworks/java/library/os-ag-jndi3/ -->
<xsl:template match="*:resource-ref[*:res-ref-name[starts-with(text(),'mail/')] ]">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<resource-ref> 
		<ref-name><xsl:value-of select="*:res-ref-name"/></ref-name> 
		<resource-link>mail/MailSession</resource-link> 
	</resource-ref>
</xsl:template>

<xsl:template match="*:resource-env-ref">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
	<resource-env-ref> 
		<ref-name><xsl:value-of select="*:resource-env-ref-name"/></ref-name> 
		<admin-object-link><xsl:value-of select="concat(ancestor::*:web-app/@id,'/',*:resource-env-ref-name)"/></admin-object-link>
	</resource-env-ref>
</xsl:template>

<xsl:template match="*:ejb-ref">
	<xsl:if test="*:description">
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="*:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
    <ejb-ref>
        <ref-name><xsl:value-of select="*:ejb-ref-name"/></ref-name>
        <ejb-link><xsl:value-of select="concat(ancestor::*:web-app/@id,'/',*:ejb-ref-name)"/></ejb-link>
    </ejb-ref>
</xsl:template>

<xsl:template match="text()|comment()|processing-instruction()"/>

<!-- Escape slash -->
<xsl:template name="escape-slash">
	<xsl:param name="str"/>
	<xsl:choose>
		<xsl:when test="contains($str,'/')">
			<xsl:value-of select="substring-before($str,'/')"/>
			<xsl:value-of select="'%2F'"/>
			<xsl:call-template name="escape-slash">
				<xsl:with-param name="str"><xsl:value-of select="substring-after($str,'/')"/></xsl:with-param>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise><xsl:value-of select="$str"/></xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
