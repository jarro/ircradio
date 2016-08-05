package com.earthflare.android.ircradio;

  
   

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
  
public class EditChannelPreferences extends PreferenceActivity {  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
          
        addPreferencesFromResource(R.xml.channelpreferences);  
    }  
    
    @Override
    protected void onPause() {
      super.onPause();

        GloboUtil.globalizeChannelPrefs(this);
      
    }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, IrcRadioService.class);
        intent.putExtra("action", "setvolume");
        this.startService(intent); 
		
		super.onStop();
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}  
    
    
    
    
} 