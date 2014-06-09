package pl.clawdivine.homeationclient.connectivity;

import java.util.ArrayList;
import java.util.Locale;

import pl.clawdivine.homeationclient.common.Consts;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

public class ConnectionManager 
{
	private WifiManager wifi;

    public ConnectionManager(Context context)
    {
    	wifi= (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }
    
    public String getHomeAtionMainIP()
    {
    	int maxAddress = 254;            	
        //tutaj wykonujemy skanowanie lokalnej sieci            	
    	DhcpInfo d=wifi.getDhcpInfo();
        int ipAddress = d.ipAddress;
        String ipAddressString = Consts.intToIp(ipAddress);
        int subnetMask = d.netmask;
        String subnetMaskString = Consts.intToIp(subnetMask);
        String[] netmaskElements = subnetMaskString.split("[.]");
        String[] ipAddressElements = ipAddressString.split("[.]");
        String result = ipAddressString + "(" + subnetMaskString + ")";
        ArrayList<ArrayList<Integer>> ipAddressRanges = new ArrayList<ArrayList<Integer>>();  
        ArrayList<String> possibleAddresses = new ArrayList<String>();
        
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
        for(int i = 0; i < ipAddressRanges.get(0).size(); i++)
        {
        	for(int j = 0; j < ipAddressRanges.get(1).size(); j++)
            {
        		for(int k = 0; k < ipAddressRanges.get(2).size(); k++)
                {
        			for(int l = 0; l < ipAddressRanges.get(3).size(); l++)
                    {
                    	String address = String.format(Locale.getDefault(), "%d.%d.%d.%d", 
                    			ipAddressRanges.get(0).get(i), 
                    			ipAddressRanges.get(1).get(j), 
                    			ipAddressRanges.get(2).get(k), 
                    			ipAddressRanges.get(3).get(l));
                    	if (!address.equalsIgnoreCase(ipAddressString))
                    		possibleAddresses.add(address);
                    }
                }
            }
        }
        
        return result;
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