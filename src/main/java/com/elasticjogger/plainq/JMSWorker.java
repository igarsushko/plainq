package com.elasticjogger.plainq;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class JMSWorker
{
  private Connection connection;
  private ConnectionMetaData connMetadata;

  public JMSWorker(Connection connection) throws JMSException
  {
    this.connection = connection;
    this.connMetadata = connection.getMetaData();

    connection.setExceptionListener(new ExceptionListener()
    {
      @Override
      public void onException(JMSException exception)
      {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    });
  }

  public String getJMSVersion() throws JMSException
  {
    return connMetadata.getJMSVersion();
  }

  public String getJMSProviderName() throws JMSException
  {
    return connMetadata.getJMSProviderName();
  }

  public List<String> getJMSXPropertyNames() throws JMSException
  {
    List<String> result = new ArrayList<>();

    Enumeration e = connMetadata.getJMSXPropertyNames();
    if (e != null)
    {
      while (e.hasMoreElements())
      {
        result.add(e.nextElement().toString());
      }
    }

    return result;
  }

  public String getProviderVersion() throws JMSException
  {
    return connMetadata.getProviderVersion();
  }

  public String getClientID() throws JMSException
  {
    return connection.getClientID();
  }
}
