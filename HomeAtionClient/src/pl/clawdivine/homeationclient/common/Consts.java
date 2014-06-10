package pl.clawdivine.homeationclient.common;

public class Consts 
{
	public static final String PREFS_NAME = "SettingsFile";
	public static final String Settings_Devices = "Settings_Devices";	
    public static final String Settings_IP = "Settings_IP";
    public static final String HOME_ATION_ECHO_RESPONSE = "HomeAtionMain";    
    public static final String REMOTE_DEVICE_PARCELABLE = "RemoteDeviceInfo";
    
    public static String intToIp(int addr) 
    {
        return  ((addr & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF) + "." + 
                ((addr >>>= 8) & 0xFF));
    }
}
