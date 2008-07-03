package com.googlecode.jndiresources.install;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.googlecode.jndiresources.config.JNDIConfig;
import com.googlecode.jndiresources.tools.CommandLineException;
import com.googlecode.jndiresources.tools.RecursiveFiles;
import com.googlecode.jndiresources.tools.XMLContext;
import com.googlecode.jndiresources.tools.XSLTools;
import com.googlecode.jndiresources.var.ExtendedProperties;
import com.googlecode.jndiresources.var.VariableReader;

/**
 * Install a JNDI resources in JEE server.
 * 
 * @author Philippe PRADOS
 */
public final class JNDIInstall
{
	/**
	 * A line.
	 */
	private static final String LINE = "----------------------------------------------------------------------------";

	/**
	 * Number of millisecond for one second.
	 */
	private static final int MILLISECOND = 1000;

	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(JNDIInstall.class);

	/**
	 * Error in command line.
	 */
	private static final int ERROR = -1;

	/**
	 * Help in command line.
	 */
	private static final int ASK_HELP = -2;

	/**
	 * The application name.
	 */
	private static final String APPNAME = "jndi-resources";

	/**
	 * The current version.
	 */
	private static final String VERSION = "0.2";

	/**
	 * The plateform.properties file name.
	 */
	private static final String PLATEFORM_PROPERTIES = "plateform.properties";

	/**
	 * The templates.properties file name.
	 */
	private static final String TEMPLATES_PROPERTIES = "templates.properties";

	/**
	 * The versions.xml file name.
	 */
	private static final String VERSIONS_XML = "versions.xml";

	/**
	 * The package directory to use.
	 */
	private String packageDir_; // Le répertoire de base pour la production des

	// fichiers

	/**
	 * The current application server name.
	 */
	private String srvapp_;

	/**
	 * All variables.
	 */
	private Properties prop_;

	/**
	 * The XSL properties.
	 */
	private Properties xslprop_;

	/**
	 * XPath to analyze versions.
	 */
	private static XPathExpression xpathVersion_;

	static
	{
		try
		{
			final XPath environnement = XMLContext.XPATH_FACTORY.newXPath();
			environnement.setNamespaceContext(XMLContext.NAMESPACE);
			xpathVersion_ = environnement.compile("/jndiv:versions/jndiv:appsrv");
		}
		catch (XPathExpressionException e)
		{
			LOG.fatal(e);
			throw new RuntimeException(e); // NOPMD

		}
	}

	/**
	 * ALl parameters to initialize Install process.
	 * 
	 * @author pprados
	 * 
	 */
	public static class ParamsInstall
	{
		/**
		 * Properties in command line.
		 */
		private final Properties cmdlineprop_ = new Properties();

		/**
		 * XSL properties in command line.
		 */
		private final Properties cmdlinexslprop_ = new Properties();

		/**
		 * Target list.
		 */
		private final Properties targetList_ = new Properties();

		/**
		 * Properties list.
		 */
		private final Set propertieslist_ = new HashSet();

		/**
		 * Package.
		 */
		private String package_;

		/**
		 * Application server.
		 */
		private String appsrv_;

		/**
		 * Version or -1.
		 */
		private ArtifactVersion version_;

		/**
		 * Set package.
		 * 
		 * @param packages The package.
		 */
		public void setPackages(final String packages)
		{
			package_ = packages;
		}

		/**
		 * Return application server name.
		 * 
		 * @return Application server name.
		 */
		public String getAppSrv()
		{
			return appsrv_;
		}

		/**
		 * Return application server version.
		 * 
		 * @return Applicatin server version.
		 */
		public ArtifactVersion getVersion()
		{
			return version_;
		}
	};

	/**
	 * Process each files.
	 */
	class ApplyInstall implements RecursiveFiles.Apply
	{
		/**
		 * Current destination.
		 */
		private String destination_;

