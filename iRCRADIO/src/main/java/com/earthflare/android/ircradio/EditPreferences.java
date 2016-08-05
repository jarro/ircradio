package com.earthflare.android.ircradio;

  
   

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.cratorsoft.android.ttslanguage.TTSPipeline;

public class EditPreferences extends PreferenceActivity {  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
          
        addPreferencesFromResource(R.xml.preferences);  
    }  
    
    @Override
    protected void onPause() {
      // TODO Auto-generated method stub
      super.onPause();
      
      
      
        new Thread(new Runnable() {
    	    public void run() {
    	
    	    	GloboUtil.globalizePrefs(EditPreferences.this);
    	            	        
    	        //update speed and pitch      
    	        try {
    	  		  TTSPipeline.getInstance().setPitch(Globo.pref_pitch);
                  TTSPipeline.getInstance().setSpeechRate(Globo.pref_speed);
    	  	  } catch (Exception e) {
    	  		// TODO Auto-generated catch block
    	  		
    	  	  }
    	    	
    	    	
    	    }
    	  }).start();
    	
      
      
      
      
    }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Globo.AM = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
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
		GloboUtil.claimaudiostream(this);
	}  
    
    
    
    
} 