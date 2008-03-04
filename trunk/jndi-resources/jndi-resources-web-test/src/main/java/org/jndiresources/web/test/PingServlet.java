package org.jndiresources.web.test;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.jms.XAConnectionFactory;
import javax.mail.Address;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jboss.security.SubjectSecurityManager;

public class PingServlet extends HttpServlet
{
	private List<DataSource> jdbc_ = new ArrayList<DataSource>();

	private List<Session> mail_ = new ArrayList<Session>();

	private List<Address> email_ = new ArrayList<Address>();

	private List<URL> url_ = new ArrayList<URL>();

	private List<InetAddress> host_ = new ArrayList<InetAddress>();

	private List<Context> jndi_ = new ArrayList<Context>();

	private List<SubjectSecurityManager> jaas_ = new ArrayList<SubjectSecurityManager>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init()
	{
	}

	private void testJDBC(PrintWriter out) throws ClassNotFoundException,
			SQLException, NamingException
	{
		System.out.println("Test JDBC");
		out.println("<h1>Test JDBC</h1>");
		String s = getInitParameter("jdbc");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			try
			{
				final String jndiName = token.nextToken();
				out.println("JDBC lookup =" + jndiName + "<br/>");
	// System.out.println("lookup ="+jndiName);
				DataSource ds = (DataSource) new InitialContext()
						.lookup(jndiName);
	// System.out.println("ds="+ds);
				jdbc_.add(ds);
				out.println("&nbsp;&nbsp;JDBC try to connect...");
				ds.getConnection().close();
				out.println("ok !<br/>");
			}
			catch (Throwable x)
			{
				out.println("error " + x + "<br/>");
				x.printStackTrace();
			}
		}

		if (false)
		{
			out.println("Direct access to "
					+ Class.forName("com.mysql.jdbc.Driver"));
			Properties prop = new Properties();
			prop.put(
				"user", "pprados");
			prop.put(
				"password", "pprados");
			DriverManager
					.getConnection(
						"jdbc:mysql://localhost:3306/javatest?autoReconnect=true",
						prop).close();
			out.println(" connection directe ok<br/>");
		}

