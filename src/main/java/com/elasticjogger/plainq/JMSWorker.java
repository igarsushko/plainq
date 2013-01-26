package com.elasticjogger.plainq;

import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

public class JMSWorker
{
  private Connection connection;
  private ProviderInfo providerInfo;

  public JMSWorker(Connection connection) throws JMSException
  {
    this.connection = connection;
    ConnectionMetaData metaData = connection.getMetaData();

    providerInfo = new ProviderInfo();
    providerInfo.setJMSProviderName(metaData.getJMSProviderName());
    providerInfo.setJMSVersion(metaData.getJMSVersion());
    providerInfo.setJMSXPropertyNames(metaData.getJMSXPropertyNames());
    providerInfo.setProviderVersion(metaData.getProviderVersion());

    connection.setExceptionListener(new ExceptionListener()
    {
      @Override
      public void onException(JMSException exception)
      {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    });
  }

  public ProviderInfo getProviderInfo()
  {
    return providerInfo;
  }

  public String getClientID() throws JMSException
  {
    return connection.getClientID();
  }
}
