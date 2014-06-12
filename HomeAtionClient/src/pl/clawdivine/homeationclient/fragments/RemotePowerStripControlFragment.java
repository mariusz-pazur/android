package pl.clawdivine.homeationclient.fragments;



import org.jraf.android.backport.switchwidget.Switch;

import pl.clawdivine.homeationclient.R;
import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import pl.clawdivine.homeationclient.connectivity.RemotePowerStripReponseHandler;
import pl.clawdivine.homeationclient.devices.DeviceChangeBroadcastReceiver;
import pl.clawdivine.homeationclient.devices.OnDeviceChangeListener;
import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class RemotePowerStripControlFragment extends Fragment implements OnDeviceChangeListener
{      	
    private ConnectionManager connectionManager;
    private Button buttonEnableAll;
    private Button buttonDisableAll;
    private TextView textViewDeviceName;
    private Switch[] switches = new Switch[4];    
    private RemoteDeviceInfo deviceInfo; 
    private DeviceChangeBroadcastReceiver receiver;
    private Intent deviceChangeIntent;
    private RemotePowerStripReponseHandler responseHandler;
    
    public void setDeviceInfo(RemoteDeviceInfo deviceInfo)
    {
    	this.deviceInfo = deviceInfo;
    }
    
    public void setConnectionManager(ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_remote_strip_control, container, false);
        this.buttonEnableAll = (Button)rootView.findViewById(R.id.button_enable_all);
        this.buttonDisableAll = (Button)rootView.findViewById(R.id.button_disable_all);
        this.textViewDeviceName = (TextView)rootView.findViewById(R.id.textView_power_strip_name);
        this.switches[0] = (Switch)rootView.findViewById(R.id.switch_1);
        this.switches[1] = (Switch)rootView.findViewById(R.id.switch_2);
        this.switches[2] = (Switch)rootView.findViewById(R.id.switch_3);
        this.switches[3] = (Switch)rootView.findViewById(R.id.switch_4);
        
        this.responseHandler = new RemotePowerStripReponseHandler(this);
        receiver = new DeviceChangeBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(Consts.BROADCAST_DEVICE_CHANGE_INFO);
        getActivity().registerReceiver(receiver, filter); 
        
        this.deviceChangeIntent = new Intent(Consts.BROADCAST_DEVICE_CHANGE_INFO);                 
             
        for (int i = 0; i < switches.length; i++)
        {
        	final int index = i;
        	this.switches[i].setChecked(deviceInfo.getState()[i] == 0);
        	this.switches[i].setOnCheckedChangeListener(new OnCheckedChangeListener() 
        	{
				@Override
				public void onCheckedChanged(CompoundButton switchButton, boolean state) 
				{
					if (switchButton.isPressed())
					{
						
						byte newState = (byte) (state ? 0 : 1);					
						if (newState == 0)//enable
							connectionManager.sendCommandEnable(deviceInfo.getId(), deviceInfo.getType(), (byte)index, responseHandler);						
						else
							connectionManager.sendCommandDisable(deviceInfo.getId(), deviceInfo.getType(), (byte)index, responseHandler);		            	
					}
				}        		
        	});
        }
        
        buttonEnableAll.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	connectionManager.sendCommandEnableAll(deviceInfo.getId(), deviceInfo.getType(), responseHandler);            	
            }
        });
        
        buttonDisableAll.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	connectionManager.sendCommandEnableAll(deviceInfo.getId(), deviceInfo.getType(), responseHandler);
            }
        });
                            
        return rootView;
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();        
        getActivity().unregisterReceiver(receiver);        
    }
    
    private void UpdateSwitchState()
    {
    	for (int i = 0; i < switches.length; i++)
        {        	
        	this.switches[i].setChecked(deviceInfo.getState()[i] == 0);        
        }
    }
    
    public void SendDeviceBroadcastChange()
    {    	
    	deviceChangeIntent.putExtra(Consts.REMOTE_DEVICE_PARCELABLE, deviceInfo);
    	getActivity().sendBroadcast(deviceChangeIntent); 
    }
    
    @Override
    public void onResume()
    {
    	this.textViewDeviceName.setText(deviceInfo.getNameWithId());
    	super.onResume();    	
    }

	@Override
	public void onDeviceChange(RemoteDeviceInfo devInfo) 
	{
		this.deviceInfo = devInfo;
		this.textViewDeviceName.setText(deviceInfo.getNameWithId());
		UpdateSwitchState();
	}

	@Override
	public RemoteDeviceInfo getDeviceInfo() {
		
		return this.deviceInfo;
	}

	@Override
	public void onDeviceChangeFail() 
	{
		Toast.makeText(getActivity().getApplicationContext(), R.string.message_fill_settings, Toast.LENGTH_LONG).show();	
	}       
}