		if (false)
		{
			out.println("Access via JNDI<br/>");
			Connection conn;
			DataSource ds = (DataSource) jdbc_.get(2);
			// DataSource ds=(DataSource)new
			// InitialContext().lookup("java:comp/env/jdbc/MySQLMinimum");

			conn = ds.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM testdata");
			while (rs.next())
				out.println(rs.getString(1) + "<br/>");
			out.println("fin du next<br/>");

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
			out.println("C'est tous bon<br/>");
		}
	}

	private void testJAAS(PrintWriter out) throws NamingException
	{
		System.out.println("Test JAAS (JBoss)");
		out.println("<h1>Test JAAS (Jboss)</h1>");
		String s = getInitParameter("jaas");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			out.println("JAAS lookup =" + jndiName + "<br/>");
			final SubjectSecurityManager jaas = (SubjectSecurityManager) new InitialContext()
					.lookup(jndiName);
			jaas_.add(jaas);
			out.println("Active subject=" + jaas.getActiveSubject() + "<br/>");
			out
					.println("Security domain=" + jaas.getSecurityDomain()
							+ "<br/>");
		}
	}

	private void testMail(PrintWriter out) throws NamingException
	{
		System.out.println("Test MAIL");
		out.println("<h1>Test MAIL</h1>");
		String s = getInitParameter("javamail");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			try
			{
				final String jndiName = token.nextToken();
				out.println("JavaMail lookup =" + jndiName + "<br/>");
System.err.println("lookup="+new InitialContext().lookup(jndiName));				
				final Session session = (Session) new InitialContext()
						.lookup(jndiName);
				mail_.add(session);
				out.println("&nbsp;&nbsp;Transport est de type "
						+ session.getTransport().getURLName().getProtocol()
						+ " sur "
						+ session.getProperty("mail."
								+ session.getTransport().getURLName()
										.getProtocol() + ".host") + " avec "
						+ session.getProperty("mail.from") + "<br/>");
				out.println("&nbsp;&nbsp;Store est de type "
						+ session.getStore().getURLName().getProtocol()
						+ " sur "
						+ session.getProperty("mail."
								+ session.getStore().getURLName().getProtocol()
								+ ".host") + "<br/>");

				out.println("&nbsp;&nbsp;"
						+ session.getProperty("mail.smtp.user") + "<br/>");
			}
			catch (Throwable x)
			{
				out.println("error " + x + "<br/>");
				x.printStackTrace();
			}
		}

		s = getInitParameter("email");
		out.println("<h2>Email lookup</h2>");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			try
			{
				final Address address = (Address) new InitialContext()
						.lookup(jndiName);
				out.println("eMail lookup " + jndiName + "=" + address + "<br/>");
				email_.add(address);
			} catch (Throwable x)
			{
				out.println("error " + x + "<br/>");
				x.printStackTrace();
			}
		}
	}

	private void testURL(PrintWriter out) throws NamingException
	{
		System.out.println("Test URL");
		out.println("<h1>Test URL</h1>");
		String s = getInitParameter("url");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			out.println("URL lookup " + jndiName + "=");
			final URL url = (URL) new InitialContext().lookup(jndiName);
			url_.add(url);
			out.println(url + "<br/>");
		}
	}

	private void testHost(PrintWriter out) throws NamingException
	{
		System.out.println("Test HOST");
		out.println("<h1>Test HOST</h1>");
		String s = getInitParameter("host");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			out.println("Host lookup " + jndiName + "=");
			final InetAddress host = (InetAddress) new InitialContext()
					.lookup(jndiName);
			host_.add(host);
			out.println(host + "<br/>");
		}
	}

	private void testJNDI(PrintWriter out) throws NamingException
	{
		System.out.println("Test JNDI");
		out.println("<h1>Test JNDI</h1>");
		String s = getInitParameter("jndi");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			try
			{
				final String jndiName = token.nextToken();
				out.println("<p>JNDI lookup =" + jndiName + "<br/>");
				final Context context = (Context) new InitialContext()
						.lookup(jndiName);
				jndi_.add(context);
				boolean find = false;
				int n = 0;
				for (final NamingEnumeration<NameClassPair> e = context
						.list(""); e.hasMore();)
				{
					++n;
					find = true;
					out.println("&nbsp;&nbsp;" + e.next() + "<br/>");
					if (n > 10)
					{
						out.println("&nbsp;&nbsp,...<br/>");
						break;
					}
				}
				if (!find)
					out.println("&nbsp;&nbsp;<i>Empty</i>");
				out.println("</p>");

			}
			catch (Exception e)
			{
				out.println("error " + e + "<br/>");
			}
		}
		try
		{
			System.out.println("Test DNS");
			out.print("<h1>Test DNS</h1>");
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(
				"java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
			env.put(
				"java.naming.provider.url", "dns://ns1.sun.com/sun.com");

			DirContext ictx = new InitialDirContext(env);
			out.println(ictx + "<br/>");
			out.println("www=" + ictx.getAttributes(
				"www", new String[]
				{ "A" }) + "<br/>");
			out.println("ftp=" + ictx.getAttributes(
				"ftp", new String[]
				{ "A" }) + "<br/>");
			// out.println(ictx.list("www"));
		}
		catch (Exception e)
		{
			out.println("error " + e + "<br/>");
		}
	}

	private void testJMS(PrintWriter out)
	{
		System.out.println("Test JMS");
		out.println("<h1>Test JMS</h1>");

		out.println("<h2>Connection Factory</h2>");
		String s = getInitParameter("jms.cf");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			try
			{

				final String jndiName = token.nextToken();
				ConnectionFactory jms = (ConnectionFactory) new InitialContext()
						.lookup(jndiName);
				// jms.createConnection().close();
				out
						.println("<p>" + jndiName + " is " + jms.getClass()
								+ "</p>");
			}
			catch (Exception e)
			{
				out.println("error " + e + "<br/>");
			}
		}

		out.println("<h2>XA Connection Factory</h2>");
		s = getInitParameter("jms.xacf");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			try
			{

				final String jndiName = token.nextToken();
				XAConnectionFactory jms = (XAConnectionFactory) new InitialContext()
						.lookup(jndiName);
				// jms.createConnection().close();
				out
						.println("<p>" + jndiName + " is " + jms.getClass()
								+ "</p>");
			}
			catch (Exception e)
			{
				out.println("error " + e + "<br/>");
			}
		}

		out.println("<h2>Queue</h2>");
		s = getInitParameter("jms.queue");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			try
			{

				final String jndiName = token.nextToken();
				Queue jms = (Queue) new InitialContext().lookup(jndiName);
				out
						.println("<p>" + jndiName + " is " + jms.getClass()
								+ "</p>");
			}
			catch (Exception e)
			{
				out.println("error " + e + "<br/>");
			}
		}

		out.println("<h2>Topic</h2>");
		s = getInitParameter("jms.topic");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token
				.hasMoreTokens();)
		{
			try
			{

				final String jndiName = token.nextToken();
				Topic jms = (Topic) new InitialContext().lookup(jndiName);
				out
						.println("<p>" + jndiName + " is " + jms.getClass()
								+ "</p>");
			}
			catch (Exception e)
			{
				out.println("error " + e + "<br/>");
			}
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		try
		{
			testMail(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b>");
		}
		try
		{
			testURL(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b>");
		}
		try
		{
			testHost(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b>");
		}
		try
		{
			testJDBC(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b>");
		}
		try
		{
			testJMS(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b>");
		}
		try
		{
			testJNDI(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b>");
		}
		try
		{
			testJAAS(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b>");
		}
		out.println("</body></html>");
	}
}
