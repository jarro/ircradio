package com.earthflare.android.ircradio;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ActDashboard extends TemplateDashboard{

	
	 public  Handler myHandler=new Handler() {

		    @Override
		    public void handleMessage(Message msg) {
		      // TODO Auto-generated method stub
		      super.handleMessage(msg);
		      
		      String action = msg.getData().getString("action"); 
		            
		      if (action == "toast") {   
		        ActDashboard.this.makeToast(msg.getData().getString("message"));
		        return;
		     }
		    }
	 }; 	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Globo.actDashboard = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		this.setContentView(R.layout.act_dashboard);
		
		initFont();
		
		GloboUtil.globalizePrefs(this);
        GloboUtil.claimaudiostream(this);
        
        Globo.handlerDashboard = myHandler;
		
	}
	
	private void initFont() {
		
		 Typeface tf = Typeface.createFromAsset(getAssets(), "ethno.ttf");
		 
		 ((TextView)findViewById(R.id.text1)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text2)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text3)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text4)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text5)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text6)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text7)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text8)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text9)).setTypeface(tf);
		 ((TextView)findViewById(R.id.text10)).setTypeface(tf);
		 
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	      Globo.actDashboard = true;	      
	      GloboUtil.claimaudiostream(this);
	      synchButtons();
	}
	

    public void synchButtons() {
    	
    	Boolean mutestate  = Globo.mutestate;        
        ToggleButton tbMute = (ToggleButton)this.findViewById(R.id.buttonmute);
        tbMute.setChecked(mutestate);
        
        
        Boolean toaststate = Globo.toaststate;        
        ToggleButton tbToast = (ToggleButton)this.findViewById(R.id.buttontoast);
        tbToast.setChecked(toaststate);
    	
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub\	
		super.onDestroy();
		
	}

    
    
}
