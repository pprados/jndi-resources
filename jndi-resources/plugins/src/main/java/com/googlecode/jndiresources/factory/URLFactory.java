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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * URL object factory.
 * 
 * @author Philippe PRADOS
 */
public class URLFactory implements ObjectFactory
{
	/**
	 * {@inheritDoc}
	 */
	public Object getObjectInstance(Object obj, javax.naming.Name name, Context nameCtx, Hashtable environment)
			throws NamingException
	{
		try
		{
			if (obj instanceof Reference)
			{
				Reference ref = (Reference) obj;
				if (!"java.net.URL".equals(ref.getClassName()))
					throw new NamingException("Invalide type " + ref.getClassName());
				return new URL((String) ref.get(
					"value").getContent());
			}
			return null;
		}
		catch (MalformedURLException e)
		{
			NamingException ne = new NamingException(e.getMessage());
			ne.setRootCause(e);
			throw ne;
		}

	}

}
