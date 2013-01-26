package com.elasticjogger.plainq;

import javax.jms.ConnectionFactory;

public interface ConnectionFactoryProvider
{
  ConnectionFactory getConnectionFactory() throws Exception;
}
