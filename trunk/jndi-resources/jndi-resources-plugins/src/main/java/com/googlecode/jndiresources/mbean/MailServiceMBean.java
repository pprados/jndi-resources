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
 * @version 1.0
 * @since 1.0
 */
public interface MailServiceMBean
{
	void setUser(final String user) throws NamingException;

	String getUser();

	void setPassword(final String password) throws NamingException;

	Element getConfiguration();

	void setConfiguration(final Element element) throws NamingException;

	void setJNDIName(final String name) throws NamingException;

	String getJNDIName();

	String getStoreProtocol();

	String getTransportProtocol();

	String getDefaultSender();

	String getSMTPServerHost();

	String getPOP3ServerHost();

	void start() throws NamingException;

	void stop();

}
