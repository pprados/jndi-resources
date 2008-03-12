package com.googlecode.jndiresources.tools;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.wagon.ResourceDoesNotExistException;

import com.googlecode.jndiresources.maven.ManageArtifact;
import com.googlecode.jndiresources.maven.MavenException;

/**
 * Tools for XSL scripts.
 * 
 * @author Philippe PRADOS
 */
public final class XSLTools
{
	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(XSLTools.class);

	/**
	 * Buffer size.
	 */
	private static final int BUFSIZE = 8192;

	/**
	 * A thread local variable to remember the current working directory.
	 */
	private static ThreadLocal cwd_ = new ThreadLocal(); // Context for

	// current path,
	// etc.

	/**
	 * Set the current working directory.
	 * 
	 * @param cwd The current working directory.
	 */
	public static void setCwd(final File cwd)
	{
		cwd_.set(cwd);
	}

	/**
	 * Return url for artifact.
	 * 
	 * @param artifact The syntax is &lt;groupid&gt;:&lt;artifactid&gt;:&lt;version&gt;
	 * @return The file path.
	 * 
	 * @throws MavenException If error.
	 * @throws ArtifactNotFoundException If artifact is not found.
	 * @throws ResourceDoesNotExistException If resource can not be loaded.
	 */
	public static String getArtifact(final String artifact) throws MavenException, ArtifactNotFoundException,
			ResourceDoesNotExistException
	{
		return new Action()
		{

			public String run() throws ArtifactNotFoundException, ResourceDoesNotExistException,
					MavenException
			{
				return ManageArtifact.getArtifact(artifact);
			}

		}.run();
	}

	/**
	 * This interface is a bridge to accept a code without all jar files.
	 */
	private interface Action
	{
		/**
		 * Method to run.
		 * @return Result
		 * @throws ArtifactNotFoundException If error.
		 * @throws ResourceDoesNotExistException If error.
		 * @throws MavenException If error.
		 * @throws IOException If error.
		 */
		String run() throws ArtifactNotFoundException, ResourceDoesNotExistException, MavenException,
				IOException;
	};

	/**
	 * Maven copy a file.
	 * 
	 * @param artifact The syntax is &lt;groupid&gt;:&lt;artifactid&gt;:&lt;version&gt;
	 * @param toStrFile Target file.
	 * @return The file name of artifact in local repository.
	 * 
	 * @throws IOException If error.
	 * @throws MavenException If the artifact is not found or can't be download
	 *             in local repository.
	 * @throws ArtifactNotFoundException If artifact is not found.
	 * @throws ResourceDoesNotExistException If resource can not be loader.
	 */
	public static String mavenCopy(final String artifact, final String toStrFile)
			throws ArtifactNotFoundException, ResourceDoesNotExistException, MavenException, IOException
	{
		return new Action()
		{

			public String run() throws ArtifactNotFoundException, ResourceDoesNotExistException,
					MavenException, IOException
			{
				final String url = getArtifact(artifact);
				fileCopyIfExist(
					url, toStrFile, true);
				return url.substring(url.lastIndexOf('/') + 1);
			}

		}.run();

	}

	/**
	 * Copy a file.
	 * 
	 * @param fromStrFile Source file.
	 * @param toStrFile Target file.
	 * 
	 * @throws IOException If error.
	 */
	public static void fileCopy(final String fromStrFile, final String toStrFile) throws IOException
	{
		fileCopyIfExist(
			fromStrFile, toStrFile, true);
	}

	/**
	 * Copy a file if the source exist.
	 * 
	 * @param fromStrFile Source file.
	 * @param toStrFile Target file.
	 * 
	 * @throws IOException If error.
	 */
	public static void fileCopyIfExist(final String fromStrFile, final String toStrFile) throws IOException
	{
		fileCopyIfExist(
			fromStrFile, toStrFile, false);
	}

