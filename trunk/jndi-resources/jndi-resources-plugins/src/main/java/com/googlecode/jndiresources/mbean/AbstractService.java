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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Abstract JNDI service.
 * 
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractService
{
	static Logger log = Logger.getLogger(MailService.class);

	private Element config_;

	private String jndiName_;

	protected boolean started_;


	protected AbstractService()
	{
		super();
	}

	/**
	 * Configuration for the mail service.
	 * 
	 * @jmx:managed-attribute
	 */
	public final Element getConfiguration()
	{
		return config_;
	}

	/**
	 * Configuration for the mail service.
	 * @throws NamingException 
	 * 
	 * @jmx:managed-attribute
	 */
	public final void setConfiguration(final Element element) throws NamingException
	{
		if (started_)
		{
			unbind();
			config_ = element;
			rebind();
		}
		else
			config_ = element;
	}

	/**
	 * The JNDI name under which javax.mail.Session objects are bound.
	 * @throws NamingException 
	 * 
	 * @jmx:managed-attribute
	 */
	public final void setJNDIName(final String name) throws NamingException
	{
		if (started_)
		{
			unbind();
			jndiName_ = name;
			rebind();
		}
		else
			jndiName_ = name;

	}

	/**
	 * @jmx:managed-attribute
	 */
	public final String getJNDIName()
	{
		return jndiName_;
	}

	protected Properties getProperties()
	{
		final boolean debug = log.isDebugEnabled();
	
		final Properties props = new Properties();
		if (config_ == null)
		{
			log.warn("No configuration specified; using empty properties map");
			return props;
		}
	
		final NodeList list = config_.getElementsByTagName("property");
		final int len = list.getLength();
	
		for (int i = 0; i < len; i++)
		{
			final Node node = list.item(i);
	
			switch (node.getNodeType())
			{
			case Node.ELEMENT_NODE:
				Element child = (Element) node;
				String name,
				value;
	
				// get the name
				if (child.hasAttribute("name"))
				{
					name = child.getAttribute("name");
				}
				else
				{
					log.warn("Ignoring invalid element; missing 'name' attribute: "
									+ child);
					break;
				}
	
				// get the value
				if (child.hasAttribute("value"))
				{
					value = child.getAttribute("value");
				}
				else
				{
					log.warn("Ignoring invalid element; missing 'value' attribute: "
									+ child);
					break;
				}
	
				if (debug)
				{
					log.debug("setting property " + name + "=" + value);
				}
				props.setProperty(
					name, value);
				break;
	
			case Node.COMMENT_NODE:
				// ignore
				break;
	
			default:
				log.debug("ignoring unsupported node type: " + node);
				break;
			}
		}
	
		if (debug)
		{
			log.debug("Using properties: " + props);
		}
	
		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop()
	{
		try
		{
			if (started_)
				unbind();
			started_ = false;
		}
		catch (NamingException e)
		{
			log.error("Impossible to unbind the instance in "+ getJNDIName());
		}
	}
	
	protected void unbind() throws NamingException
	{
		String bindName = getJNDIName();
	
		if (bindName != null)
		{
			Context ctx = new InitialContext();
			try
			{
				ctx.unbind(bindName);
			}
			finally
			{
				ctx.close();
			}
			log.info("Mail service '" + getJNDIName() + "' removed from JNDI");
		}
	}
	protected abstract void rebind() throws NamingException;

}