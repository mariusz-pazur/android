package pl.clawdivine.homeationclient;

import java.util.Locale;

import pl.clawdivine.homeationclient.common.Consts;
import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import pl.clawdivine.homeationclient.devices.RemoteDeviceInfo;
import pl.clawdivine.homeationclient.fragments.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class RemoteDevicesControlActivity extends ActionBarActivity implements ActionBar.TabListener
{	
	private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
	private ConnectionManager connectionManager;	
	private RemoteDeviceInfo devInfo;	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_devices_control);		
				
		this.connectionManager = new ConnectionManager(this.getBaseContext());		
		Intent intent = this.getIntent();
		devInfo = intent.getParcelableExtra(Consts.REMOTE_DEVICE_PARCELABLE);					
		
		final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), devInfo.getType());

        mViewPager = (ViewPager) findViewById(R.id.pager_devices);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
	}
		
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private int deviceType;
		
        public SectionsPagerAdapter(FragmentManager fm, int deviceType) 
        {
            super(fm);
            this.deviceType = deviceType;
        }

        @Override
        public Fragment getItem(int position)
        {
        	Fragment frag = null;
        	switch (position) 
        	{
            	case 0:
            		switch(deviceType)
            		{
            			case 1:
            				RemotePowerStripControlFragment rpscFrag = new RemotePowerStripControlFragment();
            				rpscFrag.setConnectionManager(connectionManager);
            				rpscFrag.setDeviceInfo(devInfo);            				
            				frag = rpscFrag;            				
            				break;
            		}            		
            		break;
            	case 1:
            		DeviceSettingsFragment settingsFrag = new DeviceSettingsFragment();
            		settingsFrag.setDeviceInfo(devInfo);
            		frag = settingsFrag;
            		break;
        	}
        	return frag;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.device_control_section).toUpperCase(l);
                case 1:
                    return getString(R.string.device_settings_section).toUpperCase(l);
            }
            return null;
        }
    }
}
