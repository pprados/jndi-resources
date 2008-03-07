package org.jndiresources.maven;

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

import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * This is a simple logger manager that will only write the logging statements
 * to the console. <p/> Sample configuration:
 * 
 * <pre>
 * &lt;logging&gt;
 *   &lt;implementation&gt;org.codehaus.plexus.logging.ConsoleLoggerManager&lt;/implementation&gt;
 *   &lt;logger&gt;
 *     &lt;threshold&gt;DEBUG&lt;/threshold&gt;
 *   &lt;/logger&gt;
 * &lt;/logging&gt;
 * </pre>
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: MavenEmbedderLoggerManager.java 292888 2005-10-01 01:17:36Z
 *          jvanzyl $
 */
public class MavenEmbedderLoggerManager extends AbstractLoggerManager implements
		LoggerManager, Initializable
{
	/**
	 * Message of this level or higher will be logged. <p/> This field is set by
	 * the plexus container thus the name is 'threshold'. The field
	 * currentThreshold contains the current setting of the threshold.
	 */
	private String threshold_ = "info";

	private int currentThreshold_;

	private final Logger curlogger_; // NOPMD

	public MavenEmbedderLoggerManager(final Logger logger)
	{
		super();
		curlogger_ = logger;
	}

	public void initialize()
	{
		debug("Initializing ConsoleLoggerManager: " + this.hashCode() + ".");

		currentThreshold_ = parseThreshold(threshold_);

		if (currentThreshold_ == -1)
		{
			debug("Could not parse the threshold level: '" + threshold_
					+ "', setting to debug.");
			currentThreshold_ = Logger.LEVEL_DEBUG;
		}
	}

	public void setThreshold(final int currentThreshold)
	{
		this.currentThreshold_ = currentThreshold;
	}

	/**
	 * @return Returns the threshold.
	 */
	public int getThreshold()
	{
		return currentThreshold_;
	}

	public void setThreshold(final String role, final String roleHint,
			final int threshold)
	{
		// Empty
	}

	public int getThreshold(final String role, final String roleHint)
	{
		return currentThreshold_;
	}

	public Logger getLoggerForComponent(final String role, final String roleHint)
	{
		return curlogger_;
	}

	public void returnComponentLogger(final String role, final String roleHint)
	{
		// Empty
	}

	public int getActiveLoggerCount()
	{
		return 1;
	}

	private int parseThreshold(final String text)
	{
		final String txt = text.trim().toLowerCase();

		if ("debug".equals(txt))
		{
			return ConsoleLogger.LEVEL_DEBUG;
		}
		else if ("info".equals(txt))
		{
			return ConsoleLogger.LEVEL_INFO;
		}
		else if ("warn".equals(txt))
		{
			return ConsoleLogger.LEVEL_WARN;
		}
		else if ("error".equals(txt))
		{
			return ConsoleLogger.LEVEL_ERROR;
		}
		else if ("fatal".equals(txt))
		{
			return ConsoleLogger.LEVEL_FATAL;
		}

		return -1;
	}

	/**
	 * Remove this method and all references when this code is verified.
	 * 
	 * @param msg The message.
	 */
	private void debug(final String msg)
	{
		// Empty
	}
}
