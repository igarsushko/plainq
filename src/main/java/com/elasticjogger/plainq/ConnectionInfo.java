package com.elasticjogger.plainq;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ConnectionInfo
{
  String JMSVersion;
  String JMSProviderName;
  String providerVersion;
  List<String> JMSXPropertyNames;
  String clientID;

  public String getJMSVersion()
  {
    return JMSVersion;
  }

  public void setJMSVersion(String JMSVersion)
  {
    this.JMSVersion = JMSVersion;
  }

  public String getJMSProviderName()
  {
    return JMSProviderName;
  }

  public void setJMSProviderName(String JMSProviderName)
  {
    this.JMSProviderName = JMSProviderName;
  }

  public String getProviderVersion()
  {
    return providerVersion;
  }

  public void setProviderVersion(String providerVersion)
  {
    this.providerVersion = providerVersion;
  }

  public List<String> getJMSXPropertyNames()
  {
    return JMSXPropertyNames;
  }

  public void setJMSXPropertyNames(Enumeration e)
  {
    JMSXPropertyNames = new ArrayList<>();
    if (e != null)
    {
      while (e.hasMoreElements())
      {
        JMSXPropertyNames.add(e.nextElement().toString());
      }
    }
  }

  public String getClientID()
  {
    return clientID;
  }

  public void setClientID(String clientID)
  {
    this.clientID = clientID;
  }

}
