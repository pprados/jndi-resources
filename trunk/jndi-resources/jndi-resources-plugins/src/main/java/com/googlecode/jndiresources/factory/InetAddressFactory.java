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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * Internet adresse object factory.
 * 
 * @author Philippe PRADOS
 */
public class InetAddressFactory implements ObjectFactory
{
	/**
	 * {@inheritDoc}
	 */
	public Object getObjectInstance(Object obj, Name name,
			Context nameCtx, Hashtable environment)
			throws NamingException
	{
		try
		{
			if (obj instanceof Reference)
			{
				Reference ref = (Reference) obj;
				if (!"java.net.InetAddress".equals(ref.getClassName()))
						throw new NamingException("Invalide type "+ref.getClassName());
				return InetAddress.getByName((String)ref.get("value").getContent());
			}
			return null;
		}
		catch (UnknownHostException e)
		{
            NamingException ne = new NamingException(e.getMessage());
            ne.setRootCause(e);
            throw ne;
		}
	}

}
