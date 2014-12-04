package pl.clawdivine.homeationclient.common;

public class Consts 
{
	public static final String PREFS_NAME = "SettingsFile";	
    public static final String HOME_ATION_ECHO_REQUEST = "HomeAtionMainRequest";
    public static final String HOME_ATION_ECHO_RESPONSE = "HomeAtionMain";  
    public static final String REMOTE_DEVICE_PARCELABLE = "RemoteDeviceInfo";
    public static final String BROADCAST_DEVICE_CHANGE_INFO = "BroadcastDeviceChangeInfo";
    public static final int UDP_REQUEST_BROADCAST_SOURCE_PORT = 39999;
    public static final int UDP_REQUEST_BROADCAST_PORT = 40000;    
    public static final int UDP_RESPONSE_BROADCAST_PORT = 40001;
    
    public static String intToIp(int addr) 
    {
        return  ((addr & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF));
    }
}
