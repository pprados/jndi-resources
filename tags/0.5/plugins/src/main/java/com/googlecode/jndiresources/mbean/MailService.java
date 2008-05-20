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

import org.apache.log4j.Logger;

import com.googlecode.jndiresources.factory.MailSessionFactory;

/**
 * MBean that gives support for JavaMail. Object of class javax.mail.Session
 * will be bound in JNDI with the name provided with method {@link #setJNDIName}.
 * 
 * @author Philippe PRADOS
 */
public class MailService extends AbstractService
		implements MailServiceMBean
{
	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(MailService.class);

	/**
	 * The JNDI name.
	 */
	public static final String JNDI_NAME = "java:/Mail";

	/**
	 * The user name.
	 */
	private String user_;

	/**
	 * The password.
	 */
	private String password_;

	/**
	 * The reference.
	 */
	private Reference ref_;

	/** 
	 * The properties.
	 */
	private Properties props_ = null;

	/**
	 * The mail service. Accept to be used in a remote JNDI.
	 * 
	 * @throws NamingException If error.
	 */
	public MailService() throws NamingException
	{
		super();
		setJNDIName(JNDI_NAME);
	}
	/**
	 * User id used to connect to a mail server.
	 * 
	 * @param user The user.
	 * @throws NamingException If error.
	 * 
	 * @see #getUser
	 * 
	 * @jmx:managed-attribute
	 */
	public void setUser(final String user) throws NamingException
	{
		if (isStarted())
		{
			unbind();
			user_ = user;
			rebind();
		}
		else
			user_ = user;
	}

	/**
	 * Get the user name.
	 * @return user name.
	 * @jmx:managed-attribute
	 */
	public String getUser()
	{
		return user_;
	}

	/**
	 * Password used to connect to a mail server.
	 * 
	 * @param password The password.
	 * 
	 * @throws NamingException  If error.
	 * 
	 * @jmx:managed-attribute
	 */
	public void setPassword(final String password) throws NamingException
	{
		if (isStarted())
		{
			unbind();
			password_ = password;
			rebind();
		}
		else
			password_ = password;
	}

	/**
	 * Get the password.
	 * Password is write only.
	 * @return the password.
	 */
	protected String getPassword()
	{
		return password_;
	}

	/**
	 * Get the store protocol.
	 * 
	 * @return the store protocol.
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
	 * Get the transport protocol.
	 * 
	 * @return the transport protocol.
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
	 * Get the default sender.
	 * 
	 * @return the default sender.
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
	 * Get the SMTP server host.
	 * 
	 * @return the SMTP server host.
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
	 * Get the POP3 server host.
	 * 
	 * @return the POP3 server host.
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
		if (isStarted())
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
		setStarted(true);
	}

	/**
	 * {@inheritDoc}
	 */
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
	
		LOG.info("Mail Service bound to " + bindName);
	}


}
