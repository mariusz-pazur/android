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
	private int id;
	private int type;
	private byte state[] = new byte[4];
	
	public RemoteDeviceInfo(Parcel source) {
		this.name = source.readString();
    	this.id = source.readInt();
		this.type = source.readInt();
		this.state = new byte[4];
		source.readByteArray(this.state);		
	}

	public RemoteDeviceInfo() {		
	}

	public String getName() {		
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}	

	public byte[] getState() {
		return state;
	}

	public void setState(byte state[]) {
		this.state = state;
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
		dest.writeInt(this.id);
		dest.writeInt(this.type);		
		dest.writeByteArray(this.state);		
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
