package pl.clawdivine.homeationclient.connectivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.common.PreferencesHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

import org.apache.http.Header;

public class ConnectionManager 
{
	private WifiManager wifi;
	private String homeAtionIpAddress = "";
	private int calls = 0;
	private int responses = 0;
	private SharedPreferences preferences;

    public ConnectionManager(Context context)
    {
    	wifi= (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	preferences = context.getSharedPreferences(Consts.PREFS_NAME, 0);
    	homeAtionIpAddress = PreferencesHelper.ReadHomeAtionIpAddress(preferences);
    }
    
    public String getHomeAtionIpAddress()
    {
    	return homeAtionIpAddress;
    }
    
    public void saveHomeAtionIpAddress(String ipAddress)
    {    	
    	this.homeAtionIpAddress = ipAddress;
        PreferencesHelper.WriteHomeAtionIpAddress(preferences, ipAddress);        
    }
    
    public String detectHomeAtionMainIP2(int timeout)
    {
    	homeAtionIpAddress = "";
    	DatagramSocket socket = null;
    	DatagramPacket packet;
    	DatagramPacket receivedPacket = null;
    	InetAddress broadcast = getBroadcastAddress();
    	byte[] buf = new byte[1024];
    	if (broadcast != null)
    	{
    		//MulticastLock lock = wifi.createMulticastLock("pl.clawdivine.homeationclient");
    		//lock.acquire();
			try {
				socket = new DatagramSocket(Consts.UDP_BROADCAST_PORT);
				socket.setBroadcast(true);
				byte[] data = Consts.HOME_ATION_ECHO_RESPONSE.getBytes();
				packet = new DatagramPacket(data, data.length, broadcast, Consts.UDP_BROADCAST_PORT);
				socket.send(packet);
				receivedPacket = new DatagramPacket(buf, buf.length);
				socket.setSoTimeout(timeout*1000);
		    	socket.receive(receivedPacket);		    	
			} catch (SocketException e1) {
				homeAtionIpAddress = "";
				
			} catch (IOException e) {
				homeAtionIpAddress = "";
			}
	    	finally {
	    		if (socket != null && !socket.isClosed())
	    			socket.close();
	    	}
			//lock.release();
			if (receivedPacket != null)
			{
				String response = new String(receivedPacket.getData());
				if (response.equalsIgnoreCase(Consts.HOME_ATION_ECHO_RESPONSE))
				{
					String hostnameAndAddress = receivedPacket.getAddress().toString();
					homeAtionIpAddress = hostnameAndAddress.substring(hostnameAndAddress.indexOf('/')+1); 
				}
			}
    	}
    	return homeAtionIpAddress;
    }
    
    public String detectHomeAtionMainIP(int timeoutInSeconds)
    {
    	homeAtionIpAddress = "";
    	calls = 0;
    	responses = 0;
    	int maxAddress = 254;            	
        //tutaj wykonujemy skanowanie lokalnej sieci            	
    	DhcpInfo d=wifi.getDhcpInfo();
        int ipAddress = d.ipAddress;
        String ipAddressString = Consts.intToIp(ipAddress);
        int subnetMask = d.netmask;
        String subnetMaskString = Consts.intToIp(subnetMask);
        String[] netmaskElements = subnetMaskString.split("[.]");
        String[] ipAddressElements = ipAddressString.split("[.]");        
        ArrayList<ArrayList<Integer>> ipAddressRanges = new ArrayList<ArrayList<Integer>>();          
        
        for(int i = 0; i < netmaskElements.length; i++)
        {
        	ipAddressRanges.add(i, new ArrayList<Integer>());
        	int maskElement = Integer.parseInt(netmaskElements[i]);
        	if (maskElement < maxAddress)
        	{                		
        			int ipElement = Integer.parseInt(ipAddressElements[i]);
        			int startRange = (maskElement & ipElement) + 1;
        			int nrOfElements = maxAddress - maskElement;
        			for (int j = startRange; j <= startRange + nrOfElements - 1; j++ )
        			{
        				ipAddressRanges.get(i).add(j);
        			}                		              	
        	}
        	else
        	{
        		ipAddressRanges.get(i).add(Integer.parseInt(ipAddressElements[i]));
        	}
        }  
        boolean hasAddressFound = false;
        for(int i = 0; i < ipAddressRanges.get(0).size() && !hasAddressFound; i++)
        {
        	for(int j = 0; j < ipAddressRanges.get(1).size() && !hasAddressFound; j++)
            {
        		for(int k = 0; k < ipAddressRanges.get(2).size() && !hasAddressFound; k++)
                {
        			for(int l = 0; l < ipAddressRanges.get(3).size() && !hasAddressFound; l++)
                    {
                    	String address = String.format(Locale.getDefault(), "%d.%d.%d.%d", 
                    			ipAddressRanges.get(0).get(i), 
                    			ipAddressRanges.get(1).get(j), 
                    			ipAddressRanges.get(2).get(k), 
                    			ipAddressRanges.get(3).get(l));
                    	if (!address.equalsIgnoreCase(ipAddressString))
                    	{
                    		calls++;
                    		HomeAtionHttpClient.echo(address, new TextHttpResponseHandler() 
                    		{
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString)
                                {
                                	responses++;
                                	if (responseString.equalsIgnoreCase(Consts.HOME_ATION_ECHO_RESPONSE))
                                	{
                                		homeAtionIpAddress = this.getRequestURI().getHost();
                                	}
                                } 
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable exception)
                                {
                                	responses++;
                                }
                            });
                    	}
                    	if (homeAtionIpAddress != "")
                    		hasAddressFound = true;
                    }
                }
            }
        }
        long startWaiting = System.currentTimeMillis();
        while(calls != responses && homeAtionIpAddress == "" && (System.currentTimeMillis() - startWaiting) < timeoutInSeconds * 1000 ) {}
        
