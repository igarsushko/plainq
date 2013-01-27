package com.elasticjogger.plainq;

import java.util.UUID;
import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

public class JMSWorker
{
  private Connection connection;
  private Session session;
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

    start();
  }

  /**
   * Sends message to destination, either to queue or topic.
   *
   * @param message
   * @throws JMSException
   */
  public void sendTextMessageToQueue(String queueName, String message) throws JMSException
  {
    TextMessage textMessage = session.createTextMessage(message);
    Queue queue = session.createQueue(queueName);
    MessageProducer producer = session.createProducer(queue);
    producer.send(textMessage);
  }

  public ProviderInfo getProviderInfo()
  {
    return providerInfo;
  }

  public String getClientID() throws JMSException
  {
    return connection.getClientID();
  }

  private void start() throws JMSException
  {
    connection.start();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  public void stop() throws JMSException
  {
    session.close();
    connection.close();
  }
}
