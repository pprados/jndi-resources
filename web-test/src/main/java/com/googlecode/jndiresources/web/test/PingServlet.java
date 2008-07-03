package com.googlecode.jndiresources.web.test;

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
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
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

/**
 * Sample with all kind of resources. To use this component, you must before
 * start
 * <ul>
 * <li>mysql</li>
 * <li>oracle xe</li>
 * <li>tnameserv</li>
 * <li>rmiregistry</li>
 * </ul>
 * 
 * @author Philippe PRADOS
 */
public class PingServlet extends HttpServlet
{
	/**
	 * The list of datasource to check.
	 */
	private List<DataSource> jdbc_ = new ArrayList<DataSource>();

	/**
	 * The list of mail session to check.
	 */
	private List<Session> mail_ = new ArrayList<Session>();

	/**
	 * The list of email to check.
	 */
	private List<Address> email_ = new ArrayList<Address>();

	/**
	 * The list of url to check.
	 */
	private List<URL> url_ = new ArrayList<URL>();

	/**
	 * The list of host to check.
	 */
	private List<InetAddress> host_ = new ArrayList<InetAddress>();

	/**
	 * The list of CF to check.
	 */
	private List<ConnectionFactory> cf_ = new ArrayList<ConnectionFactory>();

	/**
	 * The list of CF to check.
	 */
	private List<XAConnectionFactory> xacf_ = new ArrayList<XAConnectionFactory>();

	/**
	 * The list of jndi context to check.
	 */
	private List<Context> jndi_ = new ArrayList<Context>();

	/**
	 * The list of jndi context to check.
	 */
	private List<Object> link_ = new ArrayList<Object>();

	/**
	 * The list of subject security manager to check.
	 */
	private List<SubjectSecurityManager> jaas_ = new ArrayList<SubjectSecurityManager>();

	/**
	 * The maximum JNDI lookup.
	 */
	private static final int MAX_JNDI_LOOKUP=10;
	
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Test the JDBC resources.
	 * 
	 * @param out The writer.
	 * @throws ClassNotFoundException If Error.
	 * @throws SQLException If error.
	 * @throws NamingException If error.
	 */
	private void testJDBC(PrintWriter out) throws NamingException, ClassNotFoundException, SQLException
	{
		System.out.println("Test JDBC");
		out.println("<h1>Test JDBC</h1>");
		String s = getInitParameter("jdbc");
		if (s==null || s.trim().length()==0) return;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			try
			{
				final String jndiName = token.nextToken();
				out.println("JDBC lookup =" + jndiName + "<br/>");
				// System.out.println("lookup ="+jndiName);
				DataSource ds = (DataSource) new InitialContext().lookup(jndiName);
				// System.out.println("ds="+ds);
				jdbc_.add(ds);
				out.println("&nbsp;&nbsp;JDBC try to connect...");
				ds.getConnection().close();
				out.println("ok !<br/>");
			}
			catch (Throwable x)
			{
				out.println("<b>error " + x + "</b><br/>");
				x.printStackTrace();
			}
		}

		if (false)
		{
			out.println("Direct access to " + Class.forName("com.mysql.jdbc.Driver"));
			Properties prop = new Properties();
			prop.put(
				"user", "pprados");
			prop.put(
				"password", "pprados");
			DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/javatest?autoReconnect=true", prop).close();
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

	/**
	 * Test the JAAS resources.
	 * 
	 * @param out The writer.
	 * @throws NamingException If error.
	 */
	private void testJAAS(PrintWriter out) throws NamingException
	{
		System.out.println("Test JAAS (JBoss)");
		out.println("<h1>Test JAAS (Jboss)</h1>");
		String s = getInitParameter("jaas");
		if (s==null || s.trim().length()==0) return;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			out.println("JAAS lookup =" + jndiName + "<br/>");
			final SubjectSecurityManager jaas = (SubjectSecurityManager) new InitialContext()
					.lookup(jndiName);
			jaas_.add(jaas);
			out.println("Active subject=" + jaas.getActiveSubject() + "<br/>");
			out.println("Security domain=" + jaas.getSecurityDomain() + "<br/>");
		}
	}

