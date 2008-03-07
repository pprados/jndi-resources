package com.googlecode.jndiresources.mbean;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import com.googlecode.jndiresources.factory.MailSessionFactory;

/**
 * MBean that gives support for JavaMail. Object of class javax.mail.Session
 * will be bound in JNDI with the name provided with method {@link #setJNDIName}.
 * 
 * @author Philippe Prados
 */
public class MailService extends AbstractService
		implements MailServiceMBean
{
	public static final String JNDI_NAME = "java:/Mail";

	private String user_;

	private String password_;

	Reference ref_;

	/** save properties here */
	private Properties props_ = null;

	public MailService() throws NamingException
	{
		super();
		setJNDIName(JNDI_NAME);
	}
	/**
	 * User id used to connect to a mail server.
	 * @throws NamingException 
	 * 
	 * @see #getUser
	 * 
	 * @jmx:managed-attribute
	 */
	public void setUser(final String user) throws NamingException
	{
		if (started_)
		{
			unbind();
			user_ = user;
			rebind();
		}
		else
			user_ = user;
	}

	/**
	 * @jmx:managed-attribute
	 */
	public String getUser()
	{
		return user_;
	}

	/**
	 * Password used to connect to a mail server.
	 * @throws NamingException 
	 * 
	 * @jmx:managed-attribute
	 */
	public void setPassword(final String password) throws NamingException
	{
		if (started_)
		{
			unbind();
			password_ = password;
			rebind();
		}
		else
			password_ = password;
	}

	/**
	 * Password is write only.
	 */
	protected String getPassword()
	{
		return password_;
	}

	/**
	 * @jmx:managed-attribute
	 */
	public String getStoreProtocol()
	{
		if (props_ != null)
			return props_.getProperty("mail.store.protocol");
		else
			return null;
	}

	/**
	 * @jmx:managed-attribute
	 */
	public String getTransportProtocol()
	{
		if (props_ != null)
			return props_.getProperty("mail.transport.protocol");
		else
			return null;
	}

	/**
	 * @jmx:managed-attribute
	 */
	public String getDefaultSender()
	{
		if (props_ != null)
			return props_.getProperty("mail.from");
		else
			return null;
	}

	/**
	 * @jmx:managed-attribute
	 */
	public String getSMTPServerHost()
	{
		if (props_ != null)
			return props_.getProperty("mail.smtp.host");
		else
			return null;
	}

	/**
	 * @jmx:managed-attribute
	 */
	public String getPOP3ServerHost()
	{
		if (props_ != null)
			return props_.getProperty("mail.pop3.host");
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void start() throws NamingException
	{
		if (started_)
			return;
		if (getJNDIName() == null)
			throw new IllegalStateException("jndiName is null");
		if (getConfiguration() == null)
			throw new IllegalStateException("config is null");
		final Properties props = getProperties();

		// Finally create a mail reference
		ref_ = new Reference("javax.mail.Session", MailSessionFactory.class
				.getName(), null);
		for (final Iterator i = props.entrySet().iterator(); i.hasNext();)
		{
			final Map.Entry entry = (Map.Entry) i.next();
			ref_.add(new StringRefAddr((String) entry.getKey(), (String) entry
					.getValue()));
		}
		rebind();

		// now make the properties available
		props_ = props;
		started_ = true;
	}

	protected void rebind() throws NamingException
	{
		final String bindName = getJNDIName();
		Context ctx = new InitialContext();
		try
		{
			Name n = ctx.getNameParser("").parse(bindName);
			while (n.size() > 1)
			{
				final String ctxName = n.get(0);
				try
				{
					ctx = (Context) ctx.lookup(ctxName);
				}
				catch (NameNotFoundException e)
				{
					ctx = ctx.createSubcontext(ctxName);
				}
				n = n.getSuffix(1);
			}
	
			// The helper class NonSerializableFactory uses address type nns, we
			// go on to
			// use the helper class to bind the javax.mail.Session object in
			// JNDI
	
			ctx.bind(
				n.get(0), ref_);
		}
		finally
		{
			ctx.close();
		}
	
		log.info("Mail Service bound to " + bindName);
	}


}
