package com.googlecode.jndiresources.config;

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
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.googlecode.jndiresources.tools.CommandLineException;
import com.googlecode.jndiresources.tools.RecursiveFiles;
import com.googlecode.jndiresources.tools.XMLContext;
import com.googlecode.jndiresources.tools.XSLTools;

/**
 * Generate configurations files for all JEE server.
 * 
 * @author Philippe PRADOS
 */
public final class JNDIConfig
{
	private static final int MILISECOND = 1000;

	/**
	 * Error in command line.
	 */
	private static final int ERROR = -1;

	/**
	 * Help in command line.
	 */
	private static final int ASK_HELP = -2;

	/**
	 * The logger.
	 */
	private static final Logger log_ = Logger.getLogger(JNDIConfig.class);

	/**
	 * The current version.
	 */
	private static final String VERSION = "0.1";

	/**
	 * The jndi-resources.properties.
	 */
	private String data_; // Les données

	/**
	 * The current war/ear or null.
	 */
	private String war_; // Le war/ear

	/**
	 * The current templates.
	 */
	private String templates_;

	/**
	 * The directory to generate.
	 */
	private String packages_;

	/**
	 * XPath to catch //jndi:resources.
	 */
	private static XPathExpression xpathResources_;

	/**
	 * XPath to catch //jndi:resources/@familly.
	 */
	private static XPathExpression xpathFamilly_;

	/**
	 * Initialize XPaths
	 */
	static
	{
		try
		{
			XPath environnement = XMLContext.XPATH_FACTORY.newXPath();
			environnement.setNamespaceContext(XMLContext.NAMESPACE);
			xpathResources_ = environnement.compile("//jndi:resources");
			environnement = XMLContext.XPATH_FACTORY.newXPath();
			environnement.setNamespaceContext(XMLContext.NAMESPACE);
			xpathFamilly_ = environnement.compile(".//jndi:resource/@familly");
		}
		catch (XPathExpressionException e)
		{
			log_.fatal(e);
		}
	};

	/**
	 * Parameters set.
	 */
	public static class ParamsConfig
	{
		/**
		 * The war/ear or null.
		 */
		private String war_;

		/**
		 * The jndi-files.xml.
		 */
		private String data_;

		/**
		 * The ID to manage or null.
		 */
		private String id_;

		/**
		 * The templates to use.
		 */
		private String templates_ = "./templates";

		/**
		 * The package directory to generate.
		 */
		private String packages_;

		/**
		 * Set the data url. Extract the id if found '#'.
		 * 
		 * @param url The data url.
		 */
		private void setData(final String url)
		{
			final int idx = url.indexOf('#');
			if (idx >= 0)
			{
				id_ = url.substring(idx + 1);
				data_ = url.substring(
					0, idx);
			}
			else
				data_ = url;
		}

		/**
		 * Set package.
		 * 
		 * @param packages The package.
		 */
		public void setPackages(final String packages)
		{
			packages_ = packages;
		}

		/**
		 * Return packages.
		 * 
		 * @return Packages.
		 */
		public String getPackages()
		{
			return packages_;
		}

		/**
		 * Return templates.
		 * 
		 * @return Templates.
		 */
		public String getTemplates()
		{
			return templates_;
		}
	}

	/**
	 * Print help.
	 * 
	 * @param out The output stream.
	 */
	private static void help(final PrintStream out)
	{
		out.println("Usage: jndi-config [-h] (-w <war|ear> | -j <jndi-resources.xml>) \\");
		out.println("                   [-t <templates>] -p <destination>");
		out.println("(-w|--war) <war|ear file>     : The jndi-resources descriptions in META-INF");
		out.println("(-j|--jndi-file) <url[#id]>   : The jndi-resources descriptions fragment.");
		out.println("(-t|--templates) <dir>        : The templates transformations to use.");
		out.println("(-p|--packages) <destination> : The destination directory.");
		out.println("-l                            : Log info.");
		out.println("-ll                           : Log debug.");
		out.println("(-h|--help)                   : This help");
	}

