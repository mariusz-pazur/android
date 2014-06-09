package pl.clawdivine.homeationclient;

import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NoConnectionDialogFragment extends DialogFragment {
	
	private ConnectionManager connectionManager;
	private Intent connectionErrorIntent;	
	
	public NoConnectionDialogFragment()
	{				
	}
	
	public void setConnectionManager(ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}
	
	public void setConnectionErrorIntent(Intent connectionErrorIntent)
	{
		this.connectionErrorIntent = connectionErrorIntent;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		//poka� message box: czy chcesz uruchomi� WiFi  	
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	
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
    	// 3. Get the AlertDialog from create()
    	return builder.create();
	}

}