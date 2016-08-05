package com.earthflare.android.ircradio;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DialogInfo extends Dialog implements OnClickListener {

	private Button           okButton;
	
	
	
	private String title;
	private String message;
	
	public DialogInfo(Context context, String title, String message) {
		super(context);
		
		this.title = title;
		this.message = message;
		}
	

	protected void onCreate(Bundle savedInstanceState) {
	

	this.requestWindowFeature(Window.FEATURE_LEFT_ICON);
	setTitle(title);
	this.setCanceledOnTouchOutside(true);
	
	requestWindowFeature(Window.FEATURE_LEFT_ICON); 
	setContentView(R.layout.about);
	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,android.R.drawable.ic_dialog_alert); 
	
	TextView tv = (TextView) findViewById( R.id.TextView01);
	tv.setText(message);
	okButton = (Button) findViewById(R.id.buttonok);	
	okButton.setOnClickListener(this);
	
	}

	public void onClick(View view) {
    
      switch (view.getId()) {
	  case R.id.buttonok:	  		  
	  dismiss();
	  break;
	  }
	}
	

	
}