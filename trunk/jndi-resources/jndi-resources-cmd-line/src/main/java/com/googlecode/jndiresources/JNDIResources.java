package com.googlecode.jndiresources;

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
import java.io.PrintStream;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.googlecode.jndiresources.config.JNDIConfig;
import com.googlecode.jndiresources.config.JNDIConfig.ParamsConfig;
import com.googlecode.jndiresources.install.JNDIInstall;
import com.googlecode.jndiresources.install.JNDIInstall.ParamsInstall;
import com.googlecode.jndiresources.tools.CommandLineException;

/**
 * Generate and install configurations files to publish JEE resources in JNDI
 * server.
 * 
 * @author Philippe PRADOS
 */
public final class JNDIResources
{
	private static final int MILISECOND = 1000;

	/**
	 * The logger.
	 */
	private static final Logger log_ = Logger.getLogger(JNDIResources.class);

	/**
	 * Help in command line.
	 */
	private static final int ASK_HELP = -2;

	/**
	 * All parameters to custom the process.
	 */
	public static class Params
	{
		/**
		 * Parameters for config process.
		 */
		private final ParamsConfig config_ = new ParamsConfig();

		/**
		 * Parameters for install process.
		 */
		private final ParamsInstall install_ = new ParamsInstall();

		/**
		 * Propagate the packages filename to config and install parameters.
		 * 
		 * @param packages The package filename.
		 */
		public void setPackages(final String packages)
		{
			config_.setPackages(packages);
			install_.setPackages(packages);
		}
	};

	/**
	 * Print help screen.
	 * 
	 * @param out Stream to use.
	 */
	private static void help(final PrintStream out)
	{
		out.println("Usage: jndi-resources [-h] (-w <war|ear> | -j <jndi-resources.xml>) \\");
		out.println("                      [-t <templates>] [-p <destination>] -d <appsrvconfdir=dir> \\");
		out.println("                      [-D<key>=<value>|xpath:<[ns,]xpath>]* \\");
		out.println("                      [--xsl key=value>|xpath:<[ns,]xpath>]* \\");
		out.println("                      [-P <url>]* -p <sourcepackage> \\");
		out.println("                      [-a <jboss|...> -v <version>");
		out.println("(-w|--war) <war|ear file>          : The jndi-resources descriptions in META-INF");
		out.println("(-j|--jndi-file) <url[#id]>        : The jndi-resources descriptions fragment.");
		out.println("(-t|--templates) <dir>             : The templates transformations to use.");
		out.println("(-p|--packages) <destination>      : The temporary configuration directory.");
		out.println("(-d|--dest) <key>=<value>          : Define destination directories");
		out.println("-D<key>=<value>|xpath:<[ns,]xpath> : Define property");
		out.println("--xsl <key>=<value>|xpath:...      : Define XSL parameter");
		out.println("(-P|--properties) <url>            : List of properties files");
		out.println("(-a|--appsrv) <jboss|tomcat...>    : Familly of application server");
		out.println("(-v|--version)                     : Application server version to use");
		out.println("-l                                 : Log info.");
		out.println("-ll                                : Log debug.");
		out.println("(-h|--help)                        : This help");
	}

	/**
	 * Parse command line and generate ParamsInstall.
	 * 
	 * @param args Command line
	 * 
	 * @return ParamsInstall or null for help.
	 * 
	 * @throws CommandLineException If error.
	 * @throws ParserConfigurationException If error.
	 * @throws IOException If error.
	 * @throws SAXException If error.
	 * @throws XPathExpressionException If error.
	 */
	public static Params parseArgs(final String[] args) throws CommandLineException, SAXException,
			IOException, ParserConfigurationException, XPathExpressionException
	{
		final Params params = new Params();
		// By default, use a temporary package directory
		final File tmpDir = mkTempDir();
		params.setPackages(tmpDir.getAbsolutePath() + File.separatorChar);

		int j;
		for (int i = 0; i < args.length; i = ++j)
		{
			final String arg = args[i];
			if ("-h".equals(arg) || "--help".equals(arg))
			{
				help(System.out);
				return null;
			}

			j = JNDIConfig.parseArg(
				params.config_, args, i);
			if (j == ASK_HELP)
				return null;
			if (j < 0)
				j = JNDIInstall.parseArg(
					params.install_, args, i);
			if (j == ASK_HELP)
				return null;
			if (j < 0)
				throw new CommandLineException("Bad parameters " + arg);
		}
		params.install_.setPackages(params.config_.getPackages());
		JNDIConfig.checkParams(params.config_);
		JNDIInstall.checkParams(params.install_);
		return params;
	}

	/**
	 * Invoke configuration and install the a parameters sets.
	 * 
	 * @param params The parameters set.
	 * @throws TransformerException If error
	 * @throws ParserConfigurationException If error
	 * @throws SAXException If error
	 * @throws IOException If error
	 * @throws CommandLineException If error
	 * @throws XPathExpressionException If error
	 */
	private static final String LINE = "----------------------------------------------------------------------------";

	private JNDIResources(final Params params) throws XPathExpressionException, CommandLineException,
			IOException, SAXException, ParserConfigurationException, TransformerException, DOMException,
			InvalidVersionSpecificationException, ArtifactNotFoundException, ResourceDoesNotExistException

	{
		final long start = System.currentTimeMillis();
		log_.info(LINE);
		log_.info("Build install scripts to " + params.config_.getPackages() + " with models presents in "
				+ params.config_.getTemplates());
		log_.info(LINE);

		new JNDIConfig(params.config_);

		log_.info(LINE);
		log_.info("Install to " + params.install_.getAppSrv() + " v" + params.install_.getVersion());
		log_.info(LINE);

		new JNDIInstall(params.install_);

		log_.info(LINE);
		log_.info("INSTALL SUCCESSFUL");
		log_.info(LINE);
		log_.info("Total time :" + (System.currentTimeMillis() - start) / MILISECOND + " second");
		log_.info("Finished at : " + new Date());
		log_.info(LINE);
	}

	/**
	 * Parse command line and execute process.
	 * 
	 * @param args Arguments line.
	 */
	public static void main(final String[] args)
	{
		try
		{
			if (System.getProperty("jndi.resources.home") == null)
			{
				String path = JNDIConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile();
				System.setProperty(
					"jndi.resources.home", new File(path).getParentFile().getParentFile().getAbsolutePath());
			}

			final Params params = parseArgs(args);
			if (params == null)
				System.exit(0);
			new JNDIResources(params);
			System.out.println("Install " + params.install_.getAppSrv() + " version "
					+ params.install_.getVersion() + " done.");
		}
		catch (CommandLineException e)
		{
			log_.error(e.getLocalizedMessage());
			help(System.err);
			System.exit(1);
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
		catch (Exception e)
		{
			log_.error(e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Make temporary directory.
	 * 
	 * @return A temporary directory.
	 * 
	 * @throws IOException If it's impossible to make the directory.
	 */
	private static File mkTempDir() throws IOException
	{
		final String tmpdir = System.getProperty("java.io.tmpdir");
		final File tempFile = File.createTempFile(
			"jndi.", ".packages", new File(tmpdir));
		if (!tempFile.delete())
			throw new IOException();
		if (!tempFile.mkdir())
			throw new IOException();
		return tempFile;
	}

}
