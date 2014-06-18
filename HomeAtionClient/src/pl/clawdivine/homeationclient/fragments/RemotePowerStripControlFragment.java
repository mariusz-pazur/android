package pl.clawdivine.homeationclient.fragments;



import org.jraf.android.backport.switchwidget.Switch;

import pl.clawdivine.homeationclient.IDeviceBaseActivity;
import pl.clawdivine.homeationclient.R;
import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.common.PreferencesHelper;
import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import pl.clawdivine.homeationclient.connectivity.RemotePowerStripReponseHandler;
import pl.clawdivine.homeationclient.devices.DeviceChangeBroadcastReceiver;
import pl.clawdivine.homeationclient.devices.OnDeviceChangeListener;
import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class RemotePowerStripControlFragment extends Fragment implements OnDeviceChangeListener
{      	
    private ConnectionManager connectionManager;
    private Button buttonEnableAll;
    private Button buttonDisableAll;
    private Button buttonSaveNames;
    private TextView textViewDeviceName;
    private Switch[] switches = new Switch[4];  
    private EditText[] switchNames = new EditText[4];
    private RemoteDeviceInfo deviceInfo; 
    private DeviceChangeBroadcastReceiver receiver;
    private Intent deviceChangeIntent;
    private RemotePowerStripReponseHandler responseHandler;
    private IDeviceBaseActivity myActivity;    
    private SharedPreferences preferences;
    
    @Override
	public void onAttach(Activity a) {
	    super.onAttach(a);
	    myActivity = (IDeviceBaseActivity)a;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_remote_strip_control, container, false);
        this.buttonEnableAll = (Button)rootView.findViewById(R.id.button_enable_all);
        this.buttonDisableAll = (Button)rootView.findViewById(R.id.button_disable_all);
        this.textViewDeviceName = (TextView)rootView.findViewById(R.id.textView_power_strip_name);
        this.switches[0] = (Switch)rootView.findViewById(R.id.switch_1);
        this.switchNames[0] = (EditText)rootView.findViewById(R.id.editText1);
        this.switches[1] = (Switch)rootView.findViewById(R.id.switch_2);
        this.switchNames[1] = (EditText)rootView.findViewById(R.id.editText2);
        this.switches[2] = (Switch)rootView.findViewById(R.id.switch_3);
        this.switchNames[2] = (EditText)rootView.findViewById(R.id.editText3);
        this.switches[3] = (Switch)rootView.findViewById(R.id.switch_4);
        this.switchNames[3] = (EditText)rootView.findViewById(R.id.editText4);
        this.buttonSaveNames = (Button)rootView.findViewById(R.id.button_save_names);
        this.responseHandler = new RemotePowerStripReponseHandler(this);        
        
        this.deviceChangeIntent = new Intent(Consts.BROADCAST_DEVICE_CHANGE_INFO);                 
        this.connectionManager = myActivity.getConnectionManager(); 
        this.deviceInfo = myActivity.getDeviceInfo();
        preferences = getActivity().getSharedPreferences(Consts.PREFS_NAME, 0);
        
        for (int i = 0; i < switches.length; i++)
        {
        	final int index = i;
        	this.switches[i].setChecked(deviceInfo.getState()[i] == 0);
        	this.switchNames[i].setText(deviceInfo.getStateNames()[i]);
        	this.switches[i].setOnCheckedChangeListener(new OnCheckedChangeListener() 
        	{
				@Override
				public void onCheckedChanged(CompoundButton switchButton, boolean state) 
				{
					if (switchButton.isPressed())
					{
						if (!myActivity.hasToShowNoConnectionDialog())
						{
							byte newState = (byte) (state ? 0 : 1);					
							if (newState == 0)//enable
								connectionManager.sendCommandEnable(deviceInfo.getId(), deviceInfo.getType(), (byte)index, responseHandler);						
							else
								connectionManager.sendCommandDisable(deviceInfo.getId(), deviceInfo.getType(), (byte)index, responseHandler);
						}
					}
				}        		
        	});
        }
        
        buttonEnableAll.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	if (!myActivity.hasToShowNoConnectionDialog())
            		connectionManager.sendCommandEnableAll(deviceInfo.getId(), deviceInfo.getType(), responseHandler);            	
            }
        });
        
        buttonDisableAll.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	if (!myActivity.hasToShowNoConnectionDialog())
            		connectionManager.sendCommandEnableAll(deviceInfo.getId(), deviceInfo.getType(), responseHandler);
            }
        });
        
        buttonSaveNames.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	String[] newNames = new String[4];
            	for (int i = 0; i < switchNames.length; i++)
                {
            		newNames[i] = switchNames[i].getText().toString();
                }
            	deviceInfo.setStateNames(newNames);
            	SendDeviceBroadcastChange();
            	Toast.makeText(getActivity().getApplicationContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
            }
        });
                            
        return rootView;
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	this.textViewDeviceName.setText(deviceInfo.getNameWithId());
    	receiver = new DeviceChangeBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(Consts.BROADCAST_DEVICE_CHANGE_INFO);
        getActivity().registerReceiver(receiver, filter); 
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
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
	public void onDeviceChange(RemoteDeviceInfo devInfo) 
	{
		PreferencesHelper.UpdateRemoteDevice(preferences, devInfo);
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
