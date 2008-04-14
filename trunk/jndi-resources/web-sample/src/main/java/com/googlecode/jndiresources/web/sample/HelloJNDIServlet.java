package com.googlecode.jndiresources.web.sample;

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
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.mail.NoSuchProviderException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.googlecode.jndiresources.ejb.sample.LookupJndi;
import com.googlecode.jndiresources.ejb.sample.LookupJndiHome;

/**
 * Simple WAR Sample with some resources.
 * 
 * @author Philippe PRADOS
 */
public class HelloJNDIServlet extends HttpServlet
{
	/**
	 * The initial JNDI context.
	 */
	private Context ctx_;
	{
		try
		{
			ctx_ = new InitialContext();
		}
		catch (NamingException x)
		{
			System.err.println("error");
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Test the JDBC resources.
	 * 
	 * @param out The writer.
	 */
	private void testJDBC(PrintWriter out)
	{
		final String jndiKey = "java:comp/env/jdbc/Default";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			out.println("<h2>Datasource</h2>");
			DataSource ds = (DataSource) ctx_.lookup(jndiKey);
			conn = ds.getConnection();
			stmt = conn.createStatement();
			DatabaseMetaData metadata = conn.getMetaData();
			out.println("<p>" + jndiKey + " is " + metadata.getDatabaseProductName() + " "
					+ metadata.getDatabaseProductVersion() + "</p>");

		}
		catch (NamingException e)
		{
			out.println("<p><b>Error when using default JDBC: NamingException " + e.getLocalizedMessage()
					+ "</b></p>");
		}
		catch (SQLException e)
		{
			out.println("<p><b>Error when using default JDBC: SQLException " + e.getLocalizedMessage()
					+ "</b></p>");
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close();
			}
			catch (SQLException e)
			{
			}
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException e)
			{
			}
			try
			{
				if (conn != null)
					conn.close();
			}
			catch (SQLException e)
			{
			}
		}
	}

	/**
	 * Test the Mail resources.
	 *  
	 * @param out The writer.
	 */
	private void testMail(PrintWriter out)
	{
		final String jndiKey = "java:comp/env/mail/Default";
		final String jndiEMailKey = "java:comp/env/mail/email";
		// final String jndiKey="java:Mail";
		javax.mail.Session session = null;
		try
		{
			out.println("<h2" + "" + ">Mail " + jndiKey + "</h2>");

			session = (javax.mail.Session) ctx_.lookup(jndiKey);
			out.println("<p>"
					+ jndiKey
					+ " is of type "
					+ session.getTransport().getURLName().getProtocol()
					+ " in "
					+ session.getProperty("mail." + session.getTransport().getURLName().getProtocol()
							+ ".host") + " with " + session.getProperty("mail.from") + "</p>");
			out.println("<p>" + jndiKey + " store is of type "
					+ session.getStore().getURLName().getProtocol() + " in "
					+ session.getProperty("mail." + session.getStore().getURLName().getProtocol() + ".host")
					+ "</p>");
			out.println("<p>" + jndiEMailKey + " is " + ctx_.lookup(jndiEMailKey) + "</p>");

			// out.println("<p>"+session.getProperty("mail.smtp.user")+"</p>");
			// session.getProperties().list(out);

			// out.println("<p>");
			// ((Session)ctx.lookup("java:comp/env/mail/smtp")).getProperties().list(out);
			// out.println("</p>");
		}
		catch (NamingException e)
		{
			out.println("<p><b>Error when using default Mail: NamingException " + e.getLocalizedMessage()
					+ "</b></p>");
			e.printStackTrace(out);
		}
		catch (NoSuchProviderException e)
		{
			out.println("<p><b>Error when using default Mail: NoSuchProviderException "
					+ e.getLocalizedMessage() + "</b></p>");
			e.printStackTrace(out);
		}
	}

	/**
	 * Test the URL resources.
	 *  
	 * @param out The writer.
	 */
	private void testUrl(PrintWriter out)
	{
		final String jndiKey = "java:comp/env/url/Default";
		URL url = null;
		try
		{
			out.println("<h2>URL " + jndiKey + "</h2>");
			url = (URL) ctx_.lookup(jndiKey);
			out.println("<p>" + jndiKey + " is " + url);
		}
		catch (NamingException e)
		{
			out.println("<p><b>Error when using default URL: NamingException " + e.getLocalizedMessage()
					+ "</b></p>");
		}
	}

	/**
	 * Test the Host resources.
	 *  
	 * @param out The writer.
	 */
	private void testHost(PrintWriter out)
	{
		final String jndiKey = "java:comp/env/host/Default";
		InetAddress host = null;
		try
		{
			out.println("<h2>Host " + jndiKey + "</h2>");
			host = (InetAddress) ctx_.lookup(jndiKey);
			out.println("<p>" + jndiKey + " is " + host);
		}
		catch (NamingException e)
		{
			out.println("<p><b>Error when using default Host: NamingException " + e.getLocalizedMessage()
					+ "</b></p>");
			e.printStackTrace(out);
		}
	}

	/**
	 * Test the EJB resources.
	 * 
	 * @param out The writer.
	 * @throws NamingException 
	 * @throws RemoteException 
	 * @throws CreateException 
	 */
	private void testEJB(PrintWriter out) throws NamingException, RemoteException
	{
		// Use a wrapper for accept to load this class without ejb.jar
		try
		{
			new Wrapper().testEJB(out);
		}
		catch (NoClassDefFoundError x)
		{
			// Ignore
			out.println("EJB classes not found ! ("+x.getMessage()+")");
		}
	}
	static class Wrapper
	{
		private void testEJB(PrintWriter out) throws NamingException, RemoteException
		{
			final String jndiKey = "java:comp/env/ejb/LookupJndi";
			final String[] ejbJndiKey = 
				new String[]{"java:comp/env/host/Default",
							 "java:comp/env/url/Default",
							 jndiKey
							};
			
			out.println("<h1>Test EJB</h1>");
			try
			{
				LookupJndiHome home = (LookupJndiHome)PortableRemoteObject.narrow(
		            new InitialContext().lookup(jndiKey), 
		            LookupJndiHome.class);
				LookupJndi ejb=(LookupJndi)PortableRemoteObject.narrow(home.create(),LookupJndi.class);
				for (int i=0;i<ejbJndiKey.length;++i)
				{
					try
					{
						Object obj=ejb.lookup(ejbJndiKey[i]);
						out.println("Lookup "+ejbJndiKey[i]+" in EJB return "+obj+"<br/>");
					}
					catch (NamingException x)
					{
						out.println("EJB "+ejbJndiKey[i]+" not found in EJB ! ("+x.getLocalizedMessage()+")<br/>");
					}
				}
			}
			catch (NamingException x)
			{
				out.println("EJB "+jndiKey+" not found ! ("+x.getLocalizedMessage()+")");
			}
			catch (EJBException x)
			{
				out.println("EJB "+jndiKey+" exception ("+x.getLocalizedMessage()+")");
			}
			catch (CreateException x)
			{
				out.println("EJB "+jndiKey+" exception ("+x.getLocalizedMessage()+")");
			}
		}
	};
	
	/**
	 * Lookup JNDI.
	 * 
	 * @param request
	 * @param out
	 */
	private void jndiLookup(HttpServletRequest request, PrintWriter out)
	{
		out.println("<h1>Manual lookup</h1>");
		String jndi=request.getParameter("jndi");
		if (jndi!=null)
		{
			out.println("<p>lookup "+jndi+"=");
			try
			{
//				Object x=new InitialContext().lookupLink(jndi);
				Object x=new InitialContext().lookup(jndi);
				out.println(x+" ("+x.getClass()+")");
			}
			catch (Throwable x)
			{
				out.println("<pre>");
				x.printStackTrace(out);
				out.println("</pre>");
			}
			out.println("</p>");
		}
		else
			jndi="";
		out.println("<form >");
		out.println("JNDI name:<input name=\"jndi\" value=\""+jndi+"\"/>  <INPUT type=\"submit\" name=\"lookup\" value=\"lookup\">");
		out.println("</body></html>");
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		out.println("<h1>JNDI Resource</h1>");

		try
		{
			testJDBC(out);
		}
		catch (Throwable x)
		{
			out.print("<pre>");
			x.printStackTrace(out);
			out.print("</pre>");
		}
		
		try
		{
			testMail(out);
		}
		catch (Throwable x)
		{
			out.print("<pre>");
			x.printStackTrace(out);
			out.print("</pre>");
		}
		
		try
		{
			testUrl(out);
		}
		catch (Throwable x)
		{
			out.print("<pre>");
			x.printStackTrace(out);
			out.print("</pre>");
		}
		
		try
		{
			testHost(out);
		}
		catch (Throwable x)
		{
			out.print("<pre>");
			x.printStackTrace(out);
			out.print("</pre>");
		}

		try
		{
			testEJB(out);
		}
		catch (Throwable x)
		{
			out.print("<pre>");
			x.printStackTrace(out);
			out.print("</pre>");
		}

		jndiLookup(request, out);
		out.println("</body></html>");
		out.close();
	}
}
