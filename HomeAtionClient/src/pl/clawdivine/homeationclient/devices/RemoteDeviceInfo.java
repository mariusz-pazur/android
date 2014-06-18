package pl.clawdivine.homeationclient.devices;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class RemoteDeviceInfo implements Parcelable
{
	private String name;
	private byte id;
	private byte type;
	private byte state[] = new byte[4];
	private String stateNames[] = new String[] {"State1", "State2", "State3", "State4"}; 
	
	public RemoteDeviceInfo(Parcel source) {
		this.name = source.readString();
    	this.id = source.readByte();
		this.type = source.readByte();
		this.state = new byte[4];		
		source.readByteArray(this.state);
		this.stateNames = new String[4];
		source.readStringArray(this.stateNames);
	}

	public RemoteDeviceInfo() {		
	}
	
	public String getNameWithId()
	{
		return getName() + " (Id: " + getId() + ")";
	}

	public String[] getStateNames() {		
		return stateNames;
	}
	
	public void setStateNames(String[] names) {
		this.stateNames = names;
	}
	
	public String getName() {		
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public byte getId() {
		return id;
	}

	public void setId(byte id) {
		this.id = id;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}	

	public byte[] getState() {
		return state;
	}

	public void setState(byte state[]) {
		this.state = state;
	}
	
	public String getTypeName()
	{
		if (type == (byte)1)
			return "Power strip";
		else
			return "";
	}
	
	public static List<RemoteDeviceInfo> getListFromJsonArray(JsonArray arr, Gson gson)
	{
		List<RemoteDeviceInfo> devices = new ArrayList<RemoteDeviceInfo>();
		for (JsonElement jsonElement : arr)
        {
        	RemoteDeviceInfo dev = gson.fromJson(jsonElement, RemoteDeviceInfo.class);
            devices.add(dev);            
        }
		
		return devices;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);		
		dest.writeByte(this.id);
		dest.writeByte(this.type);		
		dest.writeByteArray(this.state);
		dest.writeStringArray(this.stateNames);
	}
	
	public static final Parcelable.Creator<RemoteDeviceInfo> CREATOR = new Parcelable.Creator<RemoteDeviceInfo>() {
		public RemoteDeviceInfo createFromParcel(Parcel in) {
			return new RemoteDeviceInfo(in);
		}

		public RemoteDeviceInfo[] newArray(int size) {
			return new RemoteDeviceInfo[size];
		}
	};
}
