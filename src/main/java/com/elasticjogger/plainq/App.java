package com.elasticjogger.plainq;

import java.util.logging.Logger;
import javax.jms.Connection;

public class App
{
  ConnectionProvider connectionProvider;
  private static final Logger log = Logger.getLogger(App.class.getName());

  public App(String brokerPropertiesPath) throws Exception
  {
    connectionProvider = new ConnectionProviderJndiImpl(brokerPropertiesPath);
  }

  public void start() throws Exception
  {
    Connection connection = connectionProvider.createConnection();

    JMSWorker jmsWorker = new JMSWorker(connection);

    log.info(jmsWorker.getProviderInfo().toString());
    log.info(jmsWorker.getClientID());

    jmsWorker.listenTopicNonDurable("myTopic");

    jmsWorker.sendTextMessageToQueue("myQueue", "i am the message");
    jmsWorker.sendTextMessageToTopic("myTopic", "i am the message from topic");

    jmsWorker.browseQueue("myQueue");

    jmsWorker.stop();
  }
}
