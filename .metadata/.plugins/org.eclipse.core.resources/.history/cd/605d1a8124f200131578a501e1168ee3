package pl.clawdivine.homeationclient.connectivity;

import com.loopj.android.http.*;

public class HomeAtionHttpClient 
{
  private static final String HTTP_URL = "http://";
  private static final String ECHO_METHOD = "/echo";
  private static final String DEVICES_METHOD = "/devices";

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void echo(String ipAddress, AsyncHttpResponseHandler responseHandler) 
  {
      client.get(getMethodUrl(ipAddress, ECHO_METHOD), null, responseHandler);
  }
  
  public static void getDevices(String ipAddress, AsyncHttpResponseHandler responseHandler) 
  {
      client.get(getMethodUrl(ipAddress, DEVICES_METHOD), null, responseHandler);
  }
  
  private static String getMethodUrl(String ipAddress, String methodUrl) 
  {
      return HTTP_URL + ipAddress + methodUrl;
  }
}
