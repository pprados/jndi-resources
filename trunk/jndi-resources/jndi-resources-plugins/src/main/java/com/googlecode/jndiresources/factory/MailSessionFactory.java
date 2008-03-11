package com.googlecode.jndiresources.factory;

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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * Mail session object factory.
 * 
 * @author Philippe PRADOS
 */
public class MailSessionFactory implements ObjectFactory
{

	/**
	 * {@inheritDoc}
	 */
	public Object getObjectInstance(Object refObj, Name name, Context context,
			java.util.Hashtable env) throws Exception
	{

		// Return null if we cannot create an object of the requested type
		final Reference ref = (Reference) refObj;
		if (!ref.getClassName().equals("javax.mail.Session"))
			return (null);

		// Create a new Session inside a doPrivileged block, so that JavaMail
		// can read its default properties without throwing Security
		// exceptions.
		return AccessController.doPrivileged(new PrivilegedAction()
		{
			public Object run()
			{

				// Create the JavaMail properties we will use
				Properties props = new Properties();
				props.put(
					"mail.transport.protocol", "smtp");
				props.put(
					"mail.smtp.host", "localhost");

				String password = null;

				Enumeration attrs = ref.getAll();
				while (attrs.hasMoreElements())
				{
					RefAddr attr = (RefAddr) attrs.nextElement();
					if ("factory".equals(attr.getType()))
					{
						continue;
					}

					if ("password".equals(attr.getType()))
					{
						password = (String) attr.getContent();
						continue;
					}

					props.put(
						attr.getType(), (String) attr.getContent());
				}

				Authenticator auth = null;
				if (password != null)
				{
					String user = props.getProperty("mail.smtp.user");
					if (user == null)
					{
						user = props.getProperty("mail.user");
					}

					if (user != null)
					{
						final PasswordAuthentication pa = new PasswordAuthentication(
								user, password);
						auth = new Authenticator()
						{
							protected PasswordAuthentication getPasswordAuthentication()
							{
								return pa;
							}
						};
					}
				}

				// Create and return the new Session object
				Session session = Session.getInstance(
					props, auth);
				return (session);

			}
		});

	}

}