		/**
		 * {@inheritDoc}
		 */
		public void apply(final File home, final String parent, final File cur)
				throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
				TransformerException
		{
			if (cur.isDirectory())
				return;
			LOG.debug("apply " + cur);
			if (cur.getName().endsWith(
				".jndi"))
				executeJNDIFile(
					cur.getCanonicalFile(), destination_);
			else if (cur.getName().endsWith(
				".link"))
			{
				BufferedReader in = null;
				try
				{
					in = new BufferedReader(new FileReader(cur));
					String line = in.readLine();
					if (line == null)
						throw new IOException("Invalid link file " + cur);
					line = line.replace(
						'/', File.separatorChar);
					final File src = new File(cur.getParent() + File.separatorChar + line.trim())
							.getCanonicalFile();
					final String dst = destination_ + parent + File.separatorChar;
					XSLTools.fileCopy(
						src.getCanonicalPath(), dst);
				}
				finally
				{
					if (in != null)
						in.close();
				}
			}
			else
			{
				final String src = cur.getCanonicalPath();
				final String dst = destination_ + File.separatorChar + parent;
				XSLTools.fileCopy(
					src, dst);
			}
		}
	};

	/**
	 * Print help screen.
	 * 
	 * @param out The output stream.
	 */
	private static void help(final PrintStream out)
	{
		out.println("Usage: jndi-install [-h] -d <appsrvconfdir=dir>");
		out.println("                    [-D<key>=<value>|xpath:<[ns,]xpath>]* \\");
		out.println("                    [--xsl key=value>|xpath:<[ns,]xpath>]* \\");
		out.println("                    [-P <url>]* -p <sourcepackage> -a <jboss|...>[,...]*");
		out.println("(-d|--dest) <key>=<value>              : Define destination directories");
		out.println("-D<key>=<value>|xpath:<[ns,]xpath>     : Define property");
		out.println("--xsl <key>=<value>|xpath:...          : Define XSL variable");
		out.println("(-P|--properties) <url>                : List of properties");
		out.println("(-p|--package) <sourcepackage>         : Sources product with JNDIConfig");
		out.println("(-a|--appsrv) <jboss|tomcat...>[,...]* : Familly of application server");
		out.println("(-v|--version)                         : Application server version to use");
		out.println("-l                                     : Log info.");
		out.println("-ll                                    : Log debug.");
		out.println("(-h|--help)                            : This help");
	}

