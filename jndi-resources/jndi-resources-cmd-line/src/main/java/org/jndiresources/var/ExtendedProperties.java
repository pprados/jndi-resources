package org.jndiresources.var;

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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Manager .xpath and include in properties files.
 */
// TODO : faire un cache des fichiers XPath en cas de lecture multiple des mêmes fichiers
public final class ExtendedProperties
{
	/**
	 * The logger.
	 */
	private static final Logger log_ = Logger.getLogger(ExtendedProperties.class);

	/**
	 * The XPath factory.
	 */
	private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

	/**
	 * Load XPath file and return the corresponding properties.
	 * 
	 * @param filename The filename.
	 * @param prop The properties to update.
	 * 
	 * @return prop.
	 * 
	 * @throws IOException If the file can't be read.
	 * @throws XPathExpressionException If the XPath is incorrect.
	 * @throws SAXException If the XML file is incorrect.
	 * @throws ParserConfigurationException If the parser is not configured.
	 * 
	 * @see ExtendedProperties#load(File, Properties)
	 */
	public static Properties load(File filename, final Properties prop)
			throws IOException, XPathExpressionException, SAXException,
			ParserConfigurationException
	{
		final String path=filename.getPath();
		if (path.startsWith("~"))
		{
			filename=new File(System.getProperty("user.home")+path.substring(1));
		}
		return load(
			filename.toURI().toURL(), prop);
	}

	/**
	 * Load XPath file and return the corresponding properties, extracted from
	 * different XML files.
	 * <p>
	 * The <code>.xpath</code> files is composed with
	 * <code>key=[xmlns:&lt;prefix&gt;="&lt;uri&gt;",]document('&lt;filename&gt;')&lt;xpath&gt;...</code>
	 * The first optional part, with <code>xmlns</code>, is present to use an
	 * alias in the XPath, to manage a default name-space. The alias can then be
	 * used in the xpath. All others alias presents in the XML documents are
	 * valid in the XPath. The second part must start with
	 * <code>document('filename')</code> to select the file to load. Then, the
	 * XPath is evaluated to set the property.
	 * </p>
	 * 
	 * <p>
	 * For sample, for a web.xml file, if you want to extract the id present in
	 * the tag &lt;web-app/&gt;, you can use :
	 * 
	 * <pre>
	 * id=document('web.xml')/*[namespace-uri()='http://java.sun.com/xml/ns/j2ee' and local-name()='web-app']/@id
	 * </pre>
	 * 
	 * Or, you can register an alias to the j2ee name-space, and use :
	 * 
	 * <pre>
	 * id=xmlns:j2ee=&quot;http://java.sun.com/xml/ns/j2ee&quot;,document('web.xml')/j2ee:web-app/@id
	 * </pre>
	 * 
	 * </p>
	 * If you want to use the alias present in the XML file, use it :
	 * 
	 * <pre>
	 * schema=document('web.xml')//@xsi:schemaLocation
	 * </pre>
	 * 
	 * @param file The .xpath filename.
	 * @param prop The properties to extend.
	 * @return The properties.
	 * 
	 * @throws IOException If the file can't be read.
	 * @throws XPathExpressionException If the XPath is incorrect.
	 * @throws SAXException If the XML file is incorrect.
	 * @throws ParserConfigurationException If the parser is not configured.
	 */
	public static Properties load(final URL file, final Properties prop)
	throws IOException, XPathExpressionException, SAXException,
		ParserConfigurationException
	{
		
		URL filename=file;
		String include=null;
		do
		{
			log_.debug("Load "+filename);
			final Properties p=new Properties();
			InputStream in=filename.openStream();
			if (filename.getPath().endsWith(".xml"))
				p.loadFromXML(in);
			else
				p.load(in);
			in.close();
			for (Iterator i=p.entrySet().iterator();
				i.hasNext();)
			{
				Map.Entry entry=(Map.Entry)i.next();
				final String value=(String)entry.getValue();
				prop.put(entry.getKey(),parseValue(value));
			}
			p.clear();
			include=(String)prop.get("include");
			if (include!=null)
			{
				final String exform=filename.toExternalForm();
				final int idx=exform.lastIndexOf('/');
				prop.remove("include");
				if (include.startsWith("~"))
				{
					include=System.getProperty("user.home")+include.substring(1);
				}
				if (include.startsWith("/"))
					filename=new File(include).toURI().toURL();
				else
					filename=new URL(exform.substring(0,idx+1)+include);
			}
		}
		while (include!=null);
		return prop;
	}
	
