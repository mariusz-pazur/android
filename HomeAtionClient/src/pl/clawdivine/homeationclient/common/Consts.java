package pl.clawdivine.homeationclient.common;

public class Consts 
{
	public static final String PREFS_NAME = "SettingsFile";	
    public static final String HOME_ATION_ECHO_RESPONSE = "HomeAtionMain";    
    public static final String REMOTE_DEVICE_PARCELABLE = "RemoteDeviceInfo";
    public static final String BROADCAST_DEVICE_CHANGE_INFO = "BroadcastDeviceChangeInfo";
    public static final int UDP_BROADCAST_PORT = 1337;    
    
    public static String intToIp(int addr) 
    {
        return  ((addr & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF));
    }
}
