package org.jndiresources.tools;

/*
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
 */

import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.xpath.XPathFactory;

/**
 * Builder for XML manipulations.
 */
public final class XMLContext
{
	/**
	 * name-space alias.
	 */
	private static final String ALIAS = "jndi";

	/**
	 * Name-space URI.
	 */
	private static final String NAMESPACESTR = "http://www.jndi-resources.org/1.0/";

	/**
	 * name-space alias for versions.
	 */
	private static final String ALIAS_VERSION = "jndiv";

	/**
	 * Name-space URI for versions.
	 */
	private static final String NAMESPACESTR_VERSION = "http://www.jndi-resources.org/1.0/versions";

	/**
	 * The name space context.
	 */
	public static final NamespaceContext NAMESPACE = new NamespaceContext()
	{
		public String getPrefix(final String namespaceURI)
		{
			if (NAMESPACESTR_VERSION.equals(namespaceURI)) return ALIAS_VERSION;
			return (NAMESPACESTR.equals(namespaceURI)) ? ALIAS : null;
		}

		public String getNamespaceURI(final String prefix)
		{
			if (ALIAS_VERSION.equals(prefix)) return NAMESPACESTR_VERSION;
			return (ALIAS.equals(prefix)) ? NAMESPACESTR : null;
		}

		public Iterator getPrefixes(final String namespaceURI)
		{
			return Collections.EMPTY_LIST.iterator();
		}
		
	};

	/**
	 * The XML document builder factory.
	 */
	public static final DocumentBuilderFactory DOC_BUILDER_FACTORY;

	/**
	 * The XPath factory.
	 */
	public static final XPathFactory XPATH_FACTORY;

	/**
	 * The transformer factory.
	 */
	public static final TransformerFactory TRANSFORMER_FACTORY;

	/**
	 * The SAX parser factory.
	 */
	public static final SAXParserFactory SAX_PARSER_FACTORY;

	/**
	 * The SAX transformer factory.
	 */
	public static final SAXTransformerFactory SAX_TRANSFORMER_FACTORY;

	/**
	 * Initialize factories.
	 */
	static
	{
//		try
		{
			System.setProperty(
				"javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
			System.setProperty(
				"javax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom",
				"net.sf.saxon.xpath.XPathFactoryImpl");
			DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
//			DOC_BUILDER_FACTORY.setValidating(true);
			DOC_BUILDER_FACTORY.setAttribute(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
			DOC_BUILDER_FACTORY.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", 
				new File("src/main/xsd/jndi-resources-versions.xsd"));			
	
			XPATH_FACTORY = XPathFactory.newInstance();
			TRANSFORMER_FACTORY = TransformerFactory.newInstance();
			SAX_PARSER_FACTORY = SAXParserFactory.newInstance();
			SAX_PARSER_FACTORY.setNamespaceAware(true);
//			SAX_PARSER_FACTORY.setValidating(true);
//			SAX_PARSER_FACTORY.setFeature("http://xml.org/sax/features/validation",true); 
//			SAX_PARSER_FACTORY.setFeature("http://apache.org/xml/features/validation/schema",true);
//			SAX_PARSER_FACTORY.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true); 
			SAX_TRANSFORMER_FACTORY = 
				(SAXTransformerFactory)TransformerFactory.newInstance();
		}
//		catch (SAXNotRecognizedException e)
//		{
//			throw new IllegalArgumentException(e);
//		}
//		catch (SAXNotSupportedException e)
//		{
//			throw new IllegalArgumentException(e);
//		}
//		catch (ParserConfigurationException e)
//		{
//			throw new IllegalArgumentException(e);
//		}
	};
	
	/**
	 * Not used.
	 */
	private XMLContext()
	{
		
	}
}
