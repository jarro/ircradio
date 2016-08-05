package com.earthflare.android.ircradio;


import android.content.Intent;
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

import com.earthflare.android.logmanager.LogManager;

public class ActChannelList extends TemplateChannel implements   OnChildClickListener{
  
  ActChannelList thisAct; 
  ExpandableListChannelAdapter adapter;
  
  public  Handler myHandler=new Handler() {

    @Override
    public void handleMessage(Message msg) {
      // TODO Auto-generated method stub
      super.handleMessage(msg);
      
      String action = msg.getData().getString("action"); 
            
      if (action == "refresh") {   
          refreshUI();    
          return;
      }
      
      if (action == "toast") {   
          thisAct.makeToast(msg.getData().getString("message"));
          return;
      } 
    
    }
    
    
  }; 
  
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_channellist);
        thisAct = this;

        initDrawer();
        Globo.handlerChannelList = myHandler;
        
         
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


	public void populateList() {
      ExpandableListView listView = (ExpandableListView) findViewById(R.id.channelListView);
      adapter = new ExpandableListChannelAdapter(this);
      listView.setAdapter(adapter);
      
        listView.setOnChildClickListener(this);        
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

        	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                        		        		
        		ExpandableListContextMenuInfo elcm = (ExpandableListContextMenuInfo) menuInfo;
        		
        		int type = ExpandableListView.getPackedPositionType(elcm.packedPosition);
        		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
        			
        			int groupPosition = ExpandableListView.getPackedPositionGroup(elcm.packedPosition);
        			int childPosition = ExpandableListView.getPackedPositionChild(elcm.packedPosition);
        			String channel = adapter.children[groupPosition][childPosition];
        			
        			
        			menu.setHeaderTitle(channel);
                    menu.add(0, 0, 0, R.string.ctx_clear);
                    if (channel.startsWith("#")){
                      menu.add(0, 1, 0, R.string.ctx_part);
                    }
                    menu.add(0, 2, 0 , R.string.ctx_close);
                    menu.add(0, 3, 0, R.string.ctx_activatespeech);   
                    
        		}else{
        			
        			menu.setHeaderTitle(adapter.groupNames[ExpandableListView.getPackedPositionGroup(elcm.packedPosition)]);        			
                    menu.add(0, 0, 0, R.string.ctx_viewlog);                    
                    menu.add(0, 1, 0, R.string.ctx_clear);
                    menu.add(0, 2, 0, R.string.ctx_disconnect);
                    menu.add(0, 3, 0, R.string.ctx_close);
                    menu.add(0, 4, 0, R.string.ctx_activatespeech);                    
        		}
            }

        });
        
    }
    
    
    public boolean onContextItemSelected(MenuItem item) {
      
    	ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String channel;
        Long network;
        
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            int clickeditem = item.getItemId();
            channel = adapter.children[groupPos][childPos];
            network = adapter.groups[groupPos];
            String accountName = adapter.groupNames[groupPos];
            
            switch(clickeditem) {            
            case 0:
            	LogManager.clearChannel(network, channel, accountName);
            	this.refreshUI();
            	break;
            case 1:
            	Globo.botManager.partChannel(network, channel, this);
            	break;
            case 2:
            	Globo.botManager.partChannel(network, channel, this);
            	LogManager.closeChannel(this, network, channel , accountName);
            	this.refreshUI();
            	break;
            case 3:
            	promptLanguage("channel",network,channel);
            	this.refreshUI();
            	break;
            }

            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int clickeditem = item.getItemId();
            network = adapter.groups[groupPos];
            String accountName = adapter.groupNames[groupPos];
            switch(clickeditem) {
            
            case 0:
            	Intent showServerLog = new Intent(this, ActChatLog.class);
            	showServerLog.putExtra("acttype", "server");
            	showServerLog.putExtra("channel", "");
            	showServerLog.putExtra("accountname", accountName);
            	showServerLog.putExtra("network", adapter.groups[groupPos]);
            	this.startActivity(showServerLog);
            	break;
            case 1:
            	LogManager.clearServer(network, accountName);
            	this.refreshUI();
            	break;
            case 2:            	
            	Globo.botManager.disconnectServer(network);            	
            	break;
            case 3:
            	Globo.botManager.disconnectServer(network);
            	LogManager.closeServer(this, network);
            	this.refreshUI();
            	break;
            case 4:
            	promptLanguage("server",network,"");            	
            	break;
            }
            return true;
        }

        return false;

    
  }
    
    
    
    protected void promptLanguage( String type, long network, String channel ) {
    
    	
    	PromptVoiceLanguage dlg = new PromptVoiceLanguage(this, type, network, channel); 
    	dlg.show();
    	
    }
    
      
   
    
     
   
    

    public void makeToast(String message) {      
       Toast.makeText(this, message,  Toast.LENGTH_LONG).show();
    }
    



    @Override
    protected void onSaveInstanceState(Bundle outState) {
      // TODO Auto-generated method stub
      super.onSaveInstanceState(outState);
    }
    
   


    @Override
    protected void onPause() {
      // TODO Auto-generated method stub
      super.onPause();
      Globo.actChannelListVisible = false;
    }


    @Override
    protected void onResume() {
      // TODO Auto-generated method stub
      super.onResume();
      Globo.actChannelListVisible = true;
      GloboUtil.claimaudiostream(this);
    }
    
   


	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
	
		Long network = adapter.groups[groupPosition];
		String channel = adapter.children[groupPosition][childPosition];
		String accountname = adapter.groupNames[groupPosition];
		
		Intent launchChannel = new Intent(this,ActChatLog.class);
		launchChannel.putExtra("accountname", accountname);
		launchChannel.putExtra("network", network);
		launchChannel.putExtra("channel", channel);
		launchChannel.putExtra("acttype", "channel");
		this.startActivity(launchChannel);
		return true;
	}
    
    
    public void refreshUI() {
    	adapter.refresh();
        adapter.notifyDataSetChanged();      
    }
    
    
    
    
 
 
   
}
    
