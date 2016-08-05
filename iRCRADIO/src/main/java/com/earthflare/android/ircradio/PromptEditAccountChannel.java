package com.earthflare.android.ircradio;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class PromptEditAccountChannel extends Dialog implements OnClickListener {

	private Button saveButton;
	private Button cancelButton;
	private Context ctx;

	
	private long channelid;

	public PromptEditAccountChannel(Context context, long channelid) {
		super(context);

		this.ctx = context;
		this.channelid = channelid;

	}

	protected void onCreate(Bundle savedInstanceState) {
		saveButton = (Button) findViewById(R.id.button1);
		cancelButton = (Button) findViewById(R.id.button2);

		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

		Cursor c;
		c = Globo.db.rawQuery("select *  from channel where _id = " + channelid, null);
		c.moveToFirst();

		String joinleave = c.getString(4);
		String channel = c.getString(2);
		String channelpass = c.getString(3);
		String autojoin = c.getString(5);
		String language = c.getString(6);

		this.setTitle(ctx.getString(R.string.ui_edit) + " " + channel);

		CheckBox etjoinleave = (CheckBox) this.findViewById(R.id.joinleave);
		etjoinleave.setChecked(Boolean.valueOf(joinleave));

		EditText etchannel = (EditText) this.findViewById(R.id.channel);
		etchannel.setText(channel);

		EditText etchannelpass = (EditText) this.findViewById(R.id.channelpass);
		etchannelpass.setText(channelpass);

		CheckBox etautojoin = (CheckBox) this.findViewById(R.id.autojoin);
		etautojoin.setChecked(Boolean.valueOf(autojoin));

		Spinner splanguage = (Spinner) this.findViewById(R.id.language);
		splanguage.setSelection((int) Globo.languagecodesreverse.get(language));

		c.close();
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

	private void add() {

		ContentValues initialValues = createCV();
		Globo.db.update("channel", initialValues, "_id = " + channelid, null);
		
		if (Globo.actAccountListVisible) {  		 
  		    Bundle data = new Bundle();
  	        data.putString("action", "requery");	        
  	        Message msg = new Message();
  	        msg.setData(data);
  	        Globo.handlerAccountList.sendMessage(msg);		  
  	  	}

	}

	private ContentValues createCV() {

		ContentValues initialValues = new ContentValues();
		initialValues.put("joinleave", String.valueOf(((CheckBox) this
				.findViewById(R.id.joinleave)).isChecked()));
		initialValues.put("channel", ((EditText) this
				.findViewById(R.id.channel)).getText().toString());
		initialValues.put("channelpass", ((EditText) this
				.findViewById(R.id.channelpass)).getText().toString());
		initialValues.put("autojoin", String.valueOf(((CheckBox) this
				.findViewById(R.id.autojoin)).isChecked()));
		initialValues.put("language", Globo.languagecodes.get(((Spinner) this
				.findViewById(R.id.language)).getSelectedItemPosition()));

		return initialValues;

	}

}