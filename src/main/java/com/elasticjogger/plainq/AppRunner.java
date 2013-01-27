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
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;

public class AppRunner
{
  ConnectionProvider connectionProvider;
  private static String LIB_PATH;
  private static String APP_LIB_PATH;
  private static String BROKER_PROPERTIES_PATH;
  private static final Logger log = Logger.getLogger(AppRunner.class.getName());

  public AppRunner(String brokerPropertiesPath) throws Exception
  {
    connectionProvider = new ConnectionProviderJndiImpl(brokerPropertiesPath);
    configureLogger();
  }

  private void configureLogger()
  {
    Logger logger = Logger.getLogger("com.elasticjogger");
    logger.addHandler(new ConsoleHandler());

    logger.setLevel(Level.FINE);
  }

  public static void main(String[] args) throws Exception
  {
    initPaths(args);

    List<URL> urls = listJars(LIB_PATH, APP_LIB_PATH);

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
    Connection connection = connectionProvider.createConnection();

    JMSWorker jmsWorker = new JMSWorker(connection);
    log(jmsWorker.getProviderInfo());
    log(jmsWorker.getClientID());

    jmsWorker.sendTextMessageToQueue("myQueue", "i am the message");
    jmsWorker.sendTextMessageToTopic("myTopic", "i am the message");

    jmsWorker.stop();
  }

  public static List<URL> listJars(String... dirPaths) throws MalformedURLException
  {
    List<URL> result = new ArrayList<>();
    for (String path : dirPaths)
    {
      File dir = new File(path);
      File[] files = dir.listFiles();

      if (files != null)
      {
        for (int i = 0; i < files.length; i++)
        {
          File file = files[i];
          if (file.isFile() && file.getName().endsWith("jar"))
          {
            result.add(file.toURI().toURL());
            log.info(file.toURI().toURL().toString());
          }
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
      APP_LIB_PATH = "target";
      BROKER_PROPERTIES_PATH = "src/main/resources/broker.properties";
    }
    else
    {
      LIB_PATH = "../../../../temp/lib/1";//"lib";
      APP_LIB_PATH = ".";
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
