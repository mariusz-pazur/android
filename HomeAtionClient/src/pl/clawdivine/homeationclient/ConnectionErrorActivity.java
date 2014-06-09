package pl.clawdivine.homeationclient;

import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConnectionErrorActivity extends Activity {

	private Button buttonTurnWiFiOn;
	private ConnectionManager connectionManager;
	private Intent mainActivityIntent;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection_error);
		this.buttonTurnWiFiOn = (Button)this.findViewById(R.id.button_turn_wifi_on);
		
		this.mainActivityIntent = new Intent(this, MainActivity.class);
		this.connectionManager = new ConnectionManager(this.getBaseContext());
		buttonTurnWiFiOn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	connectionManager.ToggleWiFiStatus(true);
            	//przejdü do MainActivity
            	startActivity(mainActivityIntent);
            }
        });
	}
}
