package com.elasticjogger.plainq;

import java.io.FileInputStream;
import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.naming.InitialContext;

public class ConnectionFactoryProviderJndiImpl implements ConnectionFactoryProvider
{

  private static final String CONNECTION_FACTORY = "ConnectionFactory";
  private String propertiesPath;

  public ConnectionFactoryProviderJndiImpl(String propertiesPath)
  {
    this.propertiesPath = propertiesPath;
  }

  @Override
  public ConnectionFactory getConnectionFactory() throws Exception
  {
    ConnectionFactory connectionFactory = null;
    FileInputStream fis = null;
    try
    {
      Properties properties = new Properties();
      fis = new FileInputStream(propertiesPath);
      properties.load(fis);

      InitialContext ic = new InitialContext(properties);
      connectionFactory = (ConnectionFactory) ic.lookup(CONNECTION_FACTORY);

    } finally
    {
      if (fis != null)
      {
        fis.close();
      }
    }

    return connectionFactory;
  }
}
