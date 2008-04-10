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
		 http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xmi="http://www.omg.org/XMI"
	xmlns:webappbnd="webappbnd.xmi"
	xmlns:j2ee="http://java.sun.com/xml/ns/j2ee"
	exclude-result-prefixes="j2ee"
>
<xsl:output method="xml" indent="yes"/>
<xsl:preserve-space elements="xsl:text"/>

<xsl:template match="web-app|j2ee:web-app">
	<webappbnd:WebAppBinding 
		xmi:version="2.0"
		xmi:id="WebApp_ID_Bnd"
		virtualHostName="default_host"
	>
		<webapp href="WEB-INF/web.xml#{@id}"/>
		<xsl:apply-templates />
	</webappbnd:WebAppBinding>
</xsl:template>

<xsl:template match="resource-ref|j2ee:resource-ref">
	<xsl:if test="description|j2ee:description">
		<xsl:text>&#xA;</xsl:text>
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="description|j2ee:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
   <resRefBindings 
   	xmi:id="{@id}"
   	jndiName="{concat('java:/',../@id,'/',res-ref-name|j2ee:res-ref-name)}">
      <bindingResourceRef href="WEB-INF/web.xml#{@id}"/>
   </resRefBindings>
</xsl:template>

<xsl:template match="resource-env-ref|j2ee:resource-env-ref">
	<xsl:if test="description|j2ee:description">
		<xsl:text>&#xA;</xsl:text>
		<xsl:text>&#xA;   </xsl:text>
		<xsl:comment><xsl:value-of select="description|j2ee:description"/></xsl:comment>
		<xsl:text>&#xA;   </xsl:text>
	</xsl:if>
   <resRefBindings 
   	xmi:id="{@id}"
   	jndiName="{concat('java:/',../@id,'/',res-ref-name|j2ee:res-ref-name)}">
      <bindingResourceRef href="WEB-INF/web.xml#{@id}"/>
   </resRefBindings>
</xsl:template>


<xsl:template match="text()|comment()|processing-instruction()"/>

</xsl:stylesheet>
