package pl.clawdivine.homeationclient;

import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;

public interface IDeviceBaseActivity extends IBaseActivity 
{
	RemoteDeviceInfo getDeviceInfo();
}
