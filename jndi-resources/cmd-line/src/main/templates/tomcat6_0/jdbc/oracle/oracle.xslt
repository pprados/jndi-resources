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
		 http://jndi-resources.googlecode.com/1.0/ http://www.prados.eu/xsd/1.0/jndi-resources.xsd
		"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:jndi="http://jndi-resources.googlecode.com/1.0/"
	xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
>
<xsl:strip-space elements="*"/>
<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>
<xsl:include href="../../../templates.xslt"/>

<xsl:variable name="jdbc.username">${jdbc.oracle.username}</xsl:variable>
<xsl:variable name="jdbc.password">${jdbc.oracle.password}</xsl:variable>
<xsl:variable name="jdbc.driver">${jdbc.oracle.driver}</xsl:variable>
<xsl:variable name="jdbc.url.default">${jdbc.oracle.url.default}</xsl:variable>
<xsl:variable name="jdbc.url.prefix">${jdbc.oracle.url.prefix}</xsl:variable>
<xsl:variable name="jdbc.url.suffix">${jdbc.oracle.url.suffix}</xsl:variable>
<xsl:variable name="jdbc.factory">oracle.jdbc.pool.OracleDataSourceFactory</xsl:variable>
<xsl:variable name="username">user</xsl:variable>
<xsl:variable name="jdbc.type">oracle.jdbc.pool.OracleDataSource</xsl:variable>
<xsl:variable name="jdbc.addon"/>

<xsl:variable name="jdbc.xa.driver">${jdbc.oracle.xa.driver}</xsl:variable>
<xsl:variable name="jdbc.xa.url.default">${jdbc.oracle.xa.url.default}</xsl:variable>
<xsl:variable name="jdbc.xa.url.prefix">${jdbc.oracle.xa.url.prefix}</xsl:variable>
<xsl:variable name="jdbc.xa.url.suffix">${jdbc.oracle.xa.url.suffix}</xsl:variable>
<xsl:variable name="jdbc.xa.factory">oracle.jdbc.pool.OracleDataSourceFactory</xsl:variable>
<xsl:variable name="jdbc.xa.addon" />

<xsl:variable name="jdbc.validation-query">${jdbc.oracle.validation-query}</xsl:variable>


<xsl:template name="footer">
	<xsl:call-template name="install-drivers"/>
</xsl:template>

<xsl:variable  name="basefilename">catalina.base/conf/<xsl:value-of select="$currentid"/>_oracle-ds</xsl:variable>
<xsl:variable  name="filename"><xsl:value-of select="concat($targetdir,$basefilename,'.jndi')"/></xsl:variable>

</xsl:stylesheet>