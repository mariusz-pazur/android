package pl.clawdivine.homeationclient.devices;

import pl.clawdivine.homeationclient.common.Consts;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceChangeBroadcastReceiver extends BroadcastReceiver {

	private OnDeviceChangeListener deviceChangeListener;
	public DeviceChangeBroadcastReceiver(OnDeviceChangeListener deviceChangeListener)
	{
		super();
		this.deviceChangeListener = deviceChangeListener;
	}
	@Override
    public void onReceive(Context context, Intent intent) 
	{
        String action = intent.getAction();            
        if (Consts.BROADCAST_DEVICE_CHANGE_INFO.equals(action)) 
        {
        	RemoteDeviceInfo devInfo = intent.getParcelableExtra(Consts.REMOTE_DEVICE_PARCELABLE);
        	deviceChangeListener.onDeviceChange(devInfo);
        }
    }
}