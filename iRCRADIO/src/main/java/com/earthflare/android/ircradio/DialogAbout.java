package com.earthflare.android.ircradio;


import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogAbout extends Dialog implements OnClickListener {

	private Button           okButton;
	
	
	private Context ctx;
	
	public DialogAbout(Context context) {
		super(context);
		this.ctx = context;
		}
	

	protected void onCreate(Bundle savedInstanceState) {
	
   	PackageManager pm = ctx.getPackageManager();	            
   	PackageInfo pi;
   	String vname = "";
	try {
		pi = pm.getPackageInfo(ctx.getPackageName(), 0);
		vname = pi.versionName;
	} catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}            
    
    

		
	setTitle(ctx.getString(R.string.app_name) );
	setContentView(R.layout.about);
	
	TextView tv = (TextView) findViewById( R.id.TextView01);
	tv.setText("Version: " + vname + " \n\n" + ctx.getString(R.string.app_clause) + " \n\n");
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