	/**
	 * Parse arguments.
	 * 
	 * @param params The parameters set.
	 * @param args The arguments
	 * @param pos The current position.
	 * @return New position, -1 of error or -2 if help is ask.
	 */
	public static int parseArg(final ParamsConfig params, final String[] args, final int pos)
	{
		int position = pos;
		final String arg = args[position];
		if (("-w".equals(arg) || "--war".equals(arg)) && position < args.length - 1)
		{
			params.war_ = args[++position];
		}
		else if (("-j".equals(arg) || "--jndi-file".equals(arg)) && position < args.length - 1)
		{
			params.setData(args[++position]);
		}
		else if (("-t".equals(arg) || "--templates".equals(arg)) && position < args.length - 1)
		{
			params.templates_ = args[++position];
			if (params.templates_.charAt(params.templates_.length() - 1) != File.separatorChar)
				params.templates_ += File.separatorChar;
		}
		else if (("-p".equals(arg) || "--packages".equals(arg)) && position < args.length - 1)
		{
			params.packages_ = args[++position];
			if (params.packages_.charAt(params.packages_.length() - 1) != File.separatorChar)
				params.packages_ += File.separatorChar;
		}
		else if ("-l".equals(arg))
		{
			Logger.getRootLogger().setLevel(
				Level.INFO);
		}
		else if ("-ll".equals(arg))
		{
			Logger.getRootLogger().setLevel(
				Level.DEBUG);
		}
		else if ("-h".equals(arg) || "--help".equals(arg))
		{
			help(System.out);
			return ASK_HELP;
		}
		else
			return ERROR;
		return position;
	}

	/**
	 * Check parameters set.
	 * 
	 * @param params The parameters.
	 * 
	 * @throws CommandLineException If error.
	 */
	public static void checkParams(final ParamsConfig params) throws CommandLineException
	{
		if ((params.data_ == null) && (params.war_ == null))
		{
			throw new CommandLineException("--jndi-file or --war must be set");
		}
		if (params.templates_ == null)
		{
			throw new CommandLineException("--templates must be set");
		}
		if (params.packages_ == null)
		{
			throw new CommandLineException("--packages must be set");
		}
	}

	/**
	 * Parse command line and generate ParamsInstall.
	 * 
	 * @param args Command line
	 * @return ParamsInstall or null
	 * 
	 * @exception CommandLineException If error.
	 */
	public static ParamsConfig parseArgs(final String[] args) throws CommandLineException
	{
		final ParamsConfig params = new ParamsConfig();

		for (int i = 0, j; i < args.length; i = ++j)
		{
			j = parseArg(
				params, args, i);
			if (j < 0)
			{
				if (j == ASK_HELP)
					return null;
				throw new CommandLineException("Unknow parameters " + args[i]);
			}
		}
		checkParams(params);
		return params;
	}

