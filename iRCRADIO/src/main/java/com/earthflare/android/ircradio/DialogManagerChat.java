package com.earthflare.android.ircradio;


import android.content.Context;

public class DialogManagerChat {

	private Context ctx;
	
	
	public DialogManagerChat(Context ctx) {
		this.ctx = ctx;	
	
	}
	
	public void promptUser () {
		
		PromptUser pu = new PromptUser(ctx);
		pu.setContentView(R.layout.promptuser);
		pu.show();
			
	}
	
	
	public static void showTopic(Context ctx) {
		
		DialogTopic dt = new DialogTopic(ctx);
		dt.show();
		
	}
	
}
