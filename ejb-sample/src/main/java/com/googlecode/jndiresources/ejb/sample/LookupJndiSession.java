package com.googlecode.jndiresources.ejb.sample;

import java.io.Serializable;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class LookupJndiSession implements SessionBean 
{
	private static final long serialVersionUID = 1L;

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

	public void ejbCreate() {}
	public void ejbActivate() {}
	public void ejbPassivate() {}
	public void ejbRemove() {}

	public void setSessionContext(SessionContext ctx) {  }
	public void unsetSessionContext() { }

	public Serializable lookup(String key) throws EJBException, NamingException
	{
		return (Serializable)ctx_.lookup(key);
	}
}