	/**
	 * Test the Mail resources.
	 * 
	 * @param out The writer.
	 * @throws NamingException If error.
	 */
	private void testMail(PrintWriter out) throws NamingException
	{
		System.out.println("Test MAIL");
		out.println("<h1>Test MAIL</h1>");
		String s = getInitParameter("javamail");
		if (s==null || s.trim().length()==0) return;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			try
			{
				final String jndiName = token.nextToken();
				out.println("JavaMail lookup =" + jndiName + "<br/>");
				System.err.println("lookup=" + new InitialContext().lookup(jndiName));
				final Session session = (Session) new InitialContext().lookup(jndiName);
				mail_.add(session);
				out.println("&nbsp;&nbsp;Transport est de type "
						+ session.getTransport().getURLName().getProtocol()
						+ " sur "
						+ session.getProperty("mail." + session.getTransport().getURLName().getProtocol()
								+ ".host") + " avec " + session.getProperty("mail.from") + "<br/>");
				out.println("&nbsp;&nbsp;Store est de type "
						+ session.getStore().getURLName().getProtocol()
						+ " sur "
						+ session.getProperty("mail." + session.getStore().getURLName().getProtocol()
								+ ".host") + "<br/>");

				out.println("&nbsp;&nbsp;" + session.getProperty("mail.smtp.user") + "<br/>");
			}
			catch (Throwable x)
			{
				out.println("<b>error " + x + "</b><br/>");
				x.printStackTrace();
			}
		}

