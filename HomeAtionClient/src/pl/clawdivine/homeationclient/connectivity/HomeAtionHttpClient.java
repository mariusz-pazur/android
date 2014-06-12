package pl.clawdivine.homeationclient.connectivity;

import android.annotation.SuppressLint;
import com.loopj.android.http.*;

@SuppressLint("DefaultLocale")
public class HomeAtionHttpClient 
{
  private static final String HTTP_URL = "http://";
  private static final String ECHO_METHOD = "/echo";
  private static final String DEVICES_METHOD = "/devices";
  private static final String COMMAND_METHOD = "/command?id=%d&type=%d;cmd=%d;param=%d";

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void echo(String ipAddress, AsyncHttpResponseHandler responseHandler) 
  {
      client.get(getMethodUrl(ipAddress, ECHO_METHOD), null, responseHandler);
  }
  
  public static void getDevices(String ipAddress, AsyncHttpResponseHandler responseHandler) 
  {
      client.get(getMethodUrl(ipAddress, DEVICES_METHOD), null, responseHandler);
  }
  
  @SuppressLint("DefaultLocale")
public static void sendCommandEnable(String ipAddress, byte id, byte type, byte switchNr, AsyncHttpResponseHandler responseHandler)
  {
	  String enableMethod = String.format(COMMAND_METHOD, id, type, 0, switchNr);
	  client.get(getMethodUrl(ipAddress, enableMethod), null, responseHandler);
  }
  
  public static void sendCommandDisable(String ipAddress, byte id, byte type, byte switchNr, AsyncHttpResponseHandler responseHandler)
  {
	  String disableMethod = String.format(COMMAND_METHOD, id, type, 1, switchNr);
	  client.get(getMethodUrl(ipAddress, disableMethod), null, responseHandler);
  }
  
  @SuppressLint("DefaultLocale")
public static void sendCommandEnableAll(String ipAddress, byte id, byte type,  AsyncHttpResponseHandler responseHandler)
  {
	  String enableAllMethod = String.format(COMMAND_METHOD, id, type, 4, 0);
	  client.get(getMethodUrl(ipAddress, enableAllMethod), null, responseHandler);
  }
  
  @SuppressLint("DefaultLocale")
public static void sendCommandDisableAll(String ipAddress, byte id, byte type, AsyncHttpResponseHandler responseHandler)
  {
	  String disableAllMethod = String.format(COMMAND_METHOD, id, type, 5, 0);
	  client.get(getMethodUrl(ipAddress, disableAllMethod), null, responseHandler);
  }
  
  private static String getMethodUrl(String ipAddress, String methodUrl) 
  {
      return HTTP_URL + ipAddress + methodUrl;
  }
}
