package com.elasticjogger.plainq;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;

public class AppRunner
{
  ConnectionFactoryProvider connectionFactoryProvider;
  private static String LIB_PATH;
  private static String LIB_PATH_EXT;
  private static String BROKER_PROPERTIES_PATH;

  public AppRunner(String brokerPropertiesPath)
  {
    connectionFactoryProvider = new ConnectionFactoryProviderJndiImpl(brokerPropertiesPath);
  }

  public static void main(String[] args) throws Exception
  {
    initPaths(args);

    List<URL> urls = listJars(LIB_PATH_EXT);
    urls.addAll(listJars(LIB_PATH));

    ClassLoader classloader = new URLClassLoader(urls.toArray(new URL[urls.size()]), ClassLoader.getSystemClassLoader().getParent());
    Thread.currentThread().setContextClassLoader(classloader);

    Class<?> runnerClass = classloader.loadClass("com.elasticjogger.plainq.AppRunner");

    Constructor constructor = runnerClass.getConstructor(String.class);
    Object runner = constructor.newInstance(BROKER_PROPERTIES_PATH);
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
        }
      }
    }
    return result;
  }

  public static void initPaths(String[] args)
  {
    if (args.length > 0 && args[0].equals("development"))
    {
      LIB_PATH = "../temp/lib/1";
      LIB_PATH_EXT = "target/runtime/plainq/lib";
      BROKER_PROPERTIES_PATH = "src/main/resources/broker.properties";
    }
    else
    {
      LIB_PATH = "../../../../temp/lib/1";
      LIB_PATH_EXT = "lib";
      BROKER_PROPERTIES_PATH = "lib/broker.properties";
    }
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