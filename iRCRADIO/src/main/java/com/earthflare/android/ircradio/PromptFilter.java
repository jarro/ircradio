package com.earthflare.android.ircradio;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PromptFilter extends Dialog implements OnClickListener {

	private Button  saveButton;
	private Button  cancelButton;
    private ActListChannels ctx;
	
  
	
	public PromptFilter(ActListChannels context) {
		super(context);
    
		this.ctx = context;
		
		
		}
	

	protected void onCreate(Bundle savedInstanceState) {
	  
	  this.setContentView(R.layout.promptfilter);	
	  this.setTitle(R.string.ui_filterlist);	
		
      saveButton = (Button) findViewById(R.id.button1);
	  cancelButton = (Button) findViewById(R.id.button2);

	  saveButton.setOnClickListener(this);
	  cancelButton.setOnClickListener(this);
	  
	  
	}

	public void onClick(View view) {
    
	   
	  
    switch (view.getId()) {
	  case R.id.button1:
	  filter();
	  dismiss();
	  break;
	  case R.id.button2:
	  cancel();
	  break;
	  }
	}
	
 
	private void filter() {
		
		int min;
		int max;
		String filter;
		
		try {
			min = Integer.parseInt((String)( (TextView)this.findViewById(R.id.minusers) ).getText().toString());
		} catch (NumberFormatException e) {
			min = Integer.MIN_VALUE;
		}
		
		try {
			max = Integer.parseInt((String)( (TextView)this.findViewById(R.id.maxusers) ).getText().toString());
		} catch (NumberFormatException e) {
			max = Integer.MAX_VALUE;
		}
		
		filter = (String)((TextView)this.findViewById(R.id.filtertext)).getText().toString();
		
		Message msg = new Message();
	    Bundle data = new Bundle();
	    data.putString("action", "filter");
	    data.putString("filtertext", filter);
	    data.putInt("min", min);
	    data.putInt("max", max);
	    msg.setData(data);
	    ctx.myHandler.sendMessage(msg);
		
		
	}
 
	
}