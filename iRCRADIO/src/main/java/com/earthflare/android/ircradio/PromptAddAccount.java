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

public class PromptAddAccount extends Dialog implements OnClickListener {

	private Button  saveButton;
	private Button  cancelButton;
  private Context ctx;
	
  private Cursor cursor;
  private SQLiteDatabase db;
	
	public PromptAddAccount(Context context, SQLiteDatabase db, Cursor cursor) {
		super(context);
    
		this.ctx = context;
		this.db = db;
		this.cursor = cursor;
		
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
    db.insert("account", null, initialValues);
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