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
import javax.jms.Topic;

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

  public void sendTextMessageToQueue(String queueName, String message) throws JMSException
  {
    Queue queue = session.createQueue(queueName);
    sendTextMessage(message, queue);
  }

  public void sendTextMessageToTopic(String topicName, String message) throws JMSException
  {
    Topic topic = session.createTopic(topicName);
    sendTextMessage(message, topic);
  }

  private void sendTextMessage(String message, Destination destination) throws JMSException
  {
    TextMessage textMessage = session.createTextMessage(message);
    MessageProducer producer = session.createProducer(destination);
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
