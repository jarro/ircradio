package com.earthflare.android.ircradio;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class ActMessanger extends ActionBarActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//CycleLoc.onCreate(this);
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//CycleLoc.onPause();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		//CycleLoc.onDestroy();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();		
		
	}
		
	
	
	 
		
	 public void makeToast(String message) {
	       
		    
		    try {
				Globo.toast.cancel();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		       
			   Globo.toast = Toast.makeText(this, message,  Toast.LENGTH_LONG);	   
			   Globo.toast.show();
			  
		       //Toast.makeText(this, message,  Toast.LENGTH_LONG).show();
    }

	
}
