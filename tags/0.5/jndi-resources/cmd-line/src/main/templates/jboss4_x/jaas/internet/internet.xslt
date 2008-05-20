<?xml version="1.0" encoding="UTF-8"?>

<!-- 
 * v.
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
<!-- jboss/jaas/intranet -->
<xsl:stylesheet
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd
		 http://jndi-resources.googlecode.com/1.0/ http://www.prados.fr/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
	xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>

<xsl:variable name="pattern" select="document('pattern.xml')/policy" />
<xsl:include href="../../../jboss5_x/jaas/lib/jaas.xslt"/>

<xsl:variable name="conf"><xsl:value-of select="concat($targetdir,'jboss.server.conf/conf/')"/></xsl:variable>
<xsl:variable name="props"><xsl:value-of select="concat($conf,'props/')"/></xsl:variable>
<xsl:variable name="basefilename"><xsl:value-of select="concat('deploy/',$currentid,'/',$currentid,'_',$jaasmodule,'_jaas-service')"/></xsl:variable>
<xsl:variable name="filename"><xsl:value-of select="concat($targetdir,'jboss.server.conf/',$basefilename,'.jndi')"/></xsl:variable>

<xsl:template match="jndi:resources[@id=$currentid]">
<!-- Copy files -->
	<xsl:variable name="roles"><xsl:value-of select="concat($jaasmodule,'-roles.properties')"/></xsl:variable>
	<xsl:variable name="users"><xsl:value-of select="concat($jaasmodule,'-users.properties')"/></xsl:variable>
	<xsl:value-of select="tools:fileCopy($roles,$props)"/>
	<xsl:value-of select="tools:fileCopy($users,$props)"/>

	<xsl:call-template name="main"/>
</xsl:template>

<xsl:template match="text()"/>
</xsl:stylesheet>

