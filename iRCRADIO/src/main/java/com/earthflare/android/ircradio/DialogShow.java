package com.earthflare.android.ircradio;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogShow extends Dialog implements OnClickListener {

	private Button           okButton;
	
	
	
	private String title;
	private String message;
	
	public DialogShow(Context context, String title, String message) {
		super(context);
		
		this.title = title;
		this.message = message;
		}
	

	protected void onCreate(Bundle savedInstanceState) {
	

	
	setTitle(title);
	this.setCanceledOnTouchOutside(true);
	
	 
	setContentView(R.layout.about);
	
	
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