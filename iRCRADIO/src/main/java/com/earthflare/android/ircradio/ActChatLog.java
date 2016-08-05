package com.earthflare.android.ircradio;


import java.util.concurrent.atomic.AtomicBoolean;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.earthflare.android.cross.CrossView;
import com.earthflare.android.cross.OnCrossListener;
import com.earthflare.android.logmanager.LogEntry;
import com.earthflare.android.logmanager.LogManager;
import com.earthflare.android.logmanager.ServerLog;



public class ActChatLog extends TemplateChat implements  OnCrossListener{

	
    private ListChatAdapter adapter;
    protected ListView listview;
    
	private LinearLayout container;
	private EditText et;
	
	private NavBarListener listener;
	
	
	private long uid;
	private MenuData menuData; 
	
	
	
	
	
	
	@Override protected void onCreate(Bundle bundle) {
		  super.onCreate(bundle);
		  
		  
		  
		  // window features - must be set prior to calling setContentView...	
		  		  
		  setContentView(R.layout.chat);
	      	  
		  initDrawer();
	      	  
		  listview = (ListView)this.findViewById(R.id.ListViewServerLog);
		  
		  
		  
		  Globo.handlerChatLog = myHandler;
		  
		  //attempt to go to passed message
		  
		  Intent intent = this.getIntent();
		  
		  

		  killNotification(intent);
		  
		    uid = intent.getLongExtra("uid",-1);		    
			Globo.chatlogActChannelName = intent.getStringExtra("channel");
			Globo.chatlogActNetworkName = intent.getLongExtra("network",0);
			Globo.chatlogActType = intent.getStringExtra("acttype");
			Globo.chatlogActAccountName = intent.getStringExtra("accountname");
		  
		 
		  
	}
	
	
	
	
	protected void killNotification(Intent intent) {
		
		boolean linkedfromstatus = intent.getBooleanExtra("notificationlink", false);
		  if(linkedfromstatus){
			  NotificationManager nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
			  nm.cancel(2);
		  }
		
		
	}
	
	
    @Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		
		
		uid = intent.getLongExtra("uid",-1);		  
		killNotification(intent);

		NavItem ni = new NavItem(intent.getLongExtra("network",0),intent.getStringExtra("accountname"),intent.getStringExtra("channel"),intent.getStringExtra("acttype"));
		