	/**
	 * Parse arguments.
	 * 
	 * @param params The parameters set.
	 * @param args The arguments
	 * @param pos The current position.
	 * @return New position, -1 of error or -2 if help is ask.
	 * 
	 * @throws CommandLineException If error in argument
	 * @throws SAXException If error in XPath.
	 * @throws IOException If error in XPath.
	 * @throws ParserConfigurationException If error in XPath.
	 * @throws XPathExpressionException If error in XPath.
	 */
	public static int parseArg(final ParamsInstall params, final String[] args, final int pos)
			throws CommandLineException, SAXException, IOException, ParserConfigurationException,
			XPathExpressionException
	{
		int position = pos;
		final String arg = args[position];
		if (arg.startsWith("-D"))
		{

			final int idx = args[position].indexOf('=');
			if (idx < 0)
				throw new CommandLineException("Syntax error with -D parameter");
			final String value = args[position].substring(idx + 1);
			final String param = args[position].substring(
				2, idx);
			params.cmdlineprop_.put(
				param, value);
		}
		else if (("--dest".equals(arg) || ("-d".equals(arg))) && (position < args.length - 1))
		{
			final String v = args[++position];
			final int idx = v.indexOf('=');
			String value = v.substring(idx + 1);
			final String param = v.substring(
				0, idx);
			if (value.charAt(0) == '~')
			{
				value = System.getProperty("user.home") + File.separatorChar + value.substring(1);
			}
			params.targetList_.put(
				param, ExtendedProperties.parseValue(value));
		}
		else if ("--xsl".equals(arg) && (position < args.length - 1))
		{
			final String v = args[++position];
			final int idx = v.indexOf('=');
			final String value = v.substring(idx + 1);
			final String param = v.substring(
				0, idx);
			params.cmdlinexslprop_.put(
				param, ExtendedProperties.parseValue(value));
		}
		else if (("-P".equals(arg) || "--properties".equals(arg)) && position < args.length - 1)
		{
			params.propertieslist_.add(args[++position]);
		}
		else if (("-p".equals(arg) || "--package".equals(arg)) && position < args.length - 1)
		{
			params.package_ = args[++position];
			if (params.package_.charAt(params.package_.length() - 1) != File.separatorChar)
				params.package_ += File.separatorChar;
		}
		else if (("-a".equals(arg) || "--appsrv".equals(arg)) && position < args.length - 1)
		{
			params.appsrv_ = args[++position];
		}
		else if (("-v".equals(arg) || "--version".equals(arg)) && position < args.length - 1)
		{
			params.version_ = new DefaultArtifactVersion(args[++position]);
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
	 * Check arguments.
	 * 
	 * @param params The parameters set.
	 * @throws CommandLineException If error.
	 */
	public static void checkParams(final ParamsInstall params) throws CommandLineException
	{
		if (params.appsrv_ == null)
		{
			throw new CommandLineException("--appsrv must be set");
		}
		if (params.package_ == null)
		{
			throw new CommandLineException("--package must be set");
		}
	}

	/**
	 * Parse command line and generate ParamsInstall.
	 * 
	 * @param args The command line
	 * @return ParamsInstall or null
	 * 
	 * @throws CommandLineException If error.
	 * @throws ParserConfigurationException If error.
	 * @throws IOException If error when read file.
	 * @throws SAXException If error with the SAX parser.
	 * @throws XPathExpressionException If error in XPath.
	 */
	public static ParamsInstall parseArgs(final String[] args) throws CommandLineException, SAXException,
			IOException, ParserConfigurationException, XPathExpressionException
	{
		final ParamsInstall params = new ParamsInstall();

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
	 * Install the packages in application server.
	 * 
	 * @param params The parameters set.
	 * 
	 * @throws CommandLineException If error.
	 * @throws IOException If error.
	 * @throws ParserConfigurationException If error.
	 * @throws SAXException If error.
	 * @throws XPathExpressionException If error.
	 * @throws TransformerException If error.
	 * @throws InvalidVersionSpecificationException If version is invalid.
	 * @throws URISyntaxException If target is not an URL.
	 */
	public JNDIInstall(final ParamsInstall params) throws CommandLineException, IOException, SAXException,
			ParserConfigurationException, XPathExpressionException, TransformerException,
			InvalidVersionSpecificationException, URISyntaxException
	{
		for (StringTokenizer tokens=new StringTokenizer(params.appsrv_,",");tokens.hasMoreTokens();)
		{
			final String curappsrv=tokens.nextToken().trim();
			LOG.info("Profile "+curappsrv);			
			packageDir_ = params.package_;
			srvapp_ = null;
	
			packageDir_ = new File(packageDir_).getCanonicalPath();
			if (params.version_ != null)
			{
				final File versionFile = new File(packageDir_, VERSIONS_XML);
				if (versionFile.canRead())
				{
					final Document versionsDoc = XMLContext.DOC_BUILDER_FACTORY.newDocumentBuilder().parse(
						versionFile);
	
					// Find all resources groups, and associate an id
					final NodeList result = (NodeList) xpathVersion_.evaluate(
						versionsDoc, XPathConstants.NODESET);
	
					for (int i = 0; i < result.getLength(); ++i)
					{
						final Node home = result.item(i);
						final Node nameNode = home.getAttributes().getNamedItem(
							"name");
						if (curappsrv.equals(nameNode.getNodeValue()))
						{
							Node versionsNode = home.getAttributes().getNamedItem(
								"versions");
							VersionRange range = VersionRange.createFromVersionSpec(versionsNode.getNodeValue());
							if (range.containsVersion(params.version_))
							{
								srvapp_ = home.getAttributes().getNamedItem(
									"target").getNodeValue();
								break;
							}
						}
					}
					if (srvapp_ == null)
					{
						throw new CommandLineException("Application server can't be found in versions.xml.");
					}
	
				}
				else if (params.version_ == null)
				{
					throw new CommandLineException("--version can't be set with this templates");
				}
			}
			else
				srvapp_ = curappsrv;
	
			initVariables(params.propertieslist_);
			prop_.putAll(params.cmdlineprop_);
			xslprop_ = params.cmdlinexslprop_;
	
			final ApplyInstall apply = new ApplyInstall();
	
			final File f = new File(packageDir_, srvapp_);
			if (!f.canRead())
				throw new CommandLineException(f + " not found. Update the --appsrv parameter or add a --version");
			final String[] targets = f.list();
			for (int i = 0; i < targets.length; ++i)
			{
				final String target = targets[i];
				if (!new File(packageDir_, srvapp_ + File.separatorChar + target).isDirectory())
					continue;
				String targetDir = (String) params.targetList_.get(target);
				if (targetDir == null)
				{
					throw new CommandLineException("--dest " + target + "=... must be set");
				}
//				targetDir = new File(targetDir).getCanonicalFile().toURI().toURL().toExternalForm();
				targetDir = new File(targetDir).getAbsolutePath()+File.separator;
				File home = new File(packageDir_, srvapp_ + File.separatorChar + target).getCanonicalFile();
				if (home.canRead())
				{
					LOG.info("Install to " + targetDir);
					apply.destination_ = targetDir;
					RecursiveFiles.recursiveFiles(
						home, ".", apply);
				}
			}
			if (tokens.hasMoreTokens())
				LOG.info(LINE);
		}
	}

	/**
	 * Read properties files in this order. - System.properties -
	 * /var/share/jndi-resources/plateform.properties -
	 * $PACKAGE/$APPSRV/templates.properties
	 * ~/.jndi-resources/plateform.properties -
	 * 
	 * @param propertieslist All variables.
	 * 
	 * @throws IOException If error.
	 * @throws XPathExpressionException If error.
	 * @throws SAXException If error.
	 * @throws ParserConfigurationException If error.
	 */
	private void initVariables(final Set propertieslist) throws IOException, XPathExpressionException,
			SAXException, ParserConfigurationException
	{
		File f;
		// Init properties
		prop_ = new Properties();
		prop_.putAll(System.getProperties());

		if (System.getProperty(
			"os.name").indexOf(
			"inux") != -1)
		{
			// Read /usr/share/jndi-resources/templates.properties
			f = new File(File.separatorChar + "usr" + File.separatorChar + "share" + File.separatorChar
					+ APPNAME, PLATEFORM_PROPERTIES);
			if (f.canRead())
				ExtendedProperties.load(
					f, prop_);
		}

		// Read $PACKAGE/$APPSRV/templates.properties
		f = new File(packageDir_ + File.separatorChar + srvapp_, TEMPLATES_PROPERTIES);
		if (f.canRead())
			ExtendedProperties.load(
				f, prop_);

		// Read ~/.jndi-resources/plateform.properties
		f = new File(System.getProperty("user.home") + File.separatorChar + "." + APPNAME,
				PLATEFORM_PROPERTIES);
		if (f.canRead())
			ExtendedProperties.load(
				f, prop_);

		// Parse properties list
		if (propertieslist != null)
		{
			for (Iterator i = propertieslist.iterator(); i.hasNext();)
			{
				final String filename = (String) i.next();
				URL data;
				try
				{
					data = new URL(filename);
				}
				catch (MalformedURLException x)
				{
					data = new File(filename).toURI().toURL();
				}

				ExtendedProperties.load(
					data, prop_);
			}
		}
	}

	/**
	 * Execute JNDI file.
	 * 
	 * @param cur The current file
	 * @param target The target directory.
	 * 
	 * @throws IOException If error.
	 * @throws ParserConfigurationException If error.
	 * @throws SAXException If error.
	 * @throws XPathExpressionException If error.
	 * @throws TransformerException If error.
	 */

	private void executeJNDIFile(final File cur, final String target) throws IOException,
			ParserConfigurationException, SAXException, XPathExpressionException, TransformerException
	{
		final StreamResult output = new StreamResult(System.out);

		final LinkedList pipe = new LinkedList();

		// First read to extract processing-instructions and organize the pipe.
		/**
		 * Catch processing instructions.
		 */
		class ProcessingFilter extends XMLFilterImpl
		{
			/**
			 * Extract processing instruction.
			 * 
			 * @param reader The reader.
			 */
			public ProcessingFilter(final XMLReader reader)
			{
				super(reader);
			}

			/**
			 * {@inheritDoc}
			 */
			public void processingInstruction(final String target, final String data) throws SAXException
			{
				if ("xml-stylesheet".equals(target) || "jndi-stylesheet".equals(target))
				{
					Map params = parseParameters(data);
					String type = (String) params.get("type");
					String href = (String) params.get("href");
					if ((!"text/xsl".equals(type) || (href == null)) && "xml-stylesheet".equals(target))
					{
						throw new SAXException("Invalide xml-stylesheet instruction");
					}
					params.remove("type");
					params.remove("href");
					pipe.addFirst(new Object[]
					{ href, params });
				}
				super.processingInstruction(
					target, data);
			}
		}
		Reader is = new BufferedReader(new FileReader(cur));
		InputSource input = new InputSource(is);
		XMLReader reader = new ProcessingFilter(XMLContext.SAX_PARSER_FACTORY.newSAXParser().getXMLReader());
		SAXSource transformSource = new SAXSource(reader, input);

		Transformer transformer = XMLContext.TRANSFORMER_FACTORY.newTransformer();
		LOG.debug("Transforme " + cur);
		transformer.transform(
			transformSource, new StreamResult(new DevNullOutputStream()));
		is.close();

		// Build the pipe
		TransformerHandler firstPipe = null;
		for (Iterator i = pipe.iterator(); i.hasNext();)
		{
			Object[] p = (Object[]) i.next();
			final String href = (String) p[0];
			final HashMap params = (HashMap) p[1];

			final TransformerHandler handler = XMLContext.SAX_TRANSFORMER_FACTORY
					.newTransformerHandler(new StreamSource(packageDir_ + File.separatorChar + href));
			// Add XSL variables
			transformer = handler.getTransformer();
			transformer.setParameter(
				"version", VERSION);
			transformer.setParameter(
				"targetdir", target);
			for (Iterator xv = xslprop_.keySet().iterator(); xv.hasNext();)
			{
				final String name = (String) xv.next();
				final String value = (String) xslprop_.get(name);
				transformer.setParameter(
					name, value);
			}
			for (Iterator j = params.entrySet().iterator(); j.hasNext();)
			{
				Map.Entry entry = (Map.Entry) j.next();
				transformer.setParameter(
					(String) entry.getKey(), entry.getValue());
			}
			Result res;
			if (firstPipe == null)
				res = output;
			else
				res = new SAXResult(firstPipe);
			handler.setResult(res);
			firstPipe = handler;
		}

		// Second read to execute the pipe.
		is = new VariableReader(new FileReader(cur), prop_);
		input = new InputSource(is);
		final SAXParser saxParser = XMLContext.SAX_PARSER_FACTORY.newSAXParser();
		reader = saxParser.getXMLReader();
		reader.setContentHandler(firstPipe);
		reader.parse(input);
		is.close();

	}

	/**
	 * Output stream to null.
	 */
	private static class DevNullOutputStream extends OutputStream
	{

		/**
		 * {@inheritDoc}
		 */
		public void write(final int b) throws IOException
		{
			// Do nothing
		}
	};

	/**
	 * Parse XSL processing instructions parameters.
	 * 
	 * @param params The processing instruction string
	 * @return A Map with parameters.
	 */
	private Map parseParameters(final String params)
	{
		final HashMap parms = new HashMap();

		for (final StringTokenizer tokens = new StringTokenizer(params, " "); tokens.hasMoreTokens();)
		{
			final String token = tokens.nextToken();
			final int idx = token.indexOf('=');
			final String param = token.substring(
				0, idx);
			String value = token.substring(idx + 1);
			if (value.charAt(0) == '"')
				value = value.substring(1);
			if (value.charAt(value.length() - 1) == '"')
				value = value.substring(
					0, value.length() - 1).trim();

			parms.put(
				param, value);
		}
		return parms;
	}

	/**
	 * Parse command line and execute process.
	 * 
	 * @param args Command line.
	 * @throws Exception If error.
	 */
	public static void main(final String[] args) throws Exception
	{
		ParamsInstall params;
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
			LOG.info(LINE);
			LOG.info("Install to " + params.appsrv_ + " v" + params.version_);
			LOG.info(LINE);
			new JNDIInstall(params);
			LOG.info(LINE);
			LOG.info("INSTALL SUCCESSFUL");
			LOG.info(LINE);
			LOG.info("Total time :" + (System.currentTimeMillis() - start) / MILLISECOND + " second");
			LOG.info("Finished at : " + new Date());
			LOG.info(LINE);
			System.out
					.println("Install " + params.getAppSrv() + " version " + params.getVersion() + " done.");
		}
		catch (VariableReader.VariableNotFound e)
		{
			LOG.error(e.getLocalizedMessage());
		}
		catch (CommandLineException e)
		{
			LOG.error(e.getLocalizedMessage());
			help(System.err);
			System.exit(1);
		}
	}

}
