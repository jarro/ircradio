package com.earthflare.android.ircradio;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

public class DialogManagerAccountChannel {

	private Context ctx;	
	private long accountid;
	
	
	public DialogManagerAccountChannel(Context ctx, long accountid ) {	
	  this.ctx = ctx;	

	  this.accountid = accountid;
	}
	
	public void promptAdd() {
	  PromptAddAccountChannel paa = new PromptAddAccountChannel(ctx, accountid);
	  paa.setTitle(R.string.ui_addchannel);
	  paa.setContentView(R.layout.addaccountchannelprompt);
	  paa.show();
	}
	
	public void promptAddFromList(String name) {
		  PromptAddAccountChannel paa = new PromptAddAccountChannel(ctx, accountid);
		  paa.setTitle(R.string.ui_addchannel);
		  paa.setContentView(R.layout.addaccountchannelprompt);
		  ((EditText)paa.findViewById(R.id.channel)).setText(name);
		  paa.show();
		}
	
	
	public void promptEdit(long channelid) {
    PromptEditAccountChannel paa = new PromptEditAccountChannel(ctx, channelid);    
    paa.setContentView(R.layout.addaccountchannelprompt);
    paa.show();
  }
	
	

	
}
