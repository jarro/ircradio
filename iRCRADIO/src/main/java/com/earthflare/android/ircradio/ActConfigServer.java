package com.earthflare.android.ircradio;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.TabActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;

public class ActConfigServer extends TabActivity implements OnClickListener{

	private Button  saveButton;
	private Button  cancelButton;	
	private TabHost mTabHost;	
	private String action;
	private long accountid;
	private Map<String,Integer> charsetMap;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.configserver);
		GloboUtil.claimaudiostream(this);
				
		configureTabHost();
        configureSpinners();

		initButtons();
		
		
		action = this.getIntent().getStringExtra("action");
		if (action.equals("edit")){
			accountid = this.getIntent().getLongExtra("accountid", 0);
			populateFields(accountid);
		}else{
			action = "add";
			setSpinnerDefault();	
		}
		
		
		
	}

	    
    private void setSpinnerDefault() {

    	Spinner spinnerReceive = (Spinner)this.findViewById(R.id.encodingreceive);
		Spinner spinnerSend = (Spinner)this.findViewById(R.id.encodingsend);
		Spinner spinnerServer = (Spinner)this.findViewById(R.id.encodingserver);

		Integer utf8 = charsetMap.get("UTF-8");
		
		spinnerReceive.setSelection(utf8);
		spinnerSend.setSelection(utf8);
		spinnerServer.setSelection(utf8);
    }

    
	private void configureSpinners() {

		Spinner spinnerReceive = (Spinner)this.findViewById(R.id.encodingreceive);
		Spinner spinnerSend = (Spinner)this.findViewById(R.id.encodingsend);
		Spinner spinnerServer = (Spinner)this.findViewById(R.id.encodingserver);
		
		ArrayAdapter<String> adapterSpinnerReceive = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapterSpinnerReceive.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerReceive.setAdapter(adapterSpinnerReceive);
		
		ArrayAdapter<String> adapterSpinnerSend = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapterSpinnerSend.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSend.setAdapter(adapterSpinnerSend);
		
		ArrayAdapter<String> adapterSpinnerServer = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapterSpinnerServer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerServer.setAdapter(adapterSpinnerServer);
		
		
		//set spinner values
		Set<String> charsets = Charset.availableCharsets().keySet();
		
		
		Integer counter = 0;
		charsetMap = new HashMap<String,Integer>();
		for (String key : charsets ){
			
			charsetMap.put(key, counter);
			counter ++;
			adapterSpinnerSend.add(key);
			adapterSpinnerReceive.add(key);
			adapterSpinnerServer.add(key);
		}
		
		
	}
	    
	    
    private void configureTabHost() {
    	mTabHost = getTabHost();		    
		mTabHost.addTab(mTabHost.newTabSpec("tabs1").setIndicator("S").setContent(R.id.tab1));
		mTabHost.addTab(mTabHost.newTabSpec("tabs2").setIndicator("I").setContent(R.id.tab2));
		mTabHost.addTab(mTabHost.newTabSpec("tabs3").setIndicator("P").setContent(R.id.tab3));
		mTabHost.addTab(mTabHost.newTabSpec("tabs4").setIndicator("E").setContent(R.id.tab4));
		mTabHost.addTab(mTabHost.newTabSpec("tabs5").setIndicator("R").setContent(R.id.tab5));
		mTabHost.setCurrentTab(0);

		//set height
		mTabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
		mTabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;
		mTabHost.getTabWidget().getChildAt(2).getLayoutParams().height = 40;
		mTabHost.getTabWidget().getChildAt(3).getLayoutParams().height = 40;
		mTabHost.getTabWidget().getChildAt(4).getLayoutParams().height = 40;				
           
    }
	    
	private void populateFields(long id) {
		
		Cursor c;
	    c = Globo.db.rawQuery("select *  from account where _id = " + id, null);
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
	    String autoidentify = c.getString(13);
	    String nickserv = c.getString(14);
	    String nickpass = c.getString(15);
	    String perform = c.getString(16);
	    String encodingoverride = c.getString(17);
	    String encodingsend = c.getString(18);
	    String encodingreceive = c.getString(19);
	    String encodingserver = c.getString(20);
	    String reconnectinterval = c.getString(21);
	    String reconnectretries = c.getString(22);
	    
	    this.setTitle(this.getString(R.string.ui_edit) + " " + accountname);
	    
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
           
        CheckBox cbssl = (CheckBox)this.findViewById(R.id.ssl);
        cbssl.setChecked(Boolean.valueOf(ssl));
		               
        CheckBox cbreconnect = (CheckBox)this.findViewById(R.id.reconnect);
        cbreconnect.setChecked(Boolean.valueOf(reconnect));
        
        CheckBox cbautoidentify = (CheckBox)this.findViewById(R.id.autoidentify);
        cbautoidentify.setChecked(Boolean.valueOf(autoidentify));
        
        EditText etnickserv = (EditText)this.findViewById(R.id.nickserv);
        etnickserv.setText(nickserv);
        
        EditText etnickpass = (EditText)this.findViewById(R.id.nickpass);
        etnickpass.setText(nickpass);
        
        EditText etperform = (EditText)this.findViewById(R.id.perform);
        etperform.setText(perform);
        
        CheckBox cbencodingoverride = (CheckBox)this.findViewById(R.id.encodingoverride);
        cbencodingoverride.setChecked(Boolean.valueOf(encodingoverride));
        
        Spinner spencodingsend = (Spinner)this.findViewById(R.id.encodingsend);
        if(charsetMap.containsKey(encodingsend)) {
          spencodingsend.setSelection(charsetMap.get(encodingsend));
        }else{
          spencodingsend.setSelection(charsetMap.get("UTF-8"));
        }
                
        Spinner spencodingreceive = (Spinner)this.findViewById(R.id.encodingreceive);
        if(charsetMap.containsKey(encodingreceive)) {
          spencodingreceive.setSelection(charsetMap.get(encodingreceive));
        }else{
          spencodingreceive.setSelection(charsetMap.get("UTF-8"));
        }
        
        Spinner spencodingserver = (Spinner)this.findViewById(R.id.encodingserver);
        if(charsetMap.containsKey(encodingserver)) {
          spencodingserver.setSelection(charsetMap.get(encodingserver));
        }else{
          spencodingserver.setSelection(charsetMap.get("UTF-8"));
        }
        
        EditText etreconnectinterval = (EditText)this.findViewById(R.id.reconnectinterval);
        etreconnectinterval.setText(reconnectinterval);
        
        EditText etreconnecttries = (EditText)this.findViewById(R.id.reconnectretries);
        etreconnecttries.setText(reconnectretries);
        
        c.close();
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
		    initialValues.put("autoidentify",  String.valueOf(( (CheckBox) this.findViewById(R.id.autoidentify)).isChecked() ));
		    initialValues.put("nickserv",  ( (EditText) this.findViewById(R.id.nickserv)).getText().toString()  );
		    initialValues.put("nickpass",  ( (EditText) this.findViewById(R.id.nickpass)).getText().toString()  );
		    initialValues.put("perform",  ( (EditText) this.findViewById(R.id.perform)).getText().toString()  );
		    initialValues.put("encodingoverride",  String.valueOf(( (CheckBox) this.findViewById(R.id.encodingoverride)).isChecked() ));
		    initialValues.put("encodingsend",  ( (Spinner) this.findViewById(R.id.encodingsend)).getSelectedItem().toString()  );
		    initialValues.put("encodingreceive",  ( (Spinner) this.findViewById(R.id.encodingreceive)).getSelectedItem().toString()  );
		    initialValues.put("encodingserver",  ( (Spinner) this.findViewById(R.id.encodingserver)).getSelectedItem().toString()  );
		    initialValues.put("reconnectinterval",  ( (EditText) this.findViewById(R.id.reconnectinterval)).getText().toString()  );
		    initialValues.put("reconnectretries",  ( (EditText) this.findViewById(R.id.reconnectretries)).getText().toString()  );
		    
		    return initialValues;
		    
	 }
	 
	 private void saveEdit () {
		    
		    ContentValues initialValues = createCV();
		    Globo.db.update("account", initialValues, "_id = " + accountid, null);    
		    
		  }
	 
	 private void saveAdd () {
		    
		    ContentValues initialValues = createCV();
		    Globo.db.insert("account", null, initialValues);
		    
     }
	
	
	private void initButtons () {
		
		  saveButton = (Button) findViewById(R.id.button1);
		  saveButton.setOnClickListener(this);
		  
		  cancelButton = (Button) findViewById(R.id.button2);
		  cancelButton.setOnClickListener(this);
		
	}
	
	
	
	public void onClick(View view) {
		  
	    switch (view.getId()) {
		  case R.id.button1:
		  if (action.equals("edit")){
			  saveEdit();
		  }else{
			  saveAdd();
		  }
		  finish();
		  break;
		  case R.id.button2:
		  finish();
		  break;
	   }
	}


	

	
	
	
}
