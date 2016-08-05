package com.earthflare.android.ircradio;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

public class GloboUtil {

	 public static void globalizePrefs(Context ctx) {
	        
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
	        Globo.authorSpeech = preferences.getBoolean("pref_saynick", true);
	        Globo.globalToast = preferences.getBoolean("pref_globaltoast", false);
	        Globo.pref_replacechat = preferences.getBoolean("pref_replacechat", true);
	        Globo.pref_mutemusic = preferences.getBoolean("pref_mutemusic", false);
	        Globo.pref_splitstream = preferences.getBoolean("pref_splitstream", false);
	      
	        Globo.mutestate = preferences.getBoolean("pref_mutestate", false);
	        Globo.toaststate = preferences.getBoolean("pref_toaststate", false);
	        Globo.pref_headphonemute = preferences.getBoolean("pref_headphonemute", false);
	        
	        Globo.pref_chan_showactnav =  preferences.getBoolean("pref_chan_showactnav", true);
	        Globo.pref_chan_showchannav =  preferences.getBoolean("pref_chan_showchannav", true);
	        
 	        	        
	        //notifications
	        Globo.pref_notification_channelmessage = preferences.getBoolean("pref_notificationchannelmessage", false);
	        Globo.pref_notification_newprivatemessage = preferences.getBoolean("pref_notificationnewprivatemessage", false);
	        
	        
	        //tts speak prefs
	        Globo.ttsspeak_action = preferences.getBoolean("pref_ttsspeak_action", true);
	        Globo.ttsspeak_invite = preferences.getBoolean("pref_ttsspeak_invite", true);
	        Globo.ttsspeak_join = preferences.getBoolean("pref_ttsspeak_join", true);
	        Globo.ttsspeak_kick = preferences.getBoolean("pref_ttsspeak_kick", true);	        	        
	        Globo.ttsspeak_message = preferences.getBoolean("pref_ttsspeak_message", true);
	        Globo.ttsspeak_mode = preferences.getBoolean("pref_ttsspeak_mode", true);
	        Globo.ttsspeak_notice = preferences.getBoolean("pref_ttsspeak_notice", true);
	        Globo.ttsspeak_part = preferences.getBoolean("pref_ttsspeak_part", true);
	        Globo.ttsspeak_privatemessage = preferences.getBoolean("pref_ttsspeak_privatemessage", true);
	        Globo.ttsspeak_privatenotice = preferences.getBoolean("pref_ttsspeak_privatenotice", true);
	        Globo.ttsspeak_quit = preferences.getBoolean("pref_ttsspeak_quit", true);
	        try {
				Globo.pref_pitch = Float.parseFloat(preferences.getString("pref_pitch", "1"));
			} catch (NumberFormatException e) {			
				Globo.pref_pitch = 1;
			}
	        
	        
	        try {
				Globo.pref_speed = Float.parseFloat(preferences.getString("pref_speed", "1"));
			} catch (NumberFormatException e) {			
				Globo.pref_speed = 1;
			}
			
			
			try {
				Globo.pref_call_volume = Integer.parseInt(preferences.getString("pref_call_volume", "3"));
			} catch (NumberFormatException e) {	
				Globo.pref_call_volume = 3;
			}
			
	     }
	
	 public static void globalizeChannelPrefs(Context ctx) {
		 
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
	     Globo.pref_chan_timestamp = preferences.getBoolean("pref_chan_timestamp", false);
		 
	     Globo.pref_chan_join = preferences.getBoolean("pref_chan_join", true);
	     Globo.pref_chan_part = preferences.getBoolean("pref_chan_part", true);
	     Globo.pref_chan_kick = preferences.getBoolean("pref_chan_kick", true);
	     Globo.pref_chan_quit = preferences.getBoolean("pref_chan_quit", true);
	     Globo.pref_chan_mode = preferences.getBoolean("pref_chan_mode", true);
	     Globo.pref_chan_invite = preferences.getBoolean("pref_chan_invite", true);
	     Globo.pref_chan_message = preferences.getBoolean("pref_chan_message", true);
	     
	 }
	 
	 public static void claimaudiostream(Activity act)  {	 
		   act.setVolumeControlStream(AudioManager.STREAM_MUSIC);		 
	 }
	 
	 public static void setBoolean(Context ctx, String pref, boolean val) {
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		 preferences.edit().putBoolean(pref, val).apply();
	 }
	
	 public static void abandonAudioFocus(){
         try {
             Globo.AM.abandonAudioFocus(Globo.audioFocusChangeListener);
         }catch(Exception e){

         }
     }
}