	/**
	 * Copy a file if the source exist.
	 * 
	 * @param fromStrFile Source file.
	 * @param toStrFile Target file.
	 * @param exist Flag to check or not the existance of source.
	 * 
	 * @throws IOException If error.
	 */
	public static void fileCopyIfExist(String fromStrFile, String toStrFile, final boolean exist)
			throws IOException
	{
		LOG.debug("fileCopyIfExist(" + fromStrFile + ", " + toStrFile + ", " + exist + ", " + exist
				+ ") cwd=" + cwd_.get());
		if (toStrFile.startsWith("file:"))
			toStrFile = new URL(toStrFile).getFile();
		fromStrFile = fromStrFile.replace(
			'/', File.separatorChar);
		toStrFile = toStrFile.replace(
			'/', File.separatorChar);
		final File fromFile;
		if (fromStrFile.charAt(0) == File.separatorChar)
			fromFile = new File(fromStrFile);
		else
			fromFile = new File((File) cwd_.get(), fromStrFile);
		if (!fromFile.exists() && !exist)
			return;

		File toFile = new File(toStrFile).getCanonicalFile();
		if (toStrFile.endsWith(File.separator))
			toFile.mkdirs();

		if (toFile.isDirectory())
			toFile = new File(toFile, fromFile.getName());
		else
		{
			final File parent = toFile.getParentFile();
			if (!parent.exists())
				parent.mkdirs();
		}
		if (toFile.exists() && toFile.lastModified() >= fromFile.lastModified())
		{
			return;
		}
		LOG.info("Write " + toFile);

		FileInputStream from = null;
		FileOutputStream to = null;
		try
		{
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			toFile.setLastModified(fromFile.lastModified());
			final byte[] buffer = new byte[BUFSIZE];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(
					buffer, 0, bytesRead);
		}
		finally
		{
			if (from != null)
				try
				{
					from.close();
				}
				catch (IOException e)
				{
					LOG.warn(e);
				}
			if (to != null)
				try
				{
					to.close();
				}
				catch (IOException e)
				{
					LOG.warn(e);
				}
		}
	}

	/**
	 * Make a link file.
	 * 
	 * @param fromStrFile The source file to copy
	 * @param toStrFile The target file to copy
	 * @param link The target link to reference the target file.
	 * 
	 * @throws IOException If error.
	 */
	public static void mkLink(String fromStrFile, String toStrFile, final String link) throws IOException
	{
		LOG.debug("mkLink(" + fromStrFile + ", " + toStrFile + ", " + link + ")");
		fromStrFile = fromStrFile.replace(
			'/', File.separatorChar);
		toStrFile = toStrFile.replace(
			'/', File.separatorChar);

		fileCopy(
			fromStrFile, toStrFile);
		mkLink(
			toStrFile, link);
	}

	/**
	 * Make a link file.
	 * 
	 * @param toStrFile The target file to copy
	 * @param link The target link to reference the target file.
	 * 
	 * @throws IOException If error.
	 */
	public static void mkLink(String toStrFile, final String link) throws IOException
	{
		LOG.debug("mkLink(" + toStrFile + "," + link + ")");

		BufferedWriter out = null;
		try
		{
			String linkname = link + ".link";
			toStrFile = new File(toStrFile).getCanonicalPath();
			if (toStrFile.charAt(0) != File.separatorChar)
				toStrFile = File.separatorChar + toStrFile;
			linkname = new File(linkname).getCanonicalPath();
			if (linkname.charAt(0) != File.separatorChar)
				linkname = File.separatorChar + linkname;
			new File(new File(linkname).getParent()).mkdirs();
			out = new BufferedWriter(new FileWriter(linkname));
			out.write(calcRelativeLink(
				toStrFile, linkname));
			out.newLine();
		}
		finally
		{
			if (out != null)
				out.close();
		}
	}

	/**
	 * Calculate the relative filename for make a link.
	 * 
	 * @param fileOne Must be canonical
	 * @param fileTow Must be canonical
	 * 
	 * @return The relative filename.
	 */
	private static String calcRelativeLink(final String fileOne, final String fileTow)
	{
		// 1. Find similar parts
		final StringBuffer ln = new StringBuffer();
		final StringTokenizer token1 = new StringTokenizer(fileOne, File.separator);
		final StringTokenizer token2 = new StringTokenizer(fileTow, File.separator);
		int baseLength = 0;
		String tok1;
		String tok2;
		for (;;)
		{
			tok1 = token1.nextToken();
			tok2 = token2.nextToken();
			if (!tok1.equals(tok2))
				break;
			baseLength += tok1.length() + 1;
		}
		// 2. Extract end of token1;
		for (; token2.hasMoreTokens(); token2.nextToken())
		{
			ln.append(
				"..").append(
				File.separatorChar);
		}
		ln.append(fileOne.substring(baseLength + 1));
		return ln.toString().replace(
			File.separatorChar, '/');
	}

	/**
	 * Info trace.
	 * 
	 * @param msg The message.
	 */
	public static void info(final String msg)
	{
		LOG.info(msg);
	}

	/**
	 * Debug trace.
	 * 
	 * @param msg The message.
	 */
	public static void debug(final String msg)
	{
		LOG.debug(msg);

	}

	/**
	 * Warn trace.
	 * 
	 * @param msg The message.
	 */
	public static void warn(final String msg)
	{
		LOG.warn(msg);
	}

	/**
	 * Error trace.
	 * 
	 * @param msg The message.
	 */
	public static void error(final String msg)
	{
		LOG.error(msg);
	}

	/**
	 * Utility.
	 */
	private XSLTools()
	{

	}
}
