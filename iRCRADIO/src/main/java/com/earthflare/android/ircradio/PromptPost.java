package com.earthflare.android.ircradio;

import android.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PromptPost extends Dialog implements OnClickListener {

	private Button  saveButton;
	private Button  cancelButton;
  private Context ctx;
	

	
	public PromptPost(Context context) {
		super(context);
    
		this.ctx = context;
		
		}
	

	protected void onCreate(Bundle savedInstanceState) {
	  saveButton = (Button) findViewById(R.id.button1);
	  cancelButton = (Button) findViewById(R.id.button2);

	  saveButton.setOnClickListener(this);
	  cancelButton.setOnClickListener(this);
	}

	public void onClick(View view) {
    
	   
	  
      switch (view.getId()) {
	  case R.id.button1:
	  ActAccountList mainact = (ActAccountList)ctx;
	  EditText text = (EditText) this.findViewById(R.id.text1);
	  String message = text.getText().toString();
	  mainact.postMessage(message);
	  dismiss();
	  break;
	  case R.id.button2:
	  cancel();
	  break;
	  }
	}
	
  
	
}