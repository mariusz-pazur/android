package pl.clawdivine.homeationclient.fragments;

import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import pl.clawdivine.homeationclient.*;

public class NoConnectionDialogFragment extends DialogFragment {
	
	private ConnectionManager connectionManager;
	private Intent connectionErrorIntent;	
	private IBaseActivity myActivity;	
			
	public NoConnectionDialogFragment()
	{				
	}	
	
	public void setConnectionErrorIntent(Intent connectionErrorIntent)
	{
		this.connectionErrorIntent = connectionErrorIntent;
	}
	
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		//poka� message box: czy chcesz uruchomi� WiFi  	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	connectionManager = myActivity.getConnectionManager();
    	connectionErrorIntent = new Intent(getActivity(), ConnectionErrorActivity.class);
    	builder.setMessage(R.string.wifi_dialog_message).setTitle(R.string.wifi_dialog_title);
    	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                connectionManager.ToggleWiFiStatus(true);
            }
        });
    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	startActivity(connectionErrorIntent);
            }
        });
    	builder.setOnKeyListener(new OnKeyListener()
    	{           
			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
				if (keyCode == KeyEvent.KEYCODE_BACK)
					return true;
				return false;
			}
    });
    	// 3. Get the AlertDialog from create()
    	return builder.create();
	}

}
