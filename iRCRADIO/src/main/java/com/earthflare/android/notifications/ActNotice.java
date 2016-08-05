package com.earthflare.android.notifications;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.earthflare.android.ircradio.ActChatLog;
import com.earthflare.android.ircradio.DialogMessage;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.GloboUtil;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.ircradio.TemplateNotice;

public class ActNotice extends TemplateNotice implements OnItemClickListener{
  
   
  
  public ListNoticeAdapter adapter;
  
  
  public  Handler myHandler=new Handler() {

    @Override
    public void handleMessage(Message msg) {
      // TODO Auto-generated method stub
      super.handleMessage(msg);
      
      String action = msg.getData().getString("action"); 
            
      if (action == "refresh") {           
          return;
      }
     
      if (action == "add"){
    	  Notice note = (Notice)msg.getData().getSerializable("notice");
    	  adapter.addToList(note);
    	  return;
      }
    }
    
    
  }; 
  
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.act_notice);
        
        initDrawer();
        
        GloboUtil.globalizePrefs(this);
        GloboUtil.claimaudiostream(this);
        
        this.initializeButtons();
        
        Globo.handlerNotice = myHandler;
        
        
    }
    
    
    public void populateList() {
      
       ListView lv = (ListView)this.findViewById(R.id.noticeListView);       
       adapter = new ListNoticeAdapter(this);
       lv.setAdapter(adapter);
       
       
       lv.setOnItemClickListener(this);
       lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
    	       public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		        		menu.setHeaderTitle(R.string.ctx_title_notifications);
               			menu.add(0, 0, 0, R.string.ctx_viewmessage);             			                               
               		}
       });
       
    }
    

    
    public boolean onContextItemSelected(MenuItem item) {
    	
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

    	
    	switch (item.getItemId()) {
 	    case 0:
 	      //view messages
 	      Notice note = adapter.getItem(info.position);
 	      DialogMessage di = new DialogMessage(this,note.sender,note.message);		  
 	      di.show();
 	      break;
    	}
    	
    	
    	return true;
   
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

    
    public void initializeButtons() {
      
      ((ListView)this.findViewById(R.id.noticeListView)).setOnItemClickListener(this);
      
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
      Globo.actNoticeVisible = false;
    }


    @Override
    protected void onResume() {
      // TODO Auto-generated method stub
      super.onResume();
      Globo.actNoticeVisible = true;      
      GloboUtil.claimaudiostream(this);
    }
    
    
    
    
    


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Notice note = (Notice)adapter.getItem(arg2);
		
		Long network = note.network;
		String channel = note.channel;
		String accountname = note.accountname;
		
		Intent launchChannel = new Intent(this,ActChatLog.class);
		launchChannel.putExtra("accountname", accountname);
		launchChannel.putExtra("network", network);
		launchChannel.putExtra("channel", channel);
		launchChannel.putExtra("acttype", "channel");
		launchChannel.putExtra("uid", note.uid);
		
		this.startActivity(launchChannel);
		
		
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
    	
}
    
