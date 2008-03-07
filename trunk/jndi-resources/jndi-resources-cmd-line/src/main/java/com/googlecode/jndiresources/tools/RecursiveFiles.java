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

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * Manage recursive files process.
 */
public final class RecursiveFiles
{
	/**
	 * Functor to invoke for each file and directory.
	 * 
	 */
	public interface Apply
	{
		/**
		 * Method to invoke for each file and directory.
		 * 
		 * @param home The home directory.
		 * @param parent The parent directory.
		 * @param cur The current file.
		 * 
		 * @throws XPathExpressionException If error.
		 * @throws SAXException If error.
		 * @throws IOException If error.
		 * @throws ParserConfigurationException If error.
		 * @throws TransformerException If error.
		 */
		void apply(File home, String parent, File cur)
				throws XPathExpressionException, SAXException, IOException,
				ParserConfigurationException, TransformerException;
	}

	/**
	 * Recursive iterate files, and invoke functor.
	 * 
	 * @param home The home directory.
	 * @param parent The parent directory.
	 * @param apply The functor.
	 * 
	 * @throws XPathExpressionException If error.
	 * @throws SAXException If error.
	 * @throws IOException If error.
	 * @throws ParserConfigurationException If error.
	 * @throws TransformerException If error.
	 */
	public static void recursiveFiles(final File home, final String parent,
			final Apply apply) throws XPathExpressionException, SAXException,
			IOException, ParserConfigurationException, TransformerException
	{
		final String[] dir = new File(home, parent).list();
		for (int i = 0; i < dir.length; ++i)
		{
			final File cur = new File(new File(home, parent), dir[i]);
			if (cur.isDirectory())
			{
				recursiveFiles(
					home, parent + File.separatorChar + dir[i], apply);
				apply.apply(
					home, parent, cur);
			}
			else
				apply.apply(
					home, parent, cur);
		}
	}
	
	/**
	 * Utility class.
	 */
	private RecursiveFiles()
	{
		
	}

}
