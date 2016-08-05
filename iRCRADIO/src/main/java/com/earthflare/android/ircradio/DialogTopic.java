package com.earthflare.android.ircradio;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogTopic extends Dialog implements OnClickListener {

	private Button           okButton;
	
	
	private Context ctx;
	
	public DialogTopic(Context context) {
		super(context);
		this.ctx = context;
		}
	

	protected void onCreate(Bundle savedInstanceState) {
	
            
    
    

		
	setTitle(ctx.getString(R.string.ui_topic) + Globo.chatlogActChannelName);
	setContentView(R.layout.about);
	
	TextView tv = (TextView) findViewById( R.id.TextView01);
	
	
	SpannableString topic = new SpannableString(ctx.getString(R.string.ui_couldnotretrievetopic));
	if (Globo.botManager.ircBots.containsKey(Globo.chatlogActNetworkName) ) {
		
		
		
		if (Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).channelMap.containsKey(Globo.chatlogActChannelName)) {
			
		
			
			if (Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).channelMap.get(Globo.chatlogActChannelName).containsKey("topic")) {
				
		
				topic = new SpannableString (Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).channelMap.get(Globo.chatlogActChannelName).get("topic"));
				
			}
		    
			
		}
		
	}
	android.text.util.Linkify.addLinks(topic, android.text.util.Linkify.ALL);
	tv.setText(topic);
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