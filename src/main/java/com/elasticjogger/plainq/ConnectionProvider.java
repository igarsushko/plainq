package com.elasticjogger.plainq;

import javax.jms.Connection;
import javax.jms.JMSException;

public interface ConnectionProvider
{
  Connection createConnection() throws JMSException;

  Connection createConnection(String userName, String password) throws JMSException;
}
