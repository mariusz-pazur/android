package pl.clawdivine.homeationclient;

import java.util.Locale;
import pl.clawdivine.homeationclient.connectivity.ConnectionManager;
import pl.clawdivine.homeationclient.fragments.*;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener 
{

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    
    private ConnectionManager connectionManager;   
    private Intent connectionErrorIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
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
        this.connectionManager = new ConnectionManager(this.getBaseContext()); 
        this.connectionErrorIntent = new Intent(this, ConnectionErrorActivity.class);
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	ShowNoConnectionDialog();
    }
    
    public void ShowNoConnectionDialog()
    {
    	if (!connectionManager.isWiFiEnabled())
        {
        	NoConnectionDialogFragment noConnectionDialog = new NoConnectionDialogFragment();
        	noConnectionDialog.setConnectionErrorIntent(connectionErrorIntent);
        	noConnectionDialog.setConnectionManager(connectionManager);
        	noConnectionDialog.show(getSupportFragmentManager(), "NoWiFiConnection");    		
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
        	Fragment frag = null;
        	switch (position) 
        	{
            	case 0:
            		RemoteDevicesFragment rdFrag = new RemoteDevicesFragment();            		
            		rdFrag.setConnectionManager(connectionManager);
            		frag = rdFrag;
            		break;
            	case 1:
            		MainSettingsFragment settingsFrag = new MainSettingsFragment();
            		settingsFrag.setConnectionManager(connectionManager);
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
                    return getString(R.string.remote_devices_section).toUpperCase(l);
                case 1:
                    return getString(R.string.settings_section).toUpperCase(l);
            }
            return null;
        }
    }

    

}
