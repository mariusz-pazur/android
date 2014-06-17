package pl.clawdivine.homeationclient.fragments;

import pl.clawdivine.homeationclient.*;
import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainSettingsFragment extends Fragment {
    
    private Button autoDetectButton;
    private Button saveSettingsButton;    
    private EditText editTextIp;
    private ConnectionManager connectionManager;
    private ProgressBar progress;

    public void setConnectionManager(ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        editTextIp = (EditText) rootView.findViewById(R.id.editText_ipaddress);                      
        progress = (ProgressBar) rootView.findViewById(R.id.progress_main_settings);
        progress.setVisibility(View.INVISIBLE);
        editTextIp.setText(connectionManager.getHomeAtionIpAddress());
        
        this.autoDetectButton = (Button)rootView.findViewById(R.id.button_autodetect);
        this.saveSettingsButton = (Button)rootView.findViewById(R.id.button_savesettings);               
        
        autoDetectButton.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
			public void onClick(View v)
            {            	
            	new DetectHomeAtionAddressTask().execute(connectionManager);            	            	                             
            }
        });
        
        saveSettingsButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	connectionManager.saveHomeAtionIpAddress(editTextIp.getText().toString());
            	Toast.makeText(getActivity().getApplicationContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
            }
        });
        
        return rootView;
    } 
    
    private class DetectHomeAtionAddressTask extends AsyncTask<ConnectionManager, Void, String> 
    {
    	protected void onPreExecute()
    	{
    		progress.setVisibility(View.VISIBLE);
    	}            	    

        protected void onPostExecute(String resultIP) 
        {
        	if (resultIP != "")
        		editTextIp.setText(resultIP);
        	else
        		editTextIp.setText(R.string.label_no_home_ation);
        	progress.setVisibility(View.INVISIBLE);
        }

		@Override
		protected String doInBackground(ConnectionManager... params) 
		{
			return params[0].detectHomeAtionMainIP();
		}
    }
}
