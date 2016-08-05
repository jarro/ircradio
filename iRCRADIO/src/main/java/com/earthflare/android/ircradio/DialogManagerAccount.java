package com.earthflare.android.ircradio;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DialogManagerAccount {

	private Context ctx;
	private SQLiteDatabase db;
	private Cursor accountcursor;
	
	
	public DialogManagerAccount(Context ctx, SQLiteDatabase db, Cursor accountcursor) {	
	  this.ctx = ctx;	
		this.db = db;
		this.accountcursor = accountcursor;
	}
	
	public void promptAdd() {
	  PromptAddAccount paa = new PromptAddAccount(ctx, db, accountcursor);
	  paa.setTitle(R.string.ui_addaccount);
	  paa.setContentView(R.layout.addaccountprompt);
	  paa.show();
	}
	
	
	public void promptEdit(long id) {
    PromptEditAccount paa = new PromptEditAccount(ctx, db, accountcursor, id);    
    paa.setContentView(R.layout.addaccountprompt);
    paa.show();
  }
	
	

	
}
