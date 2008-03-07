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
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Tool to list from an url. Accept file:, jar: and in the future, http: and
 * https:
 */
public final class URLList
{
	/**
	 * Utility class.
	 */
	private URLList()
	{
		
	}
	/**
	 * List an url.
	 * 
	 * @param dir The URL.
	 * @return An array of URL.
	 * @throws IOException If error.
	 */
	public static URL[] list(final URL dir) throws IOException
	{
		final List urls = new ArrayList();
		final String protocol = dir.getProtocol();
		if ("file".equals(protocol))
		{
			final String file = dir.getFile();
			final File f = new File(file);
			final String[] r = f.list();
			for (int i = 0; i < r.length; ++i)
			{
				final URL url = new URL(dir.toExternalForm() + r[i]);
				System.out.println(url);
				urls.add(url);
			}
		}
		else if ("jar".equals(protocol))
		{
			// jar:file:<jarpath>!<filepathinjar>
			final String file = dir.getFile();
			final int idx = file.indexOf('!');
			String jarfile = file.substring(
				0, idx);
			if (jarfile.startsWith("file:"))
				jarfile = jarfile.substring("file:".length());
			final String jarpath = file.substring(idx + 2);
			System.out.println("manage " + jarfile + " " + jarpath);

			final JarFile jar = new JarFile(jarfile);
			for (final Enumeration e = jar.entries(); e.hasMoreElements();)
			{
				final JarEntry entry = (JarEntry) e.nextElement();
				if (entry.getName().startsWith(
					jarpath))
				{

					final URL url = new URL(dir.toExternalForm()
							+ entry.getName().substring(
								jarpath.length()));
					urls.add(url);
				}
			}
		}
		else if ("http".equals(protocol))
		{
			// TODO : Faire la liste du protocole http via WebDav
			throw new IllegalArgumentException("TODO");
		}
		else if ("ftp".equals(protocol))
		{
			// TODO : Faire la liste du protocole ftp via api ftp
			throw new IllegalArgumentException("TODO");
		}
		final URL[] array = new URL[urls.size()];
		return (URL[]) urls.toArray(array);
	}
	/*
	 * public static void main(String[] args) throws IOException { String
	 * rep=ResourcesList.class.getClassLoader().getResource("java/lang/String.class").toExternalForm();
	 * rep=rep.substring(0,rep.lastIndexOf('/')); System.out.println(list(new
	 * URL(rep))); // jar:
	 * 
	 * System.out.println(list(new URL("file:/home/pprados/"))); // File:
	 * 
	 * System.out.println(list(new URL("http:/home/pprados/"))); // File: }
	 */
}
