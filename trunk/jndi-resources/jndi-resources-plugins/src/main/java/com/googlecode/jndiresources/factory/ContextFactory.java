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

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * Factory to build a jndi context.
 * 
 * @version 1.0
 * @since 1.0
 */
public class ContextFactory implements ObjectFactory
{
	/**
	 * {@inheritDoc}
	 */
	public Object getObjectInstance(final Object obj, final Name name,
			final Context nameCtx, final Hashtable environment)
			throws NamingException
	{
		if (obj instanceof Reference)
		{
			final Reference ref = (Reference) obj;
			if (!"javax.naming.Context".equals(ref.getClassName()))
					throw new NamingException("Invalide type "+ref.getClassName());
			final Properties prop=new Properties();
			try
			{
//				prop.load(new StringReader((String)ref.get("value").getContent()));
				prop.load(new StringBufferInputStream((String)ref.get("value").getContent()));
				return new InitialContext(prop);
			} catch (IOException e)
			{
				throw new NamingException(e.getLocalizedMessage()); // NOPMD
			}
		}
		return null;
	}

}
