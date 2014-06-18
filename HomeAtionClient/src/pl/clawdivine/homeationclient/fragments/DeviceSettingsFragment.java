package pl.clawdivine.homeationclient.fragments;

import pl.clawdivine.homeationclient.IDeviceBaseActivity;
import pl.clawdivine.homeationclient.R;
import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.devices.DeviceChangeBroadcastReceiver;
import pl.clawdivine.homeationclient.devices.OnDeviceChangeListener;
import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeviceSettingsFragment extends Fragment implements OnDeviceChangeListener {
    
	private Button saveDeviceButton;    
    private EditText editTextDeviceName;    
    private RemoteDeviceInfo deviceInfo;  
    private DeviceChangeBroadcastReceiver receiver;
    private Intent deviceChangeIntent;
    private IDeviceBaseActivity myActivity;      
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }
    
    @Override
	public void onAttach(Activity a) {
	    super.onAttach(a);
	    myActivity = (IDeviceBaseActivity)a;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_device_settings, container, false);
        editTextDeviceName = (EditText) rootView.findViewById(R.id.editText_device_name);                      
        deviceInfo = myActivity.getDeviceInfo();
        editTextDeviceName.setText(deviceInfo.getName());
        saveDeviceButton = (Button)rootView.findViewById(R.id.button_save_device);               
        
        
        saveDeviceButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	String newName = editTextDeviceName.getText().toString();
            	deviceInfo.setName(newName);
            	SendDeviceBroadcastChange();  
            	Toast.makeText(getActivity().getApplicationContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
            }
        });
        
         
        
        this.deviceChangeIntent = new Intent(Consts.BROADCAST_DEVICE_CHANGE_INFO);
        return rootView;
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
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

	@Override
	public void onDeviceChange(RemoteDeviceInfo devInfo) 
	{
		this.deviceInfo = devInfo;
		editTextDeviceName.setText(deviceInfo.getName());
	}  	

	@Override
	public void SendDeviceBroadcastChange() 
	{
		deviceChangeIntent.putExtra(Consts.REMOTE_DEVICE_PARCELABLE, deviceInfo);
    	getActivity().sendBroadcast(deviceChangeIntent); 		
	}

	@Override
	public RemoteDeviceInfo getDeviceInfo() {
		
		return this.deviceInfo;
	}

	@Override
	public void onDeviceChangeFail() {
				
	}
}