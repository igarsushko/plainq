package com.elasticjogger.plainq;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;

public class AppRunner
{
  ConnectionFactoryProvider connectionFactoryProvider = new ConnectionFactoryProviderJndiImpl("src/main/resources/broker.properties");
  ClassLoader classLoader;

  public AppRunner()
  {
    classLoader = this.getClass().getClassLoader();
  }

  public static void main1(String[] args) throws Exception
  {
    AppRunner runner = new AppRunner();
    runner.start();
  }

  public static void main(String[] args) throws Exception
  {
    String libPath = null;
    String libPathExt = null;

    if (args.length > 0)
    {
      libPath = "../temp/lib/1";
      libPathExt = "target/runtime/plainq/lib";
    }
    else
    {
      libPath = "../../../../temp/lib/1";
      libPathExt = "lib";
    }

    List<URL> urls = listJars(libPathExt);
    urls.addAll(listJars(libPath));

    ClassLoader classloader = new URLClassLoader(urls.toArray(new URL[urls.size()]), ClassLoader.getSystemClassLoader().getParent());
    Thread.currentThread().setContextClassLoader(classloader);

    Class<?> runnerClass = classloader.loadClass("com.elasticjogger.plainq.AppRunner");

    Object runner = runnerClass.newInstance();
    Method start = runnerClass.getMethod("start");
    start.invoke(runner);
  }

  public void start() throws Exception
  {
    ConnectionFactory factory = connectionFactoryProvider.getConnectionFactory();
    Connection connection = factory.createConnection();
    connection.start();

    JMSWorker jmsWorker = new JMSWorker(connection);
    log(jmsWorker.getProviderInfo());
    log(jmsWorker.getClientID());

    connection.close();
  }

  public static List<URL> listJars(String dirPath) throws MalformedURLException
  {
    File dir = new File(dirPath);
    File[] files = dir.listFiles();

    List<URL> result = new ArrayList<>();
    if (files != null)
    {
      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        if (file.isFile())
        {
          result.add(file.toURI().toURL());
          System.out.println(file.toURI().toURL());
        }
      }
    }
    return result;
  }

  public static void log(Object o)
  {
    if (o == null)
    {
      System.out.println("null");
    }
    else if (o instanceof Enumeration)
    {
      Enumeration e = (Enumeration) o;
      while (e.hasMoreElements())
      {
        log(e.nextElement());
      }
    }
    else
    {
      System.out.println(o);
    }
  }
}