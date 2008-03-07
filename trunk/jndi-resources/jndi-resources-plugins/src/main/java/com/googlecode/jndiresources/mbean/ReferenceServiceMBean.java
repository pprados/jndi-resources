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
 * @version 1.0
 * @since 1.0
 */
public interface ReferenceServiceMBean
{
	String getJNDIName();

	void setJNDIName(String jndiName) throws NamingException;

	String getType();

	void setType(String type) throws NamingException;

	String getFactory();

	void setFactory(String factory) throws NamingException;

	Element getConfiguration();

	void setConfiguration(final Element element) throws NamingException;

	void start() throws NamingException;

	void stop();

}
