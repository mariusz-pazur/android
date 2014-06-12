package pl.clawdivine.homeationclient.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.loopj.android.http.TextHttpResponseHandler;

import pl.clawdivine.homeationclient.*;
import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import pl.clawdivine.homeationclient.devices.*;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class RemoteDevicesFragment extends Fragment implements OnDeviceChangeListener{    
	
	private ListView remoteDevicesList;
	private DevicesAdapter remoteDevicesAdapter;
	private Button buttonRefreshDeviceList;
	private List<RemoteDeviceInfo> devices = new ArrayList<RemoteDeviceInfo>();
	private MainActivity mainActivity;
	private ConnectionManager connectionManager;
	private SharedPreferences preferences;
	private DeviceChangeBroadcastReceiver receiver;
	private ProgressBar progress;

	public void setConnectionManager(ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	mainActivity = (MainActivity)this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_remote_devices, container, false);
        this.remoteDevicesList = (ListView)rootView.findViewById(R.id.listView_remotedevices);
        this.buttonRefreshDeviceList = (Button)rootView.findViewById(R.id.button_refreshdevices);
        this.progress = (ProgressBar)rootView.findViewById(R.id.progress);
        this.progress.setVisibility(View.INVISIBLE);
        
        remoteDevicesAdapter = new DevicesAdapter(rootView.getContext(), R.layout.lv_item_remote_device, R.id.label_remotedeviceinfo);
        remoteDevicesList.setAdapter(remoteDevicesAdapter);
        
        preferences = getActivity().getSharedPreferences(Consts.PREFS_NAME, 0);
        
        
        buttonRefreshDeviceList.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	progress.setVisibility(View.VISIBLE);
            	devices.clear();
            	remoteDevicesAdapter.clear();
            	if (mainActivity != null)
            		mainActivity.ShowNoConnectionDialog();
            	
	            connectionManager.getDevices(
	            	new TextHttpResponseHandler() {
	                    @Override
	                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
	                    	SharedPreferences.Editor ed = preferences.edit();
	                    	Gson gson = new Gson();
	                        JsonParser parser=new JsonParser();
	                        JsonArray arr=parser.parse(responseString).getAsJsonArray();
	                        List<RemoteDeviceInfo> remoteDevices = RemoteDeviceInfo.getListFromJsonArray(arr, gson);	                        
	                        ed.putString(Consts.Settings_Devices, responseString);
	                        ed.commit();
	                        
	                        devices.addAll(remoteDevices);
	                        remoteDevicesAdapter.addAllFromArray(remoteDevices);
	                        progress.setVisibility(View.INVISIBLE);
	                    }
	                    @Override
	                    public void onFailure(String responseBody, Throwable e) 
	                    {	                    	
	                		Toast.makeText(getActivity().getApplicationContext(), R.string.message_fill_settings, Toast.LENGTH_LONG).show();
	                		progress.setVisibility(View.INVISIBLE);
	                    }
	                }
	            );            	
            }
        });
        
        remoteDevicesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				RemoteDeviceInfo devInfo = remoteDevicesAdapter.getItem(position);
				if (devInfo.getType() == 1)
				{
					//RemotePowerStrip
					Intent intent = new Intent(getActivity(), RemoteDevicesControlActivity.class);
					intent.putExtra(Consts.REMOTE_DEVICE_PARCELABLE, devInfo);
					startActivity(intent);
				}
				
			}
        	
        });
        
        receiver = new DeviceChangeBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter(Consts.BROADCAST_DEVICE_CHANGE_INFO);
        getActivity().registerReceiver(receiver, filter);                
                      
        Gson gson = new Gson();
        JsonParser parser=new JsonParser();
        String devs = preferences.getString(Consts.Settings_Devices, null);
        if (devs != null && !devs.equalsIgnoreCase(""))
        {
	        JsonArray arr=parser.parse(devs).getAsJsonArray();
	                
	        for (JsonElement jsonElement : arr)
	        {
	        	RemoteDeviceInfo dev = gson.fromJson(jsonElement, RemoteDeviceInfo.class);
	            devices.add(dev);
	            remoteDevicesAdapter.add(dev);
	        }
        }
                
        return rootView;
    }
        
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();        
        getActivity().unregisterReceiver(receiver);        
    }

	@Override
	public void onDeviceChange(RemoteDeviceInfo devInfo) 
	{
		boolean hasChanged = false;
		for(int i =0; i < devices.size(); i++)
		{
			RemoteDeviceInfo dev = devices.get(i);
			if (dev.getId() == devInfo.getId())
			{
				devices.set(i, devInfo);
				hasChanged = true;
			}
		}
		if (hasChanged)
		{
			remoteDevicesAdapter.clear();
			remoteDevicesAdapter.addAllFromArray(devices);
			Gson gson = new Gson();
			String devicesJson = gson.toJson(devices);
			SharedPreferences.Editor ed = preferences.edit();
        	ed.putString(Consts.Settings_Devices, devicesJson);
            ed.commit();
		}
	}

	@Override
	public void SendDeviceBroadcastChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RemoteDeviceInfo getDeviceInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDeviceChangeFail() {
		// TODO Auto-generated method stub
		
	}
}
