<?xml version="1.0"?>

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
<!-- TODO: Faire des liens avec les noms des templates et les call-templates -->
<xsl:stylesheet 
	version="2.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation=
		"http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="#all"
>
	<xsl:output method="xml" indent="yes" encoding="utf-8" />
<!-- dqs -->
	<xsl:template match="/">
		<html>
		<head>
<style type="text/css">
body { 
	background-color: #fff;
	font-family: Arial, Helvetica, sans-serif;
}

a:link {
 color: #00f;
}
a:visited {
 color: #00a;
}

table {
  border-spacing: 0;
  margin: 0;
}

td {
  padding: 0;
}

.markup {
 color: green;
}

.start-tag {
 color: #3F7F7F;
 font-weight: normal;
}
.end-tag {
 color: #3F7F7F;
 font-weight: normal;
}
.start-tag-bold {
 color: #3F7F7F;
 font-weight: bold;
}
.end-tag-bold {
 color: #3F7F7F;
 font-weight: bold;
}

.attribute-name {
 color: #7F007F;
}
.attribute-value {
 color: #2A00FF;
}
.comment-delimiteur {
  color: #3F5FBF;
  font-style: italic;
  white-space: pre;
}
.comment-content {
  color: #3F5FBF;
  font-style: italic;
  white-space: pre;
}
.pi {
 color: orchid;
 font-style: italic;
}
.text {
  font-weight: normal;
  color: #5f0cff;
}

.header {
  border-bottom: 3px solid black;
  padding: 0.5em;
  margin-bottom: 1em;
  text-align: left;
  font-size: small;
}

.indent {
  margin-left: 1em;
}

.spacer {
  width: 1em;
}

.expander {
  cursor: pointer;
  -moz-user-select: none;
  vertical-align: top;
  text-align: center;
}

.expander-closed > * > .expander-content {
  display: none;
}

</style>
		</head>
		<body>
			<script type="text/javascript"><![CDATA[
document.onclick=function(event)
{
	try 
	{
	          var par = event.originalTarget;
	          if (par.nodeName == 'td' && par.className == 'expander') 
	          {
	            if (par.parentNode.className == 'expander-closed') 
	            {
	              par.parentNode.className = '';
	              event.originalTarget.firstChild.data = '\u2212';
	            }
	            else 
	            {
	              par.parentNode.className = 'expander-closed';
	              event.originalTarget.firstChild.data = '+';
	            }
	          }
	} 
	catch (e) 
	{
	}
};
			]]></script>
	    <div class="header">
	      <p>
	        <a href=".">Parent</a>
	      </p>
	    </div>
	 		<xsl:apply-templates />
	    </body>
	    </html>
	</xsl:template>
	
	<xsl:template match="*">
		<div class="indent">
			&lt;
			<span class="start-tag">
				<xsl:value-of select="name(.)" />
			</span>
			<xsl:apply-templates select="@*" />
			/&gt;
		</div>
	</xsl:template>

	<xsl:template match="xsl:include|xsl:import">
		&lt;
		<span class="start-tag">
			<xsl:value-of select="name(.)" />
		</span>
		<xsl:text> </xsl:text>
		<span class="attribute-name">href</span>
		=
		<span class="attribute-value">"<a href="{@href}"><xsl:value-of select="@href" /></a>"</span>
		/&gt;
	</xsl:template>
	
	<xsl:template
		match="*[node() or processing-instruction() or comment() or string-length(.) &gt; 50]">
		<table>
			<tr>
				<xsl:call-template name="expander" />
				<td>
					&lt;
					<span class="start-tag">
						<xsl:value-of select="name(.)" />
					</span>
					<xsl:apply-templates select="@*" />
					&gt;
					<div class="expander-content">
						<xsl:apply-templates />
					</div>
					&lt;/
					<span class="end-tag">
						<xsl:value-of select="name(.)" />
					</span>
					&gt;
				</td>
			</tr>
		</table>
	</xsl:template>
<!-- 
	<xsl:template match="*[node()]">
		<div class="indent">
			<span class="markup">&lt;</span>
			<span class="start-tag">
				<xsl:value-of select="name(.)" />
			</span>
			<xsl:apply-templates select="@*" />
			<span class="markup">&gt;</span>

			<span class="text">
				<xsl:value-of select="." />
			</span>

			<span class="markup">&lt;/</span>
			<span class="end-tag">
				<xsl:value-of select="name(.)" />
			</span>
			<span class="markup">&gt;</span>
		</div>
	</xsl:template>
 -->
	<xsl:template match="@*">
		<xsl:text> </xsl:text>
		<span class="attribute-name">
			<xsl:value-of select="name(.)" />
		</span>
		<span class="markup">=</span>
		<span class="attribute-value">"<xsl:value-of select="." />"</span>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:if test="normalize-space(.)">
			<div class="indent text">
				<xsl:value-of select="." />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="processing-instruction()">
		<div class="indent pi">
			<xsl:text>&lt;?</xsl:text><xsl:value-of select="name(.)" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="." />
			<xsl:text>?&gt;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template
		match="processing-instruction()[string-length(.) &gt; 50]">
		<table>
			<tr>
				<xsl:call-template name="expander" />
				<td class="pi">
					&lt;?
					<xsl:value-of select="name(.)" />
					<div class="indent expander-content">
						<xsl:value-of select="." />
					</div>
					<xsl:text>?&gt;</xsl:text>
				</td>
			</tr>
		</table>
	</xsl:template>
 
	<xsl:template match="comment()">
		<div class="comment-delimiteur indent">
			<xsl:text>&lt;!--</xsl:text>
			<div class="comment-content"><xsl:value-of select="." /></div>
			<xsl:text>--&gt;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template match="comment()[string-length(.) &gt; 50]">
		<table>
			<tr>
				<xsl:call-template name="expander" />
				<td class="comment">
					<xsl:text>&lt;!--</xsl:text>
					<div class="indent expander-content">
						<xsl:value-of select="." />
					</div>
					<xsl:text>--&gt;</xsl:text>
				</td>
			</tr>
		</table>
	</xsl:template>
 
	<xsl:template name="expander">
		<td class="expander">
			&#x2212;
			<div class="spacer" />
		</td>
	</xsl:template>

</xsl:stylesheet>