	/**
	 * Parse the value. If the value start with xpath:, the next part will be a XPath expression.
	 * 
	 * @param value The value.
	 * @return The value or the XPath extraction.
	 * 
	 * @throws XPathExpressionException If error.
	 * @throws SAXException If error.
	 * @throws IOException If error.
	 * @throws ParserConfigurationException If error.
	 */
	public static final String parseValue(final String value) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException
	{
		if (value.startsWith("xpath:"))
			return parseXPathProperty(value.substring("xpath:".length()));
		return value;
	}

	
	
	/**
	 * Parse a XPath expressions with <code>document('...')</code> and return
	 * the value. The syntax is
	 * <code>[xmlns:&lt;prefix&gt;="&lt;uri&gt;",]document('&lt;filename&gt;')&lt;xpath&gt;</code>
	 * 
	 * @param tokens The token to analyze.
	 * @return The XPath evaluation.
	 * 
	 * @throws IOException If the file can't be read.
	 * @throws XPathExpressionException If the XPath is incorrect.
	 * @throws SAXException If the XML file is incorrect.
	 * @throws ParserConfigurationException If the parser is not configured.
	 */
	public static String parseXPathProperty(String tokens)
			throws XPathExpressionException, SAXException, IOException,
			ParserConfigurationException
	{
		final HashMap namespaceMapping = new HashMap();

		int idxcomma = 0;
		if (tokens.startsWith("xmlns:"))
		{
			idxcomma = tokens.indexOf(',');
			final int idxdots = tokens.indexOf(':');
			final int idxequal = tokens.indexOf('=');
			if ((idxcomma < 0) || (idxdots < 0) || (idxequal < 0)
					|| (idxdots > idxequal))
				throw new XPathExpressionException(
						"XPath must be start with xmlns:alias=\"uri\",document('xxx')...");
			final String alias = tokens.substring(
				idxdots + 1, idxequal);
			String uri = tokens.substring(
				idxequal + 1, idxcomma);
			if (uri.charAt(0) == '"')
				uri = uri.substring(
					1, uri.length() - 1); // Remove quottes
			namespaceMapping.put(
				alias, uri);

			tokens = tokens.substring(idxcomma + 1);
		}

		final int idx2 = tokens.indexOf(')');
		if ((idx2 < 0) || !tokens.startsWith("document('"))
		{
			throw new XPathExpressionException(
					"XPath must be start with [xmlns:alias=\"uri\",]document('xxx')...");
		}

		final String filename = tokens.substring(
			10, idx2 - 1);
		final String xpath = tokens.substring(idx2 + 1);

		// 1. Catch all namespace mapping
		final SAXParserFactory saxParserFactory = SAXParserFactory
				.newInstance();
		saxParserFactory.setNamespaceAware(true);
		URL url;
		try
		{
			url = new URL(filename);
		}
		catch (MalformedURLException e)
		{
			url = new File(filename).toURI().toURL();
		}
		InputStream in=url.openStream();
		try
		{
			saxParserFactory.newSAXParser().parse(
				in, new DefaultHandler()
				{
					public void startPrefixMapping(final String prefix, final String uri)
					{
						namespaceMapping.put(
							prefix, uri);
					}
				});
		}
		finally
		{
			in.close();
		}

		// 2. evaluate XPath
		XPath environnement = XPATH_FACTORY.newXPath();
		environnement.setNamespaceContext(new NamespaceContext()
		{
			public String getNamespaceURI(final String prefix)
			{
				return (String) namespaceMapping.get(prefix);
			}

			public String getPrefix(final String namespaceURI)
			{
				return null;
			}

			public Iterator getPrefixes(final String namespaceURI)
			{
				return Collections.EMPTY_LIST.iterator();
			}
		});
		try
		{
			final String result = environnement.evaluate(
				xpath, new InputSource(in=url.openStream()));
			if (log_.isDebugEnabled())
				log_.debug(tokens + '=' + result);
			return result;
		}
		finally
		{
			in.close();
		}
	}
	
	/**
	 * Utility class.
	 */
	private ExtendedProperties()
	{
		
	}
}
