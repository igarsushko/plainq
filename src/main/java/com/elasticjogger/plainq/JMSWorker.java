package com.elasticjogger.plainq;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public class JMSWorker
{
  private Connection connection;
  private Session session;
  private ProviderInfo providerInfo;
  private static final Logger log = Logger.getLogger(JMSWorker.class.getName());

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

  public List<Message> browseQueue(String queueName) throws JMSException
  {
    Queue queue = session.createQueue(queueName);
    QueueBrowser browser = session.createBrowser(queue);

    List<Message> result = new ArrayList<>();

    Enumeration<Message> messages = browser.getEnumeration();
    while (messages.hasMoreElements())
    {
      Message message = messages.nextElement();
      result.add(message);
      log.fine(message == null ? "null" : message.toString());
    }

    browser.close();

    return result;
  }

  public void listenTopicNonDurable(String topicName, MessageListener listener) throws JMSException
  {
    TopicConnection topicConnection = (TopicConnection) connection;//TODO find another way, maybe amq connection class implements both queue and topic connection interfaces
    TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

    Topic topic = topicSession.createTopic(topicName);
    TopicSubscriber subscriber = topicSession.createSubscriber(topic);
    subscriber.setMessageListener(listener);
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

  //TODO create stop timeout
  public void stop() throws JMSException
  {
    connection.close();
  }
}
