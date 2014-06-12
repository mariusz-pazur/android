package pl.clawdivine.homeationclient.devices;

import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class DevicesAdapter extends ArrayAdapter<RemoteDeviceInfo>
{
    private Context context;
    private int resource;
    private int textViewResourceId;

    public DevicesAdapter(Context context, int resource, int textViewResourceId)
    {
        super(context, resource, textViewResourceId);
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, null);
        }

        RemoteDeviceInfo item = getItem(position);
        if (item!= null)
        {
            TextView itemView = (TextView) view.findViewById(textViewResourceId);
            if (itemView != null)
            {
                itemView.setText(item.getTypeName() + " - " + item.getNameWithId());                
            }
        }
        return view;
    }       
    
    public void addAllFromArray(Collection<RemoteDeviceInfo> devices)
    {
    	for(RemoteDeviceInfo device : devices)
    		this.add(device);
    }
}
