package com.googlecode.jndiresources.var;

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
import java.io.Reader;
import java.util.Properties;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * A reader with a on-the-fly conversion of variables.
 * 
 * @author Philippe PRADOS
 */
public class VariableReader extends Reader
{
	/**
	 * The logger.
	 */
	private static final Logger log_ = Logger.getLogger(VariableReader.class);

	/**
	 * Default buffer size.
	 */
	private static final int DEFAULTSIZEBUF = 8192;

	/**
	 * Coef to extend buffer (30%).
	 */
	private static final int EXTENDBUFFER = 130;

	/**
	 * Percent.
	 */
	private static final int PERCENT = 100;

	/**
	 * The buffer.
	 */
	private char[] buf_;

	/**
	 * The current buffer size.
	 */
	private int bufsize_;

	/**
	 * The current position in the buffer.
	 */
	private int pos_ = 0;

	/**
	 * The size of available char in the buffer.
	 */
	private int size_ = 0;

	/**
	 * Position in the buffer to start a variable.
	 */
	private final Stack startvar_ = new Stack();

	/**
	 * The length of the variable.
	 */
	private int lenvar_;

	/**
	 * The properties to convert the variables.
	 */
	private final Properties prop_;

	/**
	 * The next reader.
	 */
	private final Reader next_;

	/**
	 * Error if the variable is not found.
	 */
	public static class VariableNotFound extends IOException
	{

		/**
		 * The serial version.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The variable name in error.
		 */
		private String varname_;

		/**
		 * Constructor.
		 * 
		 * @param msg The message of exception.
		 * @param varname The variable name.
		 */
		public VariableNotFound(final String msg, final String varname)
		{
			super(msg);
			varname_ = varname;
		}

		/**
		 * Return the variable name in error.
		 * 
		 * @return The variable name in error.
		 */
		public String getVariableName()
		{
			return varname_;
		}

	}

	/**
	 * Constructor.
	 * 
	 * @param next The next reader.
	 * @param bufsize The buffer size.
	 * @param prop The properties to change the variables to values.
	 */
	public VariableReader(final Reader next, final int bufsize, final Properties prop)
	{
		super();
		next_ = next;
		prop_ = prop;
		bufsize_ = bufsize;
		buf_ = new char[(bufsize * EXTENDBUFFER) / PERCENT];
	}

	/**
	 * Constructor.
	 * 
	 * @param next The next reader.
	 * @param prop The properties to change the variables to values.
	 */
	public VariableReader(final Reader next, final Properties prop)
	{
		this(next, DEFAULTSIZEBUF, prop);
	}

	/**
	 * Close the reader.
	 * 
	 * @exception IOException If error.
	 */
	public void close() throws IOException
	{
		next_.close();
	}

