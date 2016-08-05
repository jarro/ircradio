package com.earthflare.android.ircradio;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;



	
	public class WifiLocker{
	    
	    private PowerManager.WakeLock mWakeLock = null;
	    private WifiManager.WifiLock mWifiLock = null;
        private boolean haveWifi = false;
	    
	    public WifiLocker(Context context){
	        
	       
	    	ConnectivityManager connec =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        
	        if (  connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() ){	        	
	          haveWifi = true;
	        }else{
	          haveWifi = false;
	        }
	    	
	    	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	        mWifiLock = wifiManager.createWifiLock("ircradio");
	        mWifiLock.setReferenceCounted(false);	        	

	        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ircradio");
	        mWakeLock.setReferenceCounted(false);

	    }

	    public void acquire(){
	        // Acquire wake lock
	        mWakeLock.acquire();
	        // Acquire wifi lock
	        if (haveWifi) {
	        	mWifiLock.acquire();
	        }
	    }
	    
	    public void release(){
	        // Release wifi lock
	    	if (mWifiLock.isHeld() ) mWifiLock.release();	    	
	        // Release wake lock
	        if (mWakeLock.isHeld()) mWakeLock.release();
	    }
	}
	
	

