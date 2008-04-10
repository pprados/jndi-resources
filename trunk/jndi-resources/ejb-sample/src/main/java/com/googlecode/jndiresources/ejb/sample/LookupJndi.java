package com.googlecode.jndiresources.ejb.sample;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.ejb.EJBObject;
import javax.naming.NamingException;

public interface LookupJndi extends EJBObject
{
	public Serializable lookup(String lookup) throws RemoteException, NamingException;
}
