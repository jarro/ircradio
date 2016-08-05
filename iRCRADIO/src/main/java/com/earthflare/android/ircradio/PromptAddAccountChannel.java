package com.earthflare.android.ircradio;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class PromptAddAccountChannel extends Dialog implements OnClickListener {

	private Button saveButton;
	private Button cancelButton;
	private Context ctx;

	private long accountid;

	public PromptAddAccountChannel(Context context, long accountid) {
		super(context);

		this.ctx = context;
		this.accountid = accountid;

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

	private void add() {

		ContentValues initialValues = createCV();
		Globo.db.insert("channel", null, initialValues);
				

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
		initialValues.put("accountid", accountid);
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