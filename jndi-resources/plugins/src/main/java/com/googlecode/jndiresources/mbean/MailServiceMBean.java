/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

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

package com.googlecode.jndiresources.mbean;

import javax.naming.NamingException;

import org.w3c.dom.Element;

/**
 * MBean interface for Mail service.
 * 
 * @author Philippe PRADOS
 */
public interface MailServiceMBean
{
	/**
	 * Set the user name.
	 * @param user The user name.
	 * @throws NamingException If error.
	 */
	void setUser(final String user) throws NamingException;

	/**
	 * Get the user name.
	 * @return The user name.
	 */
	String getUser();

	/**
	 * Set the password.
	 * 
	 * @param password The password.
	 * @throws NamingException If error.
	 */
	void setPassword(final String password) throws NamingException;

	/**
	 * Set the configuration.
	 * 
	 * @param element The configuration.
	 * @throws NamingException If error.
	 */
	void setConfiguration(final Element element) throws NamingException;

	/**
	 * Get configuration.
	 * 
	 * @return The configuration.
	 */
	Element getConfiguration();

	/**
	 * Set the JNDI name.
	 * 
	 * @param name The JNDI name.
	 * @throws NamingException If error.
	 */
	void setJNDIName(final String name) throws NamingException;

	/**
	 * Get the JNDI name.
	 * @return The JNDI name.
	 */
	String getJNDIName();

	/**
	 * Get the store protocol.
	 * 
	 * @return The store protocol.
	 */
	String getStoreProtocol();

	/**
	 * Get the transport protocol.
	 * 
	 * @return The transport protocol.
	 */
	String getTransportProtocol();

	/**
	 * Get the default sender.
	 * 
	 * @return The default sender.
	 */
	String getDefaultSender();

	/**
	 * Get the SMTP server host.
	 * 
	 * @return The SMTP server host.
	 */
	String getSMTPServerHost();

	/**
	 * Get the POP3 server host.
	 * 
	 * @return The POP3 server host.
	 */
	String getPOP3ServerHost();

	/**
	 * Start the service.
	 * 
	 * @throws NamingException If error.
	 */
	void start() throws NamingException;

	/**
	 * Stop the service.
	 */
	void stop();

}
