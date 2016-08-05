package com.earthflare.android.ircradio;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.cratorsoft.android.adapter.ExpandableListORMAccountAdapter;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;

public class ActAccountList extends TemplateAccount implements OnChildClickListener{
  
  ActAccountList thisAct; 
  
  
  private ActAccountList.DeleteListener deletelistener;
  private ActAccountList.DeleteListenerChannel deletelistenerchannel;
  private Intent radioServiceIntent;
  private ExpandableListORMAccountAdapter adapter;
  
  
  public  Handler myHandler=new Handler() {

    @Override
    public void handleMessage(Message msg) {
      // TODO Auto-generated method stub
      super.handleMessage(msg);
      
      String action = msg.getData().getString("action"); 
            
      if (action == "toast") {   
        thisAct.makeToast(msg.getData().getString("message"));
        return;
      }
      
      if (action == "refresh") {   
          //servercursor.requery();
    	  adapter.notifyDataSetChanged();    	  
          return;
        }
      
      if (action == "requery") {   
          //adapter.refresh();
    	      	  
          return;
        }
      
      if (action == "connected") {
        return;
      }
      
      if (action == "disconnected") {
        return;
      }
      
      if (action == "error") {
        return;
      }
      
      if (action == "nickerror") {
    	return;
      }
      

      
      if (action == "connecting") {    	         
    	return;  
      }
      
    }
    
    
  }; 
  
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.act_accountlist);
        this.setTitle(R.string.label_accounts);
        
        initDrawer();
        
        GloboUtil.globalizePrefs(this);
        GloboUtil.claimaudiostream(this);
        thisAct = this;
                
        
        Globo.handlerAccountList = myHandler;
        
        
        
        //DebugTrace.post(this,"test");         
        
    }
    
    
    public void populateList() {
            
      
      ExpandableListView listView = (ExpandableListView) findViewById(R.id.accountListView);
        //ListView listView = new ListView(this);
        
        
                     
        adapter = new ExpandableListORMAccountAdapter(this);
        adapter.setData(ExpandableListORMAccountAdapter.AdapterBundle.getAdapterBundle());
        
        listView.setAdapter(adapter);
        
        listView.setOnChildClickListener(this);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		        		
                		ExpandableListContextMenuInfo elcm = (ExpandableListContextMenuInfo) menuInfo;
                		
                		int type = ExpandableListView.getPackedPositionType(elcm.packedPosition);
                		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                			
                			int groupPosition = ExpandableListView.getPackedPositionGroup(elcm.packedPosition);
                			int childPosition = ExpandableListView.getPackedPositionChild(elcm.packedPosition);                			
                			                			
                			menu.setHeaderTitle(R.string.ctx_title_channelmenu);
                			menu.add(0, 3, 0, R.string.ctx_viewmessages);
                			menu.add(0, 0, 1, R.string.ctx_part);                                                                                    
                            menu.add(0, 1, 2, R.string.ctx_edit);
                            menu.add(0, 2, 3, R.string.ctx_delete);                               
                		}
                	
                	    else {
                			menu.setHeaderTitle(R.string.ctx_title_servermenu);        			
                			menu.add(0, 0, 0, R.string.ctx_addchannel);
                			menu.add(0, 1, 0, R.string.ctx_connect);                    
                            menu.add(0, 2, 0, R.string.ctx_disconnect);
                            menu.add(0, 3, 0, R.string.ctx_edit);
                            menu.add(0, 4, 0, R.string.ctx_delete);                                                
                		}}
                });
 
    }
    
    
    
    public boolean onContextItemSelected(MenuItem item) {
        
    	ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        AccountData accountData;
        ChannelData channelData;
        
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            int clickeditem = item.getItemId();
                       
            accountData = (AccountData) adapter.getGroup(groupPos);
            channelData = (ChannelData) adapter.getChild(groupPos, childPos);
            
            long channelid = channelData.id;
            long accountid = channelData.accountid;
            
            //network = adapter.getGroup(groupPos)
            
            switch(clickeditem) {
            
            case 0:  //part            	
            	Globo.botManager.partAccountChannel(accountData.id, channelData.channel, this);
            	break;
            case 1:  //edit
            	DialogManagerAccountChannel dmac = new DialogManagerAccountChannel(this,accountid);
            	dmac.promptEdit(channelid);
            	break;
            case 2:  //delete            	
            	this.deletelistenerchannel = this.new DeleteListenerChannel();
                this.deletelistenerchannel.channelid = channelid;
                Builder alert = new AlertDialog.Builder(this)
                  .setTitle(R.string.alert_title_confirmdeletechannel)
                  .setMessage(getString(R.string.alert_message_confirmdeletechannel) + " " + channelData.channel + getString(R.string.char_questionmark))
                  .setPositiveButton(R.string.button_yes, this.deletelistenerchannel)
                  .setNeutralButton(R.string.button_cancel, this.deletelistenerchannel)
                  .setIcon(android.R.drawable.ic_dialog_alert);     
                alert.show();        	
            	break;            
            case 3: //view messages
            	accountid = adapter.getGroupId(groupPos);
            	String accountname = accountData.accountname;
            	viewMessages(accountid, accountname ,channelData.channel);
            	break;
            }

            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {           
       	  int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);       	          
       	  int clickeditem = item.getItemId();
                   
       	  accountData = (AccountData) adapter.getGroup(groupPos);
       	  long accountid = accountData.id;
          
          switch(clickeditem) {            
          case 0: //add channel
            DialogManagerAccountChannel dmac = new DialogManagerAccountChannel(this,accountid);
            dmac.promptAdd();            
          	break;
          case 1: //connect
            this.startService(connectIntentServer((AccountData)adapter.getGroup(groupPos)));
           	break;
          case 2:// disconnect        	
          	          	
          	Globo.botManager.disconnectAccount(accountid);
          	break;          	
          case 3: //edit
        	  Intent configServer = new Intent(this,ActConfigServer.class);
        	  configServer.putExtra("action", "edit");
        	  configServer.putExtra("accountid", accountid);        	  
        	  this.startActivity(configServer);        	  
        	  //DialogManagerAccount dma = new DialogManagerAccount(this,db,servercursor);
              //dma.promptEdit(accountid);
          	break;
          case 4: //delete
        	  this.deletelistener = this.new DeleteListener();
              this.deletelistener.accountid = accountData.id;
              
              
              String name = accountData.accountname;
              
              Builder alert = new AlertDialog.Builder(this)
                .setTitle(R.string.alert_title_confirmdeleteaccount)
                .setMessage( getString(R.string.alert_message_confirmdeleteaccount) + " " + name + getString(R.string.char_questionmark))
                .setPositiveButton(R.string.button_yes, this.deletelistener)
                .setNeutralButton(R.string.button_cancel, this.deletelistener)
                .setIcon(android.R.drawable.ic_dialog_alert);     
              alert.show();
          	break;
          } 
            return true;
        }

        return false;
   
  }
   
    
    private void viewMessages(long network,  String accountname, String channel) {
							
		Intent launchChannel = new Intent(this,ActChatLog.class);
		launchChannel.putExtra("accountname", accountname);
		launchChannel.putExtra("network", network);
		launchChannel.putExtra("channel", channel);
		launchChannel.putExtra("acttype", "channel");
		this.startActivity(launchChannel);
	}
    
    private Intent connectIntentServer (AccountData accountData) {
    	
    	Intent mIntent = new Intent(this, IrcRadioService.class);
    	
	    mIntent.putExtra("server",accountData.server.toLowerCase());
        mIntent.putExtra("nick",accountData.nick);        
        mIntent.putExtra("accountname",accountData.accountname);
        mIntent.putExtra("serverpass",accountData.serverpass);
        mIntent.putExtra("port",accountData.port);
        mIntent.putExtra("auth",accountData.auth);        
        mIntent.putExtra("accountid", accountData.id);
        mIntent.putExtra("action","connect");
        
        mIntent.putExtra("autoidentify", Boolean.valueOf(accountData.autoidentify));
        mIntent.putExtra("nickserv", accountData.nickserv);
        mIntent.putExtra("nickpass", accountData.nickpass);
        mIntent.putExtra("perform", accountData.perform);
        mIntent.putExtra("encodingoverride", Boolean.valueOf(accountData.encodingoverride));
        mIntent.putExtra("encodingsend", accountData.encodingsend);
        mIntent.putExtra("encodingreceive", accountData.encodingreceive );
        mIntent.putExtra("encodingserver", accountData.encodingserver );
        mIntent.putExtra("reconnectinterval", accountData.reconnectinterval );
        mIntent.putExtra("reconnectretries", accountData.reconnectretries);
        
        mIntent.putExtra("channel", "");
        mIntent.putExtra("channelpass", "");
        mIntent.putExtra("autojoin", "0");
        mIntent.putExtra("language", "0");
        
        mIntent.putExtra("guesscharset", Boolean.valueOf(accountData.guesscharset));
        mIntent.putExtra("reconnect", Boolean.valueOf(accountData.reconnect));		
        mIntent.putExtra("ssl", Boolean.valueOf(accountData.ssl));
    	
    	return mIntent;
    }
    
    private Intent connectIntentChannel (Intent mIntent, ChannelData channelData) {
    		 
    	
	      mIntent.putExtra("channel",channelData.channel.toLowerCase());
	      mIntent.putExtra("channelpass",channelData.channelpass);
	      mIntent.putExtra("joinleave",channelData.joinleave);	      	      	      
	      mIntent.putExtra("language", channelData.language);
	      
    	  return mIntent;
    	
    }
    

    public void doPromptPost () {
      DialogManagerPost dmp = new DialogManagerPost(this);
      dmp.promptPost();
    }
    
    public void postMessage (String message) {
      radioServiceIntent.putExtra("action","post");
      radioServiceIntent.putExtra("message",message);
      thisAct.startService(radioServiceIntent);
    }
    

    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		populateList();
		
	}
    
    

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		

	}


	public void makeToast(String message) {
       
    
    try {
		Globo.toast.cancel();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
	}
       
	   Globo.toast = Toast.makeText(this, message,  Toast.LENGTH_LONG);	   
	   Globo.toast.show();
	  
       //Toast.makeText(this, message,  Toast.LENGTH_LONG).show();
    }
    
      
    
    
    

    @Override
    protected void onSaveInstanceState(Bundle outState) {
      // TODO Auto-generated method stub
      super.onSaveInstanceState(outState);
    }
    
   
    
    class DeleteListener implements android.content.DialogInterface.OnClickListener {

      public long accountid;
      @Override
      public void onClick(DialogInterface arg0, int button) {
        if (button == DialogInterface.BUTTON1)  {
          //delete entry
          
          try {
        	  Globo.db.beginTransaction();
        	  Globo.db.execSQL("delete from account where _id = " + accountid);
			  Globo.db.execSQL("delete from channel where accountid = " + accountid);
			  Globo.db.setTransactionSuccessful();
          } catch (SQLException e) {
			e.printStackTrace();
          } finally{
			Globo.db.endTransaction();
          }
          
          //disconnect bot if connected
            if (Globo.botManager.ircBots.containsKey(accountid)) {
            	Globo.botManager.ircBots.get(accountid).onDisconnect();
            }
          //adapter.refresh();
            
        }     
      }    
    }
    
    class DeleteListenerChannel implements android.content.DialogInterface.OnClickListener {
        public long channelid;
        @Override
        public void onClick(DialogInterface arg0, int button) {        	
          if (button == DialogInterface.BUTTON1)  {
              //delete entry                                   
        	  Globo.db.execSQL("delete from channel where _id = " + channelid);                            
              //adapter.refresh();
          }     
        }    
    }
    
    public void addAccount () {
      
      Intent configServer = new Intent(this,ActConfigServer.class);
  	  configServer.putExtra("action", "add");  	         	  
  	  this.startActivity(configServer);
      //DialogManagerAccount dma = new DialogManagerAccount(this,db,servercursor);
      //dma.promptAdd();
      
    }


    @Override
    protected void onPause() {
      // TODO Auto-generated method stub
      super.onPause();
      Globo.actAccountListVisible = false;
    }


    @Override
    protected void onResume() {
      // TODO Auto-generated method stub
      super.onResume();
      
      Globo.actAccountListVisible = true;
      //adapter.refresh();
      GloboUtil.claimaudiostream(this);
    }
    
    
    

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();		
	}


	@Override
	public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPosition,
			int childPosition, long id) {

		  Intent mIntent;
		  
		  ChannelData channelData = (ChannelData) adapter.getChild(groupPosition, childPosition);
		  AccountData accountData = (AccountData) adapter.getGroup(groupPosition);
	      
	      mIntent = connectIntentServer(accountData);
	      mIntent = connectIntentChannel(mIntent, channelData);

          Globo.setTTSServer(accountData.id, accountData.language);
	      Globo.setTTSChannel(channelData.accountid, channelData.channel.toLowerCase(),  channelData.language );
	                  
	      this.startService(mIntent);
			
	      return true;
		
	}

   
    	
}
    
