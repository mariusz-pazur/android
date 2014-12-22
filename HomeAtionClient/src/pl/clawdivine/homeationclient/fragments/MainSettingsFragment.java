package pl.clawdivine.homeationclient.fragments;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;

import com.loopj.android.http.TextHttpResponseHandler;

import pl.clawdivine.homeationclient.*;
import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import android.annotation.SuppressLint;
import android.app.Activity;
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
    private EditText editTextTimeout;
    private ConnectionManager connectionManager;
    private ProgressBar progress;
    private IBaseActivity myActivity;    
    
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
	    myActivity = (IBaseActivity)a;
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        editTextIp = (EditText) rootView.findViewById(R.id.editText_ipaddress);
        editTextTimeout = (EditText) rootView.findViewById(R.id.editText_timeout);        
        progress = (ProgressBar) rootView.findViewById(R.id.progress_main_settings);
        progress.setVisibility(View.INVISIBLE);
        connectionManager = myActivity.getConnectionManager();
        editTextIp.setText(connectionManager.getHomeAtionIpAddress());        
        this.autoDetectButton = (Button)rootView.findViewById(R.id.button_autodetect);
        this.saveSettingsButton = (Button)rootView.findViewById(R.id.button_savesettings);               
        
        autoDetectButton.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("DefaultLocale")
			public void onClick(View v)
            {            	
            	if (!myActivity.hasToShowNoConnectionDialog())
            	{
            		//RejectedExecutionHandler implementation
            		RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
            		//Get the ThreadFactory implementation to use
            		ThreadFactory threadFactory = Executors.defaultThreadFactory();
            		//creating the ThreadPoolExecutor
            		ThreadPoolExecutor executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);            		
            		new DetectHomeAtionAddressTask().executeOnExecutor(executorPool, connectionManager);             		           	
            		new SendBroadcastRequestTask().executeOnExecutor(executorPool, connectionManager);
            	}
            }
        });
        
        saveSettingsButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	if (!myActivity.hasToShowNoConnectionDialog())
            	{
            		final String newIp = editTextIp.getText().toString();
            		progress.setVisibility(View.VISIBLE);
            		connectionManager.getEchoResponse(newIp, new TextHttpResponseHandler() 
            		{
            			@Override
            			public void onSuccess(int statusCode, Header[] headers, String responseString)
            			{            			
            				if (responseString.equalsIgnoreCase(Consts.HOME_ATION_ECHO_RESPONSE))
            				{
            					connectionManager.saveHomeAtionIpAddress(newIp);            	
            					Toast.makeText(getActivity().getApplicationContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
            				}
            				else
            					Toast.makeText(getActivity().getApplicationContext(), R.string.message_wrong_ip_address, Toast.LENGTH_LONG).show();
            				progress.setVisibility(View.INVISIBLE);
            			} 
            			@Override
            			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable exception)
            			{
            				Toast.makeText(getActivity().getApplicationContext(), R.string.message_wrong_ip_address, Toast.LENGTH_LONG).show();
            				progress.setVisibility(View.INVISIBLE);
            			}
            		});
            	}
            	
            }
        });
        
        return rootView;
    }   
    
    public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) 
        {            
        }

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
			String timeoutStr = editTextTimeout.getText().toString();
			int timeoutValue;
			try {
				timeoutValue = Integer.parseInt(timeoutStr);
			}
			catch(NumberFormatException e)
			{
				timeoutValue = Integer.parseInt(getString(R.string.default_timeout));
			}
			return params[0].receiveHomeAtionMainBroadcastResponse(timeoutValue);
		}
    }
    
    private class SendBroadcastRequestTask extends AsyncTask<ConnectionManager, Void, Boolean> 
    {
    	protected void onPreExecute()
    	{
    		progress.setVisibility(View.VISIBLE);
    	}            	    

        protected void onPostExecute(Boolean result) 
        {
        	if (!result)
        		editTextIp.setText(R.string.message_cant_send_broadcast);        	
        }

		@Override
		protected Boolean doInBackground(ConnectionManager... params) 
		{			
			return params[0].sendHomeAtionBroadcastRequest();
		}
    }
}
