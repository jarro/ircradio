package com.earthflare.android.ircradio;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class PromptEditAccount extends Dialog implements OnClickListener {

	private Button  saveButton;
	private Button  cancelButton;
  private Context ctx;
	
  private Cursor cursor;
  private SQLiteDatabase db;
	private long id;
  
	public PromptEditAccount(Context context, SQLiteDatabase db, Cursor cursor, long id) {
		super(context);
    
		this.ctx = context;
		this.db = db;
		this.cursor = cursor;
		this.id = id;
		
		}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  saveButton = (Button) findViewById(R.id.button1);
	  cancelButton = (Button) findViewById(R.id.button2);

	  saveButton.setOnClickListener(this);
	  cancelButton.setOnClickListener(this);
	  
	  
	    Cursor c;
	    c = db.rawQuery("select *  from account where _id = " + id, null);
	    c.moveToFirst();
	    
	    String server = c.getString(1);
	    String serverpass = c.getString(2);
	    String port = c.getString(3);	    
	    String nick = c.getString(5);
	    String auth = c.getString(6);
	    String accountname = c.getString(7);
	    String guesscharset = c.getString(10);
	    String reconnect = c.getString(11);
	    String ssl = c.getString(12);
	    
	    this.setTitle(ctx.getString(R.string.ui_edit) + " " + accountname);
	    
	    EditText etserver = (EditText)this.findViewById(R.id.server);
	    etserver.setText(server);
	    
	    EditText etserverpass = (EditText)this.findViewById(R.id.serverpass);
        etserverpass.setText(serverpass);
      
        EditText etport = (EditText)this.findViewById(R.id.port);
        etport.setText(port);
            
      
        EditText etnick = (EditText)this.findViewById(R.id.nick);
        etnick.setText(nick);
      
        EditText etauth = (EditText)this.findViewById(R.id.auth);
        etauth.setText(auth);
      
        EditText etaccountname = (EditText)this.findViewById(R.id.account);
        etaccountname.setText(accountname);
      
        CheckBox cbguesscharset = (CheckBox)this.findViewById(R.id.guesscharset);
        cbguesscharset.setChecked(Boolean.valueOf(guesscharset));
      
        CheckBox cbreconnect = (CheckBox)this.findViewById(R.id.reconnect);
        cbreconnect.setChecked(Boolean.valueOf(reconnect));
      
        CheckBox cbssl = (CheckBox)this.findViewById(R.id.ssl);
        cbssl.setChecked(Boolean.valueOf(ssl));
	
	}

	public void onClick(View view) {
	  
    switch (view.getId()) {
	  case R.id.button1:
	  add();
	  dismiss();
	  break;
	  case R.id.button2:
	  cancel();
	  break;
	  }
	}
	
  private void add () {
    
    ContentValues initialValues = createCV();
    db.update("account", initialValues, "_id = " + id, null);    
    cursor.requery();        
    
  }
  
  private ContentValues createCV () {
    
    ContentValues initialValues = new ContentValues();
    initialValues.put("server",  ( (EditText) this.findViewById(R.id.server)).getText().toString()  );
    initialValues.put("serverpass",  ( (EditText) this.findViewById(R.id.serverpass)).getText().toString()  );
    initialValues.put("port",  ( (EditText) this.findViewById(R.id.port)).getText().toString()  );    
    initialValues.put("nick",  ( (EditText) this.findViewById(R.id.nick)).getText().toString()  );
    initialValues.put("auth",  ( (EditText) this.findViewById(R.id.auth)).getText().toString()  );
    initialValues.put("accountname",  ( (EditText) this.findViewById(R.id.account)).getText().toString()  );
    initialValues.put("guesscharset",  String.valueOf(( (CheckBox) this.findViewById(R.id.guesscharset)).isChecked() ));
    initialValues.put("reconnect",  String.valueOf(( (CheckBox) this.findViewById(R.id.reconnect)).isChecked() ));
    initialValues.put("ssl",  String.valueOf(( (CheckBox) this.findViewById(R.id.ssl)).isChecked() ));
    return initialValues;
    
  }
	
}