package com.elasticjogger.plainq;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;

public class AppRunner
{

  ClassLoader mClassLoader;

  public AppRunner()
  {
    mClassLoader = this.getClass().getClassLoader();
  }

  public static void main(String[] args) throws Exception
  {
    List<URL> urls = listJars("../temp/lib");
    urls.addAll(listJars("target/runtime/plainq/lib"));
    ClassLoader classloader = new URLClassLoader(urls.toArray(new URL[urls.size()]), ClassLoader.getSystemClassLoader().getParent());

    Class<?> runnerClass = classloader.loadClass("com.elasticjogger.plainq.AppRunner");

    Object runner = runnerClass.newInstance();
    Method start = runnerClass.getMethod("start");
    start.invoke(runner);
  }

  public void start() throws Exception
  {
    Class amqClass = mClassLoader.loadClass("org.apache.activemq.ActiveMQConnectionFactory");

    ConnectionFactory factory = (ConnectionFactory) amqClass.newInstance();
    Connection conn = factory.createConnection();
    ConnectionMetaData metadata = conn.getMetaData();
    System.out.println(metadata.getJMSVersion());
  }

  public static List<URL> listJars(String dirPath) throws MalformedURLException
  {
    File dir = new File(dirPath);
    File[] files = dir.listFiles();

    List<URL> result = new ArrayList<>();
    for (int i = 0; i < files.length; i++)
    {
      File file = files[i];
      if (file.isFile())
      {
        result.add(file.toURI().toURL());
      }
    }

    return result;
  }
}