		this.changeLog(ni);
		scrollToUid();
	}



	public void initializeCrossing() {  	
    	
    	CrossView cross = (CrossView) this.findViewById(R.id.crossview);
		cross.addOnCrossListener(this);
		
        LinearLayout navChanll;
        
		
		navChanll = (LinearLayout)this.findViewById(R.id.navchanll);
		
		
				
		if (Globo.chatlogChanNav.get()) {	
			navChanll.setVisibility(View.VISIBLE);
			
			this.refreshNavBar();
		}else{		
			navChanll.setVisibility(View.GONE);
					
		}
		

		if (Globo.chatlogActNav.get()) {
			getSupportActionBar().show();
		
			
		}else{
			getSupportActionBar().hide();
		
			
		}
		
		
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		Globo.actChatlogVisisble = false;
		
	}


	private void setTitle() {
		
		SpannableString ss;
		
		boolean isChannelVoice;
		boolean isServerVoice;
		
		isChannelVoice = (Globo.voiceServer == Globo.chatlogActNetworkName && Globo.voiceChannel.equals(Globo.chatlogActChannelName) && Globo.voiceType.equals("channel") );
		isServerVoice = (Globo.voiceServer == Globo.chatlogActNetworkName && Globo.voiceType.equals("server"));
		
		int servercolor = this.getResources().getColor(Globo.botManager.getStatusColorServer(Globo.chatlogActNetworkName, false));
		int appnamelength =0;
		
		if ( Globo.chatlogActType.equals("server")) {			
		    ss = new SpannableString ( Globo.chatlogActAccountName);
		    Globo.botManager.getStatusColorServer(Globo.chatlogActNetworkName, false);
		    ss.setSpan(new ForegroundColorSpan(servercolor), appnamelength,  ss.length() , 0);		    
		    if (isServerVoice) {
		    	ss.setSpan(new StyleSpan(Typeface.ITALIC), appnamelength, ss.length(), 0);
		    }
		    
		}else{			
			int channelcolor = this.getResources().getColor(Globo.botManager.getStatusColorChannel(Globo.chatlogActNetworkName, Globo.chatlogActChannelName, false));
			ss = new SpannableString ( Globo.chatlogActAccountName + " " + Globo.chatlogActChannelName);
			ss.setSpan(new ForegroundColorSpan(servercolor), appnamelength,  appnamelength + Globo.chatlogActAccountName.length() , 0);
			ss.setSpan(new ForegroundColorSpan(channelcolor), appnamelength + Globo.chatlogActAccountName.length() , ss.length() , 0);
			if (isServerVoice) {
		    	ss.setSpan(new StyleSpan(Typeface.ITALIC), appnamelength, appnamelength + Globo.chatlogActAccountName.length(), 0);
		    }else if(isChannelVoice) {
		    	ss.setSpan(new StyleSpan(Typeface.ITALIC), appnamelength + Globo.chatlogActAccountName.length(), ss.length(), 0);
		    }
		}
		
	    this.setTitle(ss);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
	
		super.onResume();
		
		
		GloboUtil.globalizeChannelPrefs(this);
		GloboUtil.claimaudiostream(this);

		if (Globo.chatlogChanNav == null) {
			Globo.chatlogChanNav = new AtomicBoolean();
			Globo.chatlogChanNav.set(Globo.pref_chan_showchannav);
		}
		
		if (Globo.chatlogActNav == null)  {
			Globo.chatlogActNav = new AtomicBoolean();
			Globo.chatlogActNav.set(Globo.pref_chan_showactnav);
		}
		
		//mark read and update notification
		synchronized(LogManager.serverLogMap) {
		  updateChecked();
		}
		
		listener = new NavBarListener();
		initialize();
		initializeCrossing();
		setTitle();
		
		listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		
		scrollToUid();
		
		//if from notice go to position else go to end
		
	}
	
	
	protected void scrollToUid() {
		
		if (uid > -1) {  
		    int position = adapter.getPositionByUid(uid);
		    
			listview.post(new Runnable(){ public void run() 
			{ listview.setSelectionFromTop(adapter.getPositionByUid(uid), 10);
			  uid=-1;
			  ActChatLog.this.getIntent().putExtra("uid", -1); }});		  
		}else{
		    listview.post(new Runnable(){ public void run() { 
		        
		    	listview.setSelection(adapter.getCount()-1); 		    
		    }});
		}
		
	}

    protected void updateChecked() {
    	
    	if (LogManager.serverLogMap.containsKey(Globo.chatlogActNetworkName)) {
    		
    		ServerLog sl = LogManager.serverLogMap.get(Globo.chatlogActNetworkName);
    		if (Globo.chatlogActType.equals("server")) {
    			sl.checked = true; 
    		}else{
    			if (sl.channelLogMap.containsKey(Globo.chatlogActChannelName)) {
    				 if (!sl.channelLogMap.get(Globo.chatlogActChannelName).checked) {
    					 //mark checked
    					 sl.channelLogMap.get(Globo.chatlogActChannelName).checked = true;
    					 
    					 if (!Globo.chatlogActChannelName.startsWith("#")) {
    						 //refresh notification
    						 Intent serviceIconUpdate = new Intent(this, IrcRadioService.class);
    					     serviceIconUpdate.putExtra("action", "updateicon");
    					     this.startService(serviceIconUpdate);
    					 }
    				 }
    			}
    		}
  	}
    }

	public void selectUser () {
	      
	      DialogManagerChat dmc = new DialogManagerChat(this);
	      dmc.promptUser();
	      
	}

	synchronized private void initialize() {
		
	
		listview = (ListView)this.findViewById(R.id.ListViewServerLog);
		//font style  mono for server log.
		Typeface typeface = Typeface.DEFAULT;
		if (Globo.chatlogActType.equals("server") ) {
			typeface = Typeface.MONOSPACE;
		}
		
		adapter = new ListChatAdapter(this, typeface, Globo.chatlogActAccountName  );
		listview.setAdapter(adapter);
		this.registerForContextMenu(listview);
		Globo.actChatlogVisisble = true;
		
		
		
		//initialize textbox
		et = (EditText)this.findViewById(R.id.textinput);
		et.setOnKeyListener(new OnKeyListener()
		  {public boolean onKey(View v, int keyCode, KeyEvent event){
		    
			if (keyCode == KeyEvent.KEYCODE_ENTER) {				
				if (event.getAction() == KeyEvent.ACTION_UP) {					
					postmessage();
					return true;						
				}				
				return true;
			}
			return false;
		  }
		  }); 
		
		
		
	}

    
	private void refreshNavBar() {
		
		setTitle();
		
		if (Globo.chatlogChanNav.get()){
		
			LinearLayout navChanll;
	        View divider1;
			
			navChanll = (LinearLayout)this.findViewById(R.id.navchanll);
			
			
		synchronized(LogManager.serverLogMap) {
		  //NavBar.initialize(this, navChanll, listener);
		}
		
		}
		
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		
		super.onDestroy();
		
		
	}
	
	public  Handler myHandler=new Handler() {

	    public void handleMessage(Message msg) {
	      // TODO Auto-generated method stub
	      super.handleMessage(msg);
	      
	      String action = msg.getData().getString("action"); 
	      String type = msg.getData().getString("type");
	      
	      if (action == "addline") {
	    	  LogEntry logentry =(LogEntry) msg.getData().getSerializable("logentry");	    	  
	    	  adapter.addToList(logentry);
	    	  adapter.notifyDataSetChanged();
	    	  return;
	      }
	      
	      if (action == "toast") {   
	          ActChatLog.this.makeToast(msg.getData().getString("message"));
	          return;
	      }  
	      
	      if (action == "refresh") {
	    	  
	    	  refreshNavBar();
	      }
	      
	    }
	};

	


	private void postmessage() {
		
		
		
		String input = et.getText().toString();
		//check if server connected  - parse command else warn not connected
 
		if ( Globo.botManager.ircBots.containsKey(Globo.chatlogActNetworkName) ) {
			
			if (  Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).isConnected() ){

			  Globo.botManager.ircBots.get(Globo.chatlogActNetworkName).parseinput(input, Globo.chatlogActChannelName);
             				
			}else{
				notConnectedError();
			}
			
		}else {

		    notConnectedError();
			
		}
	
		et.setText("");
	}
	
	private void notConnectedError() {
		
		if(Globo.chatlogActType.equals("channel")){
			
			//message notconnected
			LogManager.writeChannelLog(MessageType.ERROR, "IRC Radio", getString(R.string.error_notconnectedtoserver), Globo.chatlogActNetworkName, Globo.chatlogActChannelName, Globo.chatlogActAccountName, this, true,"");
			et.setText("");
			
		}else{
			
			LogManager.writeServerLog("error",getString(R.string.error_notconnectedtoserver),"",Globo.chatlogActNetworkName,  Globo.chatlogActAccountName, this);
			
		}
		
		
	}
	
	
	public void makeToast(String message) {	      
	       Toast.makeText(this, message,  Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
	}

	@Override
	public void onCross(boolean crossed) {
		// TODO Auto-generated method stub
		
		LinearLayout navChanll;
   
		
		navChanll = (LinearLayout)this.findViewById(R.id.navchanll);
		
		
		
		
		
		if (!crossed ) {			
			if (Globo.chatlogChanNav.get()) {
				Globo.chatlogChanNav.set(false);
				navChanll.setVisibility(View.GONE);
		
			}else{
				Globo.chatlogChanNav.set(true);
				navChanll.setVisibility(View.VISIBLE);
		
				this.refreshNavBar();
			}		
		}else{
			if (Globo.chatlogActNav.get()) {
				Globo.chatlogActNav.set(false);
				this.getSupportActionBar().hide();
					
			}else{
				Globo.chatlogActNav.set(true);
				this.getSupportActionBar().show();
		
			}
		}
	} 
	
	
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		
		long network;
		String account;
		String channel;
		String type;
		
		boolean copysafe=false;		
		AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if (mi != null){
			copysafe = true;
		}
		
			
		
		boolean navbutton = true;
		if (v.getTag() == null) {
			navbutton = false;
			network = Globo.chatlogActNetworkName;
			account = Globo.chatlogActAccountName;
			channel = Globo.chatlogActChannelName;
			type = Globo.chatlogActType;			
		}else{
			NavItem ni = (NavItem)v.getTag();
			network = ni.network;
			account = ni.accountname;
			channel = ni.channelname;
			type = ni.acttype;
		}
		
		 menuData = new MenuData(network,account,channel,type);
				
			
			if (type.equals("server")) {
				menu.setHeaderTitle(account);        			                                    
				if (!navbutton && copysafe){
					menu.add(0, 5, 0, R.string.ctx_copy);
				}
				menu.add(0, 1, 0, R.string.ctx_clear);
                menu.add(0, 2, 0, R.string.ctx_disconnect);
                menu.add(0, 3, 0, R.string.ctx_close);
                menu.add(0, 4, 0, R.string.ctx_activatespeech);               
			}else{
				menu.setHeaderTitle(channel);
				if (!navbutton && copysafe){
					menu.add(0, 5, 0, R.string.ctx_copy);
				}
				menu.add(0, 0, 0, R.string.ctx_clear);
                if (channel.startsWith("#")){
                  menu.add(0, 1, 0, R.string.ctx_part);
                }
                menu.add(0, 2, 0 , R.string.ctx_close);
                menu.add(0, 3, 0, R.string.ctx_activatespeech);                
			}			
		
		
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int clickeditem = item.getItemId();
		
		
		if (menuData.type.equals("server")) {
            
			switch(clickeditem) {                        
            case 1:
            	LogManager.clearServer(menuData.network, menuData.account);            	
            	adapter.refresh();
            	this.adapter.notifyDataSetChanged();
            	this.refreshNavBar();
            	break;
            case 2:            	
            	Globo.botManager.disconnectServer(menuData.network);            	
            	break;
            case 3:
            	Globo.botManager.disconnectServer(menuData.network);
            	LogManager.closeServer(this, menuData.network);
            	nextLog();            	
            	break;
            case 4:
            	promptLanguage("server",menuData.network,"");            	
            	break;
            case 5:
            	AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            	
        		LogEntry logEntry = (LogEntry) adapter.getItem(mi.position);
            	clipboardCopy(logEntry);            	
            	break;	
            }
            return true;
			
			
		}
		
		if (menuData.type.equals("channel")) {
			switch(clickeditem) {            
            case 0:
            	LogManager.clearChannel(menuData.network, menuData.channel, menuData.account);
            	adapter.refresh();
            	this.adapter.notifyDataSetChanged();
            	this.refreshNavBar();
            	break;
            case 1:
            	Globo.botManager.partChannel(menuData.network, menuData.channel, this);            	
            	break;
            case 2:
            	Globo.botManager.partChannel(menuData.network, menuData.channel, this);
            	LogManager.closeChannel(this, menuData.network, menuData.channel , menuData.account);
            	nextLog();
            	break;
            case 3:
            	promptLanguage("channel",menuData.network,menuData.channel);
            	this.refreshNavBar();
            	break;
            case 5:
            	AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();            	
            	LogEntry logEntry = (LogEntry) adapter.getItem(mi.position);
            	clipboardCopy(logEntry);            	
            	break;	
            }
            return true;
		}				
		return false;
	}

	
	protected void clipboardCopy(LogEntry logEntry) {
		ClipboardManager cm = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
		cm.setText(logEntry.ss.toString());		
	}
	
	 protected void promptLanguage( String type, long network, String channel ) {
	    	PromptVoiceLanguage dlg = new PromptVoiceLanguage(this, type, network, channel); 
	    	dlg.show();
     }

	class NavBarListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			NavItem ni = (NavItem)v.getTag();
			changeLog(ni);
		}
	}
	
	public void changeLog(NavItem ni) {
		
		synchronized(LogManager.serverLogMap) {	
			Globo.chatlogActChannelName = ni.channelname;
			Globo.chatlogActNetworkName = ni.network;
			Globo.chatlogActType = ni.acttype;
			Globo.chatlogActAccountName = ni.accountname;
			
			Intent intent = new Intent(ActChatLog.this,ActChatLog.class);
			intent.putExtra("channel", ni.channelname);
			intent.putExtra("network", ni.network);
			intent.putExtra("acttype", ni.acttype);
			intent.putExtra("accountname", ni.accountname);
			ActChatLog.this.setIntent(intent);
			
			
			updateChecked();
			
			
			//font style  mono for server log.
			Typeface typeface = Typeface.DEFAULT;
			if (Globo.chatlogActType.equals("server") ) {
				typeface = Typeface.MONOSPACE;
			}
			
			adapter = new ListChatAdapter(ActChatLog.this, typeface, Globo.chatlogActAccountName  );
			listview.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			listview.setSelection(adapter.getCount());
			
			setTitle();
			
			LinearLayout navChanll;
	        navChanll = (LinearLayout)this.findViewById(R.id.navchanll);
			//NavBar.initialize(ActChatLog.this, navChanll, listener);
			}		
	}
	
	
	public void nextLog() {
		
		NavItem ni = new NavItem(Globo.chatlogActNetworkName,Globo.chatlogActAccountName);
		
		ni = LogManager.getNextLog(ni);
		
		if (ni.acttype.equals("none")) {
			finish();
		}else{
			this.changeLog(ni);
		}
		
	}

	private class MenuData {
		
		public long network;
		public String account;
		public String channel;
		public String type;
		
		MenuData(long network, String account, String channel, String type) {
		  this.network = network;
		  this.account = account;
		  this.channel = channel;
		  this.type = type;
		}
		
	}
	
}
