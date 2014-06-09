package pl.clawdivine.homeationclient;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.devices.DevicesAdapter;
import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;
import pl.clawdivine.homeationclient.devices.RemotePowerStripInfo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class RemoteDevicesFragment extends Fragment {    
	
	private ListView remoteDevicesList;
	private DevicesAdapter remoteDevicesAdapter;
	private Button buttonRefreshDeviceList;
	private List<RemoteDeviceInfo> devices = new ArrayList<RemoteDeviceInfo>();
	private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	mainActivity = (MainActivity)this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_remote_devices, container, false);
        this.remoteDevicesList = (ListView)rootView.findViewById(R.id.listView_remotedevices);
        this.buttonRefreshDeviceList = (Button)rootView.findViewById(R.id.button_refreshdevices);
        
        remoteDevicesAdapter = new DevicesAdapter(rootView.getContext(), R.layout.lv_item_remote_device, R.id.label_remotedeviceinfo);
        remoteDevicesList.setAdapter(remoteDevicesAdapter);
        
        buttonRefreshDeviceList.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	if (mainActivity != null)
            		mainActivity.ShowNoConnectionDialog();
            	RemotePowerStripInfo rmInfo = new RemotePowerStripInfo();
                rmInfo.setId(0);
                rmInfo.setName("Device 1");
                rmInfo.setType(1);
                rmInfo.setState(new boolean[]{false, true, false, true});
                
                remoteDevicesAdapter.add(rmInfo);
                
                devices.add(rmInfo);
                
                SharedPreferences mPrefs = v.getContext().getSharedPreferences(Consts.PREFS_NAME, 0);
                SharedPreferences.Editor ed = mPrefs.edit();
                Gson gson = new Gson(); 
                String devicesJsonString = gson.toJson(devices);
                ed.putString(Consts.Settings_Devices, devicesJsonString);
                ed.commit();
            }
        });
        
        SharedPreferences mPrefs = this.getActivity().getSharedPreferences(Consts.PREFS_NAME, 0);        
        Gson gson = new Gson();
        JsonParser parser=new JsonParser();
        JsonArray arr=parser.parse(mPrefs.getString(Consts.Settings_Devices, null)).getAsJsonArray();
                
        for (JsonElement jsonElement : arr)
        {
        	RemoteDeviceInfo dev = gson.fromJson(jsonElement, RemoteDeviceInfo.class);
            devices.add(dev);
            remoteDevicesAdapter.add(dev);
        }
                
        return rootView;
    }
}