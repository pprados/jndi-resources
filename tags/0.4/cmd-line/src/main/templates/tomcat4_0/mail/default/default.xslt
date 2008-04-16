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
	xmlns:tools="http://jndi-resources.googlecode.com/1.0/java/com.googlecode.jndiresources.tools.XSLTools"
	exclude-result-prefixes="#all">
	<xsl:param name="war"/>
	<xsl:strip-space elements="*" />
	<xsl:output method="xml" indent="yes" encoding="utf-8"
		omit-xml-declaration="no" />
	<xsl:include href="../../install-drivers.xslt"/>
	<xsl:variable name="lib">
		<xsl:value-of select="concat($targetdir,'catalina.home/common/lib/')" />
	</xsl:variable>

	<xsl:variable name="basefilename">catalina.base/conf/<xsl:value-of select="$currentid" />_mail_service</xsl:variable>
	<xsl:variable name="filename">
		<xsl:value-of select="concat($targetdir,$basefilename,'.jndi')" />
	</xsl:variable>

	<xsl:template match="text()|comment()" />

	<xsl:template match="jndi:resources[@id=$currentid]">
	<xsl:if test="not(ends-with($war,'.ear'))">
		<xsl:value-of
			select="tools:fileCopy('../../../tools/xslt/save.xslt',concat($targetdir,'../xslt/'))" />
		<xsl:value-of select="tools:fileCopy('../../update-tomcat-server.xslt',concat($targetdir,'../xslt/'))"/>
		<xsl:value-of
			select="tools:info(concat('Generate ',$filename))" />
		<xsl:result-document href="{$filename}">
			<xsl:text>&#xA;</xsl:text>
			<xsl:comment>Generated by jndi-resources v<xsl:value-of select="$version" /></xsl:comment>
			<xsl:text>&#xA;</xsl:text>
			<xsl:processing-instruction name="jndi-stylesheet">href="xslt/update-tomcat-server.xslt"</xsl:processing-instruction>
			<xsl:text>&#xA;</xsl:text>
			<xsl:processing-instruction name="jndi-stylesheet">href="xslt/save.xslt" target="conf/server.xml"</xsl:processing-instruction>
			<xsl:text>&#xA;</xsl:text>
			<Server>
				<GlobalNamingResources>
					<xsl:apply-templates />
				</GlobalNamingResources>
			</Server>
		</xsl:result-document>

		<xsl:call-template name="install-drivers">
			<xsl:with-param name="familly" select="'javamail'"/>
		</xsl:call-template>
	</xsl:if>
	</xsl:template>

	<xsl:template
		match="jndi:resources[@id=$currentid]/jndi:resource[@familly=$familly]">
		<Resource auth="Container" type="javax.mail.Session">
			<xsl:attribute name="name">
          	<xsl:choose>
          		<xsl:when test="starts-with(@name,'java:')">
          			<xsl:value-of
							select="concat('${jndi-prefix}',substring-after(@name,'java:'))" />
          		</xsl:when>
          		<xsl:otherwise>
          			<xsl:value-of select="concat('${jndi-prefix}',@name)" />
          		</xsl:otherwise>
          	</xsl:choose>
          	</xsl:attribute>
		</Resource>
		<ResourceParams>
			<xsl:attribute name="name">
          	<xsl:choose>
          		<xsl:when test="starts-with(@name,'java:')">
          			<xsl:value-of
							select="concat('${jndi-prefix}',substring-after(@name,'java:'))" />
          		</xsl:when>
          		<xsl:otherwise>
          			<xsl:value-of select="concat('${jndi-prefix}',@name)" />
          		</xsl:otherwise>
          	</xsl:choose>
          	</xsl:attribute>

			<parameter>
				<name>factory</name>
				<value>org.apache.naming.factory.MailSessionFactory</value>
			</parameter>

			<xsl:call-template name="property">
				<xsl:with-param name="param">user</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="attribute">
				<xsl:with-param name="param">password</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="property">
				<xsl:with-param name="param">store.protocol</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="property">
				<xsl:with-param name="param">transport.protocol</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="property">
				<xsl:with-param name="param">host</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="property">
				<xsl:with-param name="param">from</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="property">
				<xsl:with-param name="param">debug</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">user</xsl:with-param>
				<xsl:with-param name="tech">store</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">host</xsl:with-param>
				<xsl:with-param name="tech">store</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">port</xsl:with-param>
				<xsl:with-param name="tech">store</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">connectiontimeout</xsl:with-param>
				<xsl:with-param name="tech">store</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">timeout</xsl:with-param>
				<xsl:with-param name="tech">store</xsl:with-param>
			</xsl:call-template>


			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">user</xsl:with-param>
				<xsl:with-param name="tech">transport</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">host</xsl:with-param>
				<xsl:with-param name="tech">transport</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">port</xsl:with-param>
				<xsl:with-param name="tech">transport</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">connectiontimeout</xsl:with-param>
				<xsl:with-param name="tech">transport</xsl:with-param>
			</xsl:call-template>

			<xsl:call-template name="protocol-property">
				<xsl:with-param name="param">timeout</xsl:with-param>
				<xsl:with-param name="tech">transport</xsl:with-param>
			</xsl:call-template>


			<xsl:for-each
				select="jndi:extends[@appsrv=$appsrv]/jndi:attribute">
				<xsl:attribute name="{@name}">
					<xsl:value-of select="." />
				</xsl:attribute>
			</xsl:for-each>
		</ResourceParams>
	</xsl:template>

	<xsl:template name="attribute">
		<xsl:param name="param" />
		<xsl:choose>
			<xsl:when test=".//jndi:property[@name=$param]">
				<parameter>
					<name><xsl:value-of select="$param"/></name>
					<value>
						<xsl:value-of select=".//jndi:property[@name=$param]/@value" />
					</value>
				</parameter>
			</xsl:when>
			<xsl:otherwise>
				<parameter>
					<name><xsl:value-of select="$param"/></name>
					<value>
						<xsl:value-of select="concat('${mail.',$param,'}')" />
					</value>
				</parameter>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="property">
		<xsl:param name="param" />
		<xsl:choose>
			<xsl:when test=".//jndi:property[@name=$param]">
				<parameter>
					<name>
						<xsl:value-of select="concat('mail.',$param)"/>
					</name>
					<value>
						<xsl:value-of select=".//jndi:property[@name=$param]/@value" />
					</value>
				</parameter>
			</xsl:when>
			<xsl:otherwise>
				<parameter>
					<name>
						<xsl:value-of select="concat('mail.',$param)"/>
					</name>
					<value><xsl:value-of select="concat('${mail.',$param,'}')" /></value>
				</parameter>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="protocol-property">
		<xsl:param name="param" />
		<xsl:param name="tech" />
		<xsl:choose>
			<xsl:when
				test=".//jndi:property[@name=concat($tech,'.',$param)]">
				<xsl:choose>
					<xsl:when
						test=".//jndi:property[@name=concat($tech,'.protocol')]">
						<parameter>
							<name>
								<xsl:value-of select="concat('mail.',.//jndi:property[@name=concat($tech,'.protocol')]/@value,'.',$param)"/>
							</name>
							<value>
								<xsl:value-of select=".//jndi:property[@name=concat($tech,'.',$param)]/@value" />
							</value>
						</parameter>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$tech = 'transport'">
								<parameter>
									<name>
										<xsl:value-of select="concat('mail.smtp.',$param)"/>
									</name>
									<value>
										<xsl:value-of select=".//jndi:property[@name=concat($tech,'.',$param)]/@value" />
									</value>
								</parameter>
							</xsl:when>
							<xsl:otherwise>
								<parameter>
									<name>
										<xsl:value-of select="concat('mail.pop3.',$param)"/>
									</name>
									<value>
										<xsl:value-of select=".//jndi:property[@name=concat($tech,'.',$param)]/@value" />
									</value>
								</parameter>
								<parameter>
									<name>
										<xsl:value-of select="concat('mail.imap.',$param)"/>
									</name>
									<value>
										<xsl:value-of select=".//jndi:property[@name=concat($tech,'.',$param)]/@value" />
									</value>
								</parameter>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when
						test=".//jndi:property[@name=concat($tech,'.protocol')]">
						<parameter>
							<name>
								<xsl:value-of select="concat('mail.',.//jndi:property[@name=concat($tech,'.protocol')]/@value,'.',$param)"/>
							</name>
							<value>
								<xsl:value-of select="concat('${mail.',$tech,'.',$param,'}')" />
							</value>
						</parameter>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$tech = 'transport'">
								<xsl:choose>
									<xsl:when
										test=".//jndi:property[@name=concat('transport.',$param)]">
										<parameter>
											<name>
												<xsl:value-of select="concat('mail.smtp.',$param)"/>
											</name>
											<value>
												<xsl:value-of select=".//jndi:property[@name=concat('transport.',$param)]" />
											</value>
										</parameter>
									</xsl:when>
									<xsl:otherwise>
										<parameter>
											<name>
												<xsl:value-of select="concat('mail.smtp.',$param)"/>
											</name>
											<value>
												<xsl:value-of select="concat('${mail.',$tech,'.',$param,'}')" />
											</value>
										</parameter>
									</xsl:otherwise>
								</xsl:choose>

							</xsl:when>
							<xsl:otherwise>
								<parameter>
									<name>
										<xsl:value-of select="concat('mail.pop3.',$param)"/>
									</name>
									<value>
										<xsl:value-of select="concat('${mail.',$tech,'.',$param,'}')" />
									</value>
								</parameter>
								<parameter>
									<name>
										<xsl:value-of select="concat('mail.imap.',$param)"/>
									</name>
									<value>
										<xsl:value-of select="concat('${mail.',$tech,'.',$param,'}')" />
									</value>
								</parameter>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>

