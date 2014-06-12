package pl.clawdivine.homeationclient.connectivity;

import org.apache.http.Header;

import pl.clawdivine.homeationclient.devices.OnDeviceChangeListener;
import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

public class RemotePowerStripReponseHandler extends TextHttpResponseHandler 
{
	private OnDeviceChangeListener deviceChangeListener;
	
	public RemotePowerStripReponseHandler(OnDeviceChangeListener deviceChangeListener)
	{
		super();
		this.deviceChangeListener = deviceChangeListener;
	} 
	
	@Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
    	Gson gson = new Gson();			                        
        RemoteDeviceInfo dev = gson.fromJson(responseString, RemoteDeviceInfo.class);
        deviceChangeListener.getDeviceInfo().setState(dev.getState());
        deviceChangeListener.SendDeviceBroadcastChange();
    }
	
	@Override
    public void onFailure(String responseBody, Throwable e) {		
		deviceChangeListener.SendDeviceBroadcastChange();
		deviceChangeListener.onDeviceChangeFail();
    }

}
