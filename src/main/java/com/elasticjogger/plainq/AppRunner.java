package com.elasticjogger.plainq;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.jms.Connection;

public class AppRunner
{
  ConnectionProvider connectionProvider;
  private static List<URL> URLS;
  private static String BROKER_PROPERTIES_PATH;
  private static final Logger log = Logger.getLogger(AppRunner.class.getName());

  public AppRunner(String brokerPropertiesPath) throws Exception
  {
    connectionProvider = new ConnectionProviderJndiImpl(brokerPropertiesPath);
    configureLogger();
  }

  private void configureLogger()
  {
    //Reset configuration so default configuration is not applied
    //default config is at %JAVA_HOME%\jdk${version}\jre\lib\logging.properties
    LogManager.getLogManager().reset();
    System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$td.%1$tm.%1$tY %1$tT.%1$tL] %2$s [%4$s] - %5$s%6$s%n");

    Logger logger = Logger.getLogger("com.elasticjogger");
    ConsoleHandler ch = new ConsoleHandler();
    ch.setFormatter(new SimpleFormatter());
    logger.addHandler(ch);

    logger.setLevel(Level.FINE);
  }

  public static void main(String[] args) throws Exception
  {
    initVars(args);

    ClassLoader classloader = new URLClassLoader(URLS.toArray(new URL[URLS.size()]), ClassLoader.getSystemClassLoader().getParent());
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

    log.info(jmsWorker.getProviderInfo().toString());
    log.info(jmsWorker.getClientID());

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
            log.fine(file.toURI().toURL().toString());
          }
        }
      }
    }
    return result;
  }

  public static void initVars(String[] args) throws Exception
  {
    URLS = new ArrayList<>();
    if (args.length > 0 && args[0].equals("development"))
    {
      URLS.addAll(listJars("../temp/lib/1"));
      URLS.add(new File("target/classes/").toURI().toURL());
      BROKER_PROPERTIES_PATH = "src/main/resources/broker.properties";
    }
    else
    {
      URLS.addAll(listJars(".", "../../../../temp/lib/1"));//"lib";
      BROKER_PROPERTIES_PATH = "lib/broker.properties";
    }
  }
}
