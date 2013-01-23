package com.elasticjogger.plainq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;

public class Runner
{

  ClassLoader classLoader;

  public Runner()
  {
    classLoader = this.getClass().getClassLoader();
  }

  public void start() throws Exception
  {
    Runner runner = new Runner();
    runner.doJob();
  }

  public void doJob() throws Exception
  {
    Class amqClass = classLoader.loadClass("org.apache.activemq.ActiveMQConnectionFactory");

    ConnectionFactory factory = (ConnectionFactory) amqClass.newInstance();
    Connection conn = factory.createConnection();
    ConnectionMetaData metadata = conn.getMetaData();
    System.out.println(metadata.getJMSVersion());
  }
}
