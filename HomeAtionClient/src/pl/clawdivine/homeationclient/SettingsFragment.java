package pl.clawdivine.homeationclient;

import pl.clawdivine.homeationclient.common.Consts;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {
    
    private Button autoDetectButton;
    private Button saveSettingsButton;    
    private EditText editTextIp;
    private DhcpInfo d;
    private WifiManager wifi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        editTextIp = (EditText) rootView.findViewById(R.id.editText_ipaddress);
               
        // Restore preferences
        SharedPreferences settings = this.getActivity().getSharedPreferences(Consts.PREFS_NAME, 0);
        String ip = settings.getString(Consts.Settings_IP, "192.168.0.6");
        editTextIp.setText(ip);
        
        this.autoDetectButton = (Button)rootView.findViewById(R.id.button_autodetect);
        this.saveSettingsButton = (Button)rootView.findViewById(R.id.button_savesettings);
        
        wifi= (WifiManager)this.getActivity().getSystemService(Context.WIFI_SERVICE);
        
        autoDetectButton.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
			public void onClick(View v)
            {
            	int maxAddress = 254;            	
                //tutaj wykonujemy skanowanie lokalnej sieci            	
                d=wifi.getDhcpInfo();
                int ipAddress = d.ipAddress;
                String ipAddressString = Consts.intToIp(ipAddress);
                int subnetMask = d.netmask;
                String subnetMaskString = Consts.intToIp(subnetMask);
                String[] netmaskElements = subnetMaskString.split("[.]");
                String[] ipAddressElements = ipAddressString.split("[.]");
                editTextIp.setText(ipAddressString + "(" + subnetMaskString + ")");
                ArrayList<ArrayList<Integer>> ipAddressRanges = new ArrayList<ArrayList<Integer>>();  
                ArrayList<String> possibleAddresses = new ArrayList<String>();
                
                for(int i = 0; i < netmaskElements.length; i++)
                {
                	ipAddressRanges.add(i, new ArrayList<Integer>());
                	int maskElement = Integer.parseInt(netmaskElements[i]);
                	if (maskElement < maxAddress)
                	{                		
                			int ipElement = Integer.parseInt(ipAddressElements[i]);
                			int startRange = (maskElement & ipElement) + 1;
                			int nrOfElements = maxAddress - maskElement;
                			for (int j = startRange; j <= startRange + nrOfElements - 1; j++ )
                			{
                				ipAddressRanges.get(i).add(j);
                			}                		              	
                	}
                	else
                	{
                		ipAddressRanges.get(i).add(Integer.parseInt(ipAddressElements[i]));
                	}
                }
                for(int i = 0; i < ipAddressRanges.get(0).size(); i++)
                {
                	for(int j = 0; j < ipAddressRanges.get(1).size(); j++)
                    {
                		for(int k = 0; k < ipAddressRanges.get(2).size(); k++)
                        {
                			for(int l = 0; l < ipAddressRanges.get(3).size(); l++)
                            {
                            	String address = String.format(Locale.getDefault(), "%d.%d.%d.%d", 
                            			ipAddressRanges.get(0).get(i), 
                            			ipAddressRanges.get(1).get(j), 
                            			ipAddressRanges.get(2).get(k), 
                            			ipAddressRanges.get(3).get(l));
                            	if (!address.equalsIgnoreCase(ipAddressString))
                            		possibleAddresses.add(address);
                            }
                        }
                    }
                }                
            }
        });
        
        saveSettingsButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	SharedPreferences settings = v.getContext().getSharedPreferences(Consts.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();                
                editor.putString(Consts.Settings_IP, editTextIp.getText().toString());

                // Commit the edits!
                editor.commit();
            }
        });
        
        return rootView;
    }      
}