	/**
	 * Generate configurations files.
	 * 
	 * @param params The parameters sets.
	 * 
	 * @throws ParserConfigurationException If error.
	 * @throws SAXException If error.
	 * @throws IOException If error.
	 * @throws XPathExpressionException If error.
	 * @throws TransformerException If error.
	 * @throws ArtifactNotFoundException If artifact is not found.
	 * @throws ResourceDoesNotExistException If artifact can not be load.
	 */
	public JNDIConfig(final ParamsConfig params) throws ParserConfigurationException, SAXException,
			IOException, XPathExpressionException, TransformerException, ArtifactNotFoundException,
			ResourceDoesNotExistException
	{
		final HashSet exclude = new HashSet();
		exclude.add("tools");
		exclude.add("CVS");
		exclude.add(".svn");

		templates_ = params.templates_;
		packages_ = params.packages_;
		war_ = params.war_;

		deletePackages();

		// Extract jndi-resources.xml from component
		if (params.war_ != null)
		{
			data_ = "jar:" + new File(params.war_).toURI().toURL() + "!/META-INF/jndi-resources.xml";
		}
		else
			data_ = params.data_;

		URL data;
		try
		{
			data = new URL(data_);
		}
		catch (MalformedURLException x)
		{
			final int idx = data_.indexOf('#');
			data = new File((idx != -1) ? data_.substring(
				0, idx) : data_).toURI().toURL();
		}
		final InputStream in = data.openStream();
		final Document jndiparams;
		try
		{
			jndiparams = XMLContext.DOC_BUILDER_FACTORY.newDocumentBuilder().parse(
				in);
		}
		finally
		{
			in.close();
		}

		// Find all resources group, and associate an id
		final NodeList result = (NodeList) xpathResources_.evaluate(
			jndiparams, XPathConstants.NODESET);

		boolean doit = false;
		for (int i = 0; i < result.getLength(); ++i)
		{
			final Node home = result.item(i);
			final Node idnode = home.getAttributes().getNamedItem(
				"id");
			final String id = (idnode == null) ? "default" : idnode.getNodeValue();
			if ((params.id_ == null) || id.equals(params.id_))
			{
				doit = true;
				log_.info("Translate " + id + " resources...");
				final NodeList familleNodes = (NodeList) xpathFamilly_.evaluate(
					home, XPathConstants.NODESET);

				// Extract all family to manage
				final List familly = new ArrayList();
				for (int j = 0; j < familleNodes.getLength(); ++j)
				{
					familly.add(familleNodes.item(
						j).getNodeValue());
				}
				Collections.sort(familly);

				// Remove duplicate
				String prev = null;
				for (final Iterator k = familly.iterator(); k.hasNext();)
				{
					final String cur = (String) k.next();
					if (cur.equals(prev))
						k.remove();
					else
						prev = cur;
				}

				// Execute the process.xslt in each family
				final File t = new File(templates_ + File.separatorChar + "process.xslt");
				if (t.canRead())
				{
					log_.info("Process " + t + "...");
					XSLTools.setCwd(new File(t.getParent()));
					xsl(
						new DOMSource(jndiparams), new StreamSource(t), "", "", packages_
								+ File.separatorChar, id);
				}

				final File f = new File(templates_);
				final String[] listAppSrv = f.list();
				if (listAppSrv == null)
					throw new TransformerException("Invalid templates parameter (" + templates_ + ")");
				// Now I have all the families and all applications server to do
				for (int l = 0; l < listAppSrv.length; ++l)
				{
					final String appsrv = listAppSrv[l];
					if (exclude.contains(appsrv))
						continue;

					// Execute the appsrc/process.xslt
					final File tt = new File(templates_ + File.separatorChar + appsrv + File.separatorChar
							+ "process.xslt");
					if (tt.canRead())
					{
						XSLTools.setCwd(new File(tt.getParent()));
						log_.info("Process " + tt + " conversion...");
						xsl(
							new DOMSource(jndiparams), new StreamSource(tt), appsrv, "", packages_ + appsrv
									+ File.separatorChar, id);
					}

					if (new File(templates_, appsrv).isDirectory())
					{
						log_.info("");
						log_.info("*** Generate installs files for " + appsrv);
						for (final Iterator k = familly.iterator(); k.hasNext();)
						{
							String process = (String) k.next();
							final String dir = templates_ + File.separatorChar + appsrv + File.separatorChar
									+ process;
							final File dirFile = new File(dir);
							XSLTools.setCwd(dirFile);
							File xslt = new File(dir, "process.xslt");
							if ("process.xslt".equals(dirFile.getName()))
								continue;
							if (xslt.canRead())
							{
								log_.info("Process " + process + " conversion...");
								xsl(
									new DOMSource(jndiparams), new StreamSource(xslt), appsrv, process,
									packages_ + appsrv + File.separatorChar, id);
							}
							else
							{
								log_.warn("I can't generate resource with " + process + " for " + appsrv
										+ '!');
							}
						}
					}
				}
				break; // Convert one at a time
			}
		}
		if (!doit)
			log_.error("Do nothing !");

	}