        return homeAtionIpAddress;
    }
    
    public InetAddress getBroadcastAddress() {
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
          quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        try {
			return InetAddress.getByAddress(quads);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public void getDevices(AsyncHttpResponseHandler handler)
    {
    	HomeAtionHttpClient.getDevices(homeAtionIpAddress,handler);
    }
    
    public void getEchoResponse(String ipAddress, AsyncHttpResponseHandler handler)
    {
    	HomeAtionHttpClient.echo(ipAddress,handler);
    }
    
    public void sendCommandEnable(byte id, byte type, byte switchNr, AsyncHttpResponseHandler responseHandler)
    {
  	  	HomeAtionHttpClient.sendCommandEnable(homeAtionIpAddress, id, type, switchNr, responseHandler);  	  	
    }
    
    public void sendCommandDisable(byte id, byte type, byte switchNr, AsyncHttpResponseHandler responseHandler)
    {
    	HomeAtionHttpClient.sendCommandDisable(homeAtionIpAddress, id, type, switchNr, responseHandler);  
    }
    
    public void sendCommandEnableAll(byte id, byte type,  AsyncHttpResponseHandler responseHandler)
    {
    	HomeAtionHttpClient.sendCommandEnableAll(homeAtionIpAddress, id, type, responseHandler);  
    }
    
    public void sendCommandDisableAll(byte id, byte type, AsyncHttpResponseHandler responseHandler)
    {
    	HomeAtionHttpClient.sendCommandDisableAll(homeAtionIpAddress, id, type, responseHandler);  
    }
    
    public boolean isWiFiEnabled()
    {
    	return wifi.isWifiEnabled();
    }  
    
    public void ToggleWiFiStatus(boolean isEnabled)
    {
    	wifi.setWifiEnabled(isEnabled);
    	while(isWiFiEnabled() != isEnabled)
    	{
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				break;
			}    	
    	}
    }
}
