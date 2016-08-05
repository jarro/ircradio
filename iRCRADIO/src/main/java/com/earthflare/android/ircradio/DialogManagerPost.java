package com.earthflare.android.ircradio;


import android.content.Context;

public class DialogManagerPost {

	private Context ctx;
	
	
	public DialogManagerPost(Context ctx) {
		this.ctx = ctx;	
	
	}
	
	public void promptPost () {
		
		PromptPost pp = new PromptPost(ctx);
		pp.setTitle("Message");
		pp.setContentView(R.layout.promptpost);
		pp.show();
			
	}
	
	
}