	/**
	 * @throws IOException If error.
	 */
	private void deletePackages() throws IOException
	{
		// Deletes all files in packages directory
		try
		{
			File fPackage = new File(packages_);
			if (fPackage.exists())
			{
				RecursiveFiles.recursiveFiles(
					new File(packages_), ".", new RecursiveFiles.Apply()
					{
						public void apply(final File home, final String parent, final File cur)
						{
							cur.delete();
						}
					});
			}
			else
				fPackage.mkdir();
		}
		catch (IOException e)
		{
			log_.error(
				"Impossible to clean the target package directory", e);
			throw (IOException) e.fillInStackTrace();
		}
		catch (Exception e)
		{
			log_.error(
				"Internal error", e);
			throw new RuntimeException("Internal error", e); // NOPMD
		}
	}

	/**
	 * Execute XSL process.
	 * 
	 * @param doc The jndi-resources.xml file.
	 * @param xsl The XSL file.
	 * @param appsrv The application server.
	 * @param familly The jndi familly to manage.
	 * @param targetDir The target directory.
	 * @param id The id to manage.
	 * 
	 * @throws TransformerException If error.
	 * @throws IOException If error.
	 */
	private void xsl(final Source doc, final Source xsl, final String appsrv, final String familly,
			final String targetDir, final String id) throws TransformerException, IOException,
			ArtifactNotFoundException, ResourceDoesNotExistException
	{

		// Get an XSL Transformer object
		Transformer transformer = XMLContext.TRANSFORMER_FACTORY.newTransformer(xsl);
		transformer.setParameter(
			"version", VERSION);
		transformer.setParameter(
			"appsrv", appsrv);
		transformer.setParameter(
			"familly", familly); // jdbc/default
		transformer.setParameter(
			"targetdir", targetDir); // ./tmp
		transformer.setParameter(
			"currentid", id); // abc
		if (war_ != null)
		{
			File war = new File(war_).getCanonicalFile();
			String parent = war.getParent();
			if (parent.charAt(0) != File.separatorChar)
				parent = File.separatorChar + parent;
			transformer.setParameter(
				"parentwar", parent + File.separatorChar); // abc
			transformer.setParameter(
				"war", war.getName()); // abc
		}
		else
		{
			transformer.setParameter(
				"parentwar", "");
			transformer.setParameter(
				"war", "");
		}

		StreamResult output = new StreamResult(System.out);
		transformer.transform(
			doc, output);
	}

	private static final String LINE = "----------------------------------------------------------------------------";

	/**
	 * Parse command line and execute process.
	 * 
	 * @param args Arguments line.
	 */
	public static void main(final String[] args)
	{
		ParamsConfig params;
		try
		{
			if (System.getProperty("jndi.resources.home") == null)
			{
				String path = JNDIConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile();
				System.setProperty(
					"jndi.resources.home", new File(path).getParentFile().getParentFile().getAbsolutePath());
			}

			params = parseArgs(args);
			if (params == null)
				System.exit(0);

			final long start = System.currentTimeMillis();
			log_.info(LINE);
			log_.info("Build install scripts to " + params.packages_ + " with models presents in "
					+ params.templates_);
			log_.info(LINE);

			new JNDIConfig(params);
			log_.info(LINE);
			log_.info("BUILD SUCCESSFUL");
			log_.info(LINE);
			log_.info("Total time :" + (System.currentTimeMillis() - start) / MILISECOND + " second");
			log_.info("Finished at : " + new Date());
			log_.info(LINE);
			System.out.println("Generate install files done.");
		}
		catch (ArtifactNotFoundException e)
		{
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		catch (ResourceDoesNotExistException e)
		{
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		catch (CommandLineException e)
		{
			System.err.println(e.getLocalizedMessage());
			help(System.err);
			System.exit(1);
		}
		catch (Exception e)
		{
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
