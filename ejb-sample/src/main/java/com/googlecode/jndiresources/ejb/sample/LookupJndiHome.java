package com.googlecode.jndiresources.ejb.sample;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface LookupJndiHome  extends EJBHome //, EJBLocalHome 
{
  public LookupJndi create() throws CreateException,RemoteException;
}
