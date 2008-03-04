package org.jndiresources.mbean;

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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.log4j.Logger;

/**
 * Reference MBean service.
 *  
 * @version 1.0
 * @since 1.0
 */
public class ReferenceService extends AbstractService
	implements ReferenceServiceMBean
{
	private static Logger log = Logger.getLogger(ReferenceService.class);

	private String type_;

	private String factory_;

	/**
	 * @param type The type of the resource.
	 * 
	 * @jmx:managed-attribute
	 */
	public void setType(final String type) throws NamingException
	{
		if (started_)
		{
			unbind();
			type_ = type;
			rebind();
		}
		else
			type_ = type;
	}

	/**
	 * @return The type of the resource.
	 * 
	 * @jmx:managed-attribute
	 */
	public String getType()
	{
		return type_;
	}

	/**
	 * @param factory The factory.
	 * 
	 * @jmx:managed-attribute
	 */
	public void setFactory(final String factory) throws NamingException
	{
		if (started_)
		{
			unbind();
			factory_ = factory;
			rebind();
		}
		else
			factory_ = factory;
	}

	/**
	 * @return The factory.
	 * 
	 * @jmx:managed-attribute
	 */
	public String getFactory()
	{
		return factory_;
	}

	/**
	 * {@inheritDoc}
	 */
	public void start() throws NamingException
	{
		if (getJNDIName() == null)
			throw new IllegalStateException("jndiName is null");
		if (type_ == null)
			throw new IllegalStateException("type is null");
		if (factory_ == null)
			throw new IllegalStateException("factory is null");
		rebind();
		started_ = true;
	}

	/**
	 * Rebind the resource.
	 * 
	 * @throws NamingException If error.
	 */
	protected void rebind() throws NamingException
	{
		final String bindName = getJNDIName();
		final Reference ref = new Reference(type_, factory_, null);
		for (final Iterator i = getProperties().entrySet().iterator(); i.hasNext();)
		{
			final Map.Entry entry = (Map.Entry) i.next();
			ref.add(new StringRefAddr((String) entry.getKey(), (String) entry
					.getValue()));
		}

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

			ctx.bind(n.get(0), ref);
		}
		finally
		{
			ctx.close();
		}

		log.info("Instance bound to " + bindName);
	}
}
