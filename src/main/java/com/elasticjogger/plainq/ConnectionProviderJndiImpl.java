package com.elasticjogger.plainq;

import java.io.FileInputStream;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;

public class ConnectionProviderJndiImpl implements ConnectionProvider
{
  private static final String CONNECTION_FACTORY = "ConnectionFactory";
  private InitialContext jndiContext;
  private ConnectionFactory connectionFactory;

  public ConnectionProviderJndiImpl(String propertiesPath) throws Exception
  {
    createJNDIContext(propertiesPath);
    connectionFactory = (ConnectionFactory) jndiContext.lookup(CONNECTION_FACTORY);
  }

  private void createJNDIContext(String propertiesPath) throws Exception
  {
    FileInputStream fis = null;
    try
    {
      Properties properties = new Properties();
      fis = new FileInputStream(propertiesPath);
      properties.load(fis);

      jndiContext = new InitialContext(properties);
    }
    finally
    {
      if (fis != null)
      {
        fis.close();
      }
    }
  }

  @Override
  public Connection createConnection() throws JMSException
  {
    return connectionFactory.createConnection();
  }

  @Override
  public Connection createConnection(String userName, String password) throws JMSException
  {
    return connectionFactory.createConnection(userName, password);
  }
}
