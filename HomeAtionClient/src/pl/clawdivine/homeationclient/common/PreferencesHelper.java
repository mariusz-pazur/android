package pl.clawdivine.homeationclient.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;
import android.content.SharedPreferences;

public class PreferencesHelper 
{
	private static final String Settings_Devices = "Settings_Devices";	
    private static final String Settings_IP = "Settings_IP";
    
    public static String ReadHomeAtionIpAddress(SharedPreferences preferences)
    {
		return preferences.getString(Settings_IP, "");    	
    }
    
    public static void WriteHomeAtionIpAddress(SharedPreferences preferences, String ipAddress)
    {
    	SharedPreferences.Editor editor = preferences.edit();                
        editor.putString(Settings_IP, ipAddress);        
        editor.commit();
    }
    
    public static List<RemoteDeviceInfo> ReadRemoteDevices(SharedPreferences preferences)
    {
    	List<RemoteDeviceInfo> devices = new ArrayList<RemoteDeviceInfo>();
    	Gson gson = new Gson();
        JsonParser parser=new JsonParser();
        String devs = preferences.getString(Settings_Devices, null);
        if (devs != null && !devs.equalsIgnoreCase(""))
        {
	        JsonArray arr=parser.parse(devs).getAsJsonArray();
	                
	        for (JsonElement jsonElement : arr)
	        {
	        	RemoteDeviceInfo dev = gson.fromJson(jsonElement, RemoteDeviceInfo.class);
	            devices.add(dev);	            
	        }
        }
        
        return devices;
    }
    
    public static void WriteRemoteDevices(SharedPreferences preferences, List<RemoteDeviceInfo> devices)
    {
    	SharedPreferences.Editor ed = preferences.edit();
    	Gson gson = new Gson();        
        String devicesJson = gson.toJson(devices);                               
        ed.putString(Settings_Devices, devicesJson);
        ed.commit();
    }
    
    public static void UpdateRemoteDevice(SharedPreferences preferences, RemoteDeviceInfo device)
    {
    	List<RemoteDeviceInfo> devices = ReadRemoteDevices(preferences);
    	boolean hasChanged = false;
    	if (devices != null)
    	{    		
    		for(int i =0; i < devices.size(); i++)
    		{
    			RemoteDeviceInfo dev = devices.get(i);
    			if (dev != null && dev.getId() == device.getId())
				{
					devices.set(i, device);
					hasChanged = true;
				}
    		}
    	}
    	if (hasChanged)
    	{
    		WriteRemoteDevices(preferences, devices);
    	}
    }
}
