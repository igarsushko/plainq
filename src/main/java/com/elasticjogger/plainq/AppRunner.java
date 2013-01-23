package com.elasticjogger.plainq;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class AppRunner
{

  public static void main(String[] args) throws Exception
  {
    AppRunner runner = new AppRunner();
    runner.start();
  }

  /**
   * Starts application and adds shutdown hook.
   */
  public void start() throws Exception
  {
    List<URL> urls = listJars("../temp/lib");
    urls.addAll(listJars("target/runtime/plainq/lib"));
    ClassLoader classloader = new URLClassLoader(urls.toArray(new URL[urls.size()]), ClassLoader.getSystemClassLoader().getParent());

    Class<?> runnerClass = classloader.loadClass("com.elasticjogger.plainq.Runner");

    Object runner = runnerClass.newInstance();
    Method start = runnerClass.getMethod("start");
    start.invoke(runner);
  }

  public List<URL> listJars(String dirPath) throws MalformedURLException
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