	/**
	 * Read a buffer. The variables are updated before set the buffer.
	 * 
	 * @param cbuf The buffer.
	 * @param off The offset in the buffer.
	 * @param len The len to read.
	 * 
	 * @return The number of char read.
	 * 
	 * @throws IOException If read error.
	 */
	public int read(final char[] cbuf, final int off, final int len) throws IOException
	{
		int offset = off;
		int length = len;
		if (size_ == -1)
			return -1; // EOF
		int readsize = 0;

		while ((size_ != -1) && (length > 0))
		{
			// Copie ce qui est encore présent dans le tampon
			final int maxcopy = (length > size_) ? size_ : length;
			System.arraycopy(
				buf_, pos_, cbuf, offset, maxcopy);
			length -= maxcopy;
			offset += maxcopy;
			readsize += maxcopy;
			pos_ += maxcopy;
			size_ -= maxcopy;

			// Recharge le tampon
			if (size_ == 0)
			{
				// Recupère le nom de la dernière variable en cours
				pos_ = 0;
				int start = pos_;
				if (startvar_.size() > 0)
				{
					final int startvar = ((Integer) startvar_.pop()).intValue();
					System.arraycopy(
						buf_, startvar, buf_, 0, lenvar_);
					start = lenvar_;
				}
				size_ = next_.read(
					buf_, start, bufsize_ - start) + start;
				if (size_ == -1)
					return readsize;

				// Cherche les variables
				boolean dollard = false;
				boolean variable = false;
				int i = 0;
				int startvar = 0;
				for (; i < size_; ++i)
				{
					if (dollard)
					{
						if (buf_[i] == '{') // Start variable
						{
							variable = true;
							startvar_.push(Integer.valueOf(i - 1));
						}
						else
							dollard = false;
					}
					if (variable)
					{
						if (buf_[i] == '}') // End variable
						{
							startvar = ((Integer) startvar_.pop()).intValue();
							variable = !startvar_.empty();
							lenvar_ = i - startvar + 1;
							final String varname = new String(buf_, startvar + 2, i - startvar - 2);
							// final String
							String value = prop_.getProperty(varname);
							if (value == null)
							{
								onError(
									"Variable " + varname + " not found!", varname);
								value = new String(buf_, startvar, lenvar_);
							}
							if (log_.isDebugEnabled())
								log_.debug(varname + "=" + value);
							if (!value.equals(new String(buf_, startvar, lenvar_)))
							{
								// Insert value
								final int l = value.length();

								final int solde = size_ - startvar - lenvar_;

								// Extend buffer if it's a necessity
								if (startvar + l + solde > buf_.length)
								{
									final char[] buf = new char[((buf_.length + solde) * EXTENDBUFFER)
											/ PERCENT];
									System.arraycopy(
										buf_, 0, buf, 0, buf_.length);
									buf_ = buf;
								}

								System.arraycopy(
									buf_, startvar + lenvar_, buf_, startvar + l, solde);
								System.arraycopy(
									value.toCharArray(), 0, buf_, startvar, l);
								size_ -= lenvar_ - l;
								i = startvar; // Recursive variable possible
							}
						}
					}
					if (buf_[i] == '$')
					{
						dollard = true;
					}
				}
				if (variable || dollard)
				{
					lenvar_ = i - ((Integer) startvar_.peek()).intValue();
					size_ -= lenvar_;
				}
			}
		}
		return readsize;
	}

	protected void onError(final String msg, final String varname) throws VariableNotFound
	{
		throw new VariableNotFound(msg, varname);
	}
	// public static void main(String[] args) throws IOException
	// {
	// Properties prop=new Properties();
	// prop.put("def", "def");
	// prop.put("abcdefghi","hello");
	// char[] buf=new char[1024];
	//		
	// class IgnoreErrorVariableReader extends VariableReader
	// {
	// IgnoreErrorVariableReader(Reader next,int size,Properties prop)
	// {
	// super(next,size,prop);
	// }
	// protected void onError(String msg,String var)
	// {
	// System.out.println("onError("+msg+","+var+")");
	// }
	// }
	// System.out.println(new String(buf,0,
	// new IgnoreErrorVariableReader(new InputStreamReader(new
	// StringBufferInputStream("${novar}")),12,prop).read(buf)));
	// System.out.println(new String(buf,0,
	// new VariableReader(new InputStreamReader(new
	// StringBufferInputStream("NOVAR")),12,prop).read(buf)));
	// System.out.println(new String(buf,0,
	// new VariableReader(new InputStreamReader(new
	// StringBufferInputStream("${abcdefghi}")),12,prop).read(buf)));
	// System.out.println(new String(buf,0,
	// new VariableReader(new InputStreamReader(
	// new StringBufferInputStream("${abc${def}ghi}")),15,prop).read(buf)));
	// System.out.println(new String(buf,0,
	// new VariableReader(new InputStreamReader(
	// new StringBufferInputStream("AA${abc${def}ghi}A")),15,prop).read(buf)));
	// }
}
