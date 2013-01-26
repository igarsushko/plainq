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
  private ConnectionInfo connectionInfo;

  public JMSWorker(Connection connection) throws JMSException
  {
    this.connection = connection;
    ConnectionMetaData metaData = connection.getMetaData();

    connectionInfo = new ConnectionInfo();
    connectionInfo.setClientID(this.connection.getClientID());
    connectionInfo.setJMSProviderName(metaData.getJMSProviderName());
    connectionInfo.setJMSVersion(metaData.getJMSVersion());
    connectionInfo.setJMSXPropertyNames(metaData.getJMSXPropertyNames());
    connectionInfo.setProviderVersion(metaData.getProviderVersion());

    connection.setExceptionListener(new ExceptionListener()
    {
      @Override
      public void onException(JMSException exception)
      {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    });
  }

  public ConnectionInfo getConnectionInfo()
  {
    return connectionInfo;
  }
}
