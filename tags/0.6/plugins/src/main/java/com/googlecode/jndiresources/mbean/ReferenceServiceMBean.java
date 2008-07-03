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

import javax.naming.NamingException;

import org.w3c.dom.Element;

/**
 * Interface for Reference service.
 * 
 * @author Philippe PRADOS
 */
public interface ReferenceServiceMBean
{
	/**
	 * Get the JNDI name.
	 * 
	 * @return The JNDI name.
	 */
	String getJNDIName();

	/**
	 * Set the JNDI name.
	 * 
	 * @param jndiName The JNDI name.
	 * @throws NamingException If error.
	 */
	void setJNDIName(String jndiName) throws NamingException;

	/**
	 * Get the type.
	 * 
	 * @return The type.
	 */
	String getType();

	/**
	 * Set the type.
	 * 
	 * @param type The type.
	 * @throws NamingException If error.
	 */
	void setType(String type) throws NamingException;

	/**
	 * Get the factory.
	 * 
	 * @return The factory.
	 */
	String getFactory();

	/**
	 * Set the factory.
	 * 
	 * @param factory The factory.
	 * @throws NamingException If error.
	 */
	void setFactory(String factory) throws NamingException;

	/**
	 * Get the configuration.
	 * 
	 * @return The configuration.
	 */
	Element getConfiguration();

	/**
	 * Set the configuration.
	 * 
	 * @param element The XML fragment.
	 * @throws NamingException If error.
	 */
	void setConfiguration(final Element element) throws NamingException;

	/**
	 * Start the service.
	 * @throws NamingException If error.
	 */
	void start() throws NamingException;

	/**
	 * Stop the service.
	 */
	void stop();

}