		s = getInitParameter("email");
		if (s==null || s.trim().length()==0) return;
		out.println("<h2>Email lookup</h2>");
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			try
			{
				final Address address = (Address) new InitialContext().lookup(jndiName);
				out.println("eMail lookup " + jndiName + "=" + address + "<br/>");
				email_.add(address);
			}
			catch (Throwable x)
			{
				out.println("<b>error " + x + "</b><br/>");
				x.printStackTrace();
			}
		}
	}

	/**
	 * Test the URL resources.
	 * 
	 * @param out The writer.
	 * @throws NamingException If error.
	 */
	private void testURL(PrintWriter out) throws NamingException
	{
		System.out.println("Test URL");
		out.println("<h1>Test URL</h1>");
		String s = getInitParameter("url");
		if (s==null || s.trim().length()==0) return;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			out.println("URL lookup " + jndiName + "=");
			final URL url = (URL) new InitialContext().lookup(jndiName);
			url_.add(url);
			out.println(url + "<br/>");
		}
	}

	/**
	 * Test the Host resources.
	 * 
	 * @param out The writer.
	 * @throws NamingException If error.
	 */
	private void testHost(PrintWriter out) throws NamingException
	{
		System.out.println("Test HOST");
		out.println("<h1>Test HOST</h1>");
		String s = getInitParameter("host");
		if (s==null || s.trim().length()==0) return;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			out.println("Host lookup " + jndiName + "=");
			final InetAddress host = (InetAddress) new InitialContext().lookup(jndiName);
			host_.add(host);
			out.println(host + "<br/>");
		}
	}

	/**
	 * Test the JNDI Links.
	 * 
	 * @param out The writer.
	 * @throws NamingException If error.
	 */
	private void testLink(PrintWriter out) throws NamingException
	{
		System.out.println("Test Link");
		out.println("<h1>Test Links</h1>");
		String s = getInitParameter("link");
		if (s==null || s.trim().length()==0) return;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			final String jndiName = token.nextToken();
			out.println("Link lookup " + jndiName + "=");
			//			final Object link = (Object) new InitialContext().lookupLink(jndiName);
			final Object link = new InitialContext().lookup(jndiName);
			//link_.add(link);
			out.println(link + "<br/>");
		}
	}

	/**
	 * Test the JNDI resources.
	 * 
	 * @param out The writer.
	 * @throws NamingException If error.
	 */
	private void testJNDI(PrintWriter out) throws NamingException
	{
		System.out.println("Test JNDI");
		out.println("<h1>Test JNDI</h1>");
		String s = getInitParameter("jndi");
		if (s==null || s.trim().length()==0) return;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();)
		{
			try
			{
				final String jndiName = token.nextToken();
				out.println("<p>JNDI lookup =" + jndiName + "<br/>");
				final Context context = (Context) new InitialContext().lookup(jndiName);
				jndi_.add(context);
				boolean find = false;
				int n = 0;
				for (final NamingEnumeration<NameClassPair> e = context.list(""); e.hasMore();)
				{
					++n;
					find = true;
					out.println("&nbsp;&nbsp;" + e.next() + "<br/>");
					if (n > MAX_JNDI_LOOKUP)
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
				out.println("<b>error " + e + "</b><br/>");
			}
		}
		try
		{
			System.out.println("Test DNS");
			out.print("<h1>Test DNS</h1>");
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(
				"java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
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
			out.println("<b>error " + e + "</b><br/>");
		}
	}

	/**
	 * Test the JMS resources.
	 * 
	 * @param out The writer.
	 */
	private void testJMS(PrintWriter out)
	{
		System.out.println("Test JMS");
		out.println("<h1>Test JMS</h1>");
		int idx;

		out.println("<h2>Connection Factory</h2>");
		String s = getInitParameter("jms.cf");
		if (s==null || s.trim().length()==0) return;
		idx=0;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();++idx)
		{
			try
			{

				final String jndiName = token.nextToken();
				out.print(jndiName + " is ");
				boolean val=false;
//				if (val)
//				{
//					ActiveMQManagedConnectionFactory a=new ActiveMQManagedConnectionFactory();
//					org.jboss.logging.util.LoggerPluginWriter writer=new org.jboss.logging.util.LoggerPluginWriter(new org.jboss.logging.LoggerPlugin()
//					{
//	
//						public void debug(Object arg0)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void debug(Object arg0, Throwable arg1)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void error(Object arg0)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void error(Object arg0, Throwable arg1)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void fatal(Object arg0)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void fatal(Object arg0, Throwable arg1)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void info(Object arg0)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void info(Object arg0, Throwable arg1)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void init(String arg0)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public boolean isDebugEnabled()
//						{
//							// TODO Auto-generated method stub
//							return false;
//						}
//	
//						public boolean isInfoEnabled()
//						{
//							// TODO Auto-generated method stub
//							return false;
//						}
//	
//						public boolean isTraceEnabled()
//						{
//							// TODO Auto-generated method stub
//							return false;
//						}
//	
//						public void trace(Object arg0)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void trace(Object arg0, Throwable arg1)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void warn(Object arg0)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//	
//						public void warn(Object arg0, Throwable arg1)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//					
//					});
//					a.setLogWriter(writer);
//					new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new ActiveMQManagedConnectionFactory());
//	//				new InitialContext().rebind("jndi-web-test/jms/ActiveMQConnectionFactory",new ActiveMQManagedConnectionFactory());
//	//				new InitialContext().lookup("jndi-web-test/jms/ActiveMQConnectionFactory")
//				}
				ConnectionFactory cf = (ConnectionFactory) new InitialContext().lookup(jndiName);
				cf_.add(idx,cf);
				out.println(cf.getClass() + "<br/>");
				cf.createConnection().close();
			}
			catch (Exception e)
			{
				out.println("<b>error " + e + "</b><br/>");
				e.printStackTrace();
				cf_.add(idx,null);
			}
		}

		out.println("<h2>XA Connection Factory</h2>");
		s = getInitParameter("jms.xacf");
		if (s==null || s.trim().length()==0) return;
		idx=0;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();++idx)
		{
			try
			{

				final String jndiName = token.nextToken();
				out.print(jndiName + " is ");
				XAConnectionFactory xacf = (XAConnectionFactory) new InitialContext().lookup(jndiName);
				xacf_.add(idx,xacf);
				out.println(xacf.getClass() + "<br/>");
				xacf.createXAConnection().close();
			}
			catch (Exception e)
			{
				out.println("<b>error " + e + "</b><br/>");
				e.printStackTrace();
			}
		}

		out.println("<h2>Queue</h2>");
		s = getInitParameter("jms.queue");
		if (s==null || s.trim().length()==0) return;
		idx=0;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();++idx)
		{
			try
			{

				final String jndiName = token.nextToken();
				out.println(jndiName + " is ");
				Queue queue = (Queue) new InitialContext().lookup(jndiName);
				out.println(queue.getClass() + " ");

				QueueConnectionFactory factory=(QueueConnectionFactory)cf_.get(idx);
				if (factory!=null)
				{
					QueueConnection conn = factory.createQueueConnection();
					conn.start();
					QueueSession session = conn.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
					QueueSender sender=session.createSender(queue);
					MessageConsumer consumer=session.createConsumer(queue);
					TextMessage msg= session.createTextMessage("Hello");
					sender.send(msg);
	//				msg=(TextMessage)consumer.receive();
	//				out.println("Receive " + msg.getText() + "<br/>");
					sender.close();
					session.close();
					conn.stop();
					conn.close();
					out.println("and is functional");
				}
				out.println("<br/>");
			}
			catch (Exception e)
			{
				out.println("<b>error " + e + "</b><br/>");
				e.printStackTrace();
			}
		}

		out.println("<h2>Topic</h2>");
		s = getInitParameter("jms.topic");
		if (s==null || s.trim().length()==0) return;
		idx=0;
		for (StringTokenizer token = new StringTokenizer(s, "; \n\t"); token.hasMoreTokens();++idx)
		{
			try
			{

				final String jndiName = token.nextToken();
				out.println(jndiName + " is ");
				Topic topic = (Topic) new InitialContext().lookup(jndiName);
				out.println(topic.getClass() + " ");
				
				TopicConnectionFactory factory=(TopicConnectionFactory)cf_.get(idx);
				if (factory!=null)
				{
					TopicConnection conn = factory.createTopicConnection();
					conn.start();
					TopicSession session = conn.createTopicSession(false,TopicSession.AUTO_ACKNOWLEDGE);
					TopicSubscriber subscriber = session.createSubscriber(topic);
					TopicPublisher publisher=session.createPublisher(topic);
					TextMessage msg=session.createTextMessage("Hello");
					publisher.send(msg);
	//				msg=(TextMessage)subscriber.receive();
	//				out.println("Receive " + msg.getText() + "<br/>");
					publisher.close();
					session.close();
					conn.stop();
					conn.close();
					out.println("and is functional");
				}
				out.println("<br/>");
			}
			catch (Exception e)
			{
				out.println("<b>error " + e + "</b><br/>");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lookup JNDI.
	 * 
	 * @param request
	 * @param out
	 */
	private void jndiLookup(HttpServletRequest request, PrintWriter out)
	{
		out.println("<h1>Lookup</h1>");
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
		out.println("</form>");
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		try
		{
			testMail(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		try
		{
			testURL(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		try
		{
			testHost(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		try
		{
			testJDBC(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		try
		{
			testJMS(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		try
		{
			testJNDI(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		try
		{
			testLink(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		try
		{
			testJAAS(out);
		}
		catch (Throwable e)
		{
			out.print("<b>" + e + "</b><br/>");
			e.printStackTrace();
		}
		jndiLookup(request, out);
		out.println("</body></html>");
		out.close();
	}

}
