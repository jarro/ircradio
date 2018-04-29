package com.earthflare.android.ircradio;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;

import com.cratorsoft.android.language.IRCText;
import com.cratorsoft.android.language.LangMan;
import com.cratorsoft.android.language.ParsedLanguageAndEngine;
import com.cratorsoft.android.language.SCLM;


public class BotManager {
		
	public Map<Long,RadioBot> ircBots = new HashMap<Long,RadioBot>();
	
	
	
	synchronized public void addChannel(Context ctx, Intent intent ) {
		
		Boolean stopspeech = intent.getBooleanExtra("stopspeech", true);
		if(stopspeech){
		  MessageQueue.INSTANCE.flush();
		}
		
		// if bot exists for server  join channel
		String server = intent.getStringExtra("server");
		String port = intent.getStringExtra("port");
		String serverpass = intent.getStringExtra("serverpass");
		String channel = intent.getStringExtra("channel");
		String channelpass = intent.getStringExtra("channelpass");
		String joinleave = intent.getStringExtra("joinleave");
		String nick = intent.getStringExtra("nick");
		Long accountid = intent.getLongExtra("accountid", -1);
		String accountName = intent.getStringExtra("accountname");
		boolean guesscharset = intent.getBooleanExtra("guesscharset", false);
		boolean reconnect = intent.getBooleanExtra("reconnect", false);
		boolean ssl = intent.getBooleanExtra("ssl", false);
		String language = intent.getStringExtra("language");
		String ttsprefixes = intent.getStringExtra("ttsprefixes");
		if (ttsprefixes == null){
			ttsprefixes = "";
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		if (nick.equals("") ) { nick = ( preferences.getString("pref_nick", "droid") );}
		
		
		Map<String,String> channelinfo = new HashMap<String,String>();
		channelinfo.put("channelpass", channelpass);
		channelinfo.put("joinleave", joinleave);						
		channelinfo.put("language", language);
		channelinfo.put("ttsprefixes", ttsprefixes);
		
		if (ircBots.containsKey(accountid)) {
		    RadioBot rb = ircBots.get(accountid) ;
			if (!channel.equals("")) {
				  synchronized(rb.channelMap){
				  rb.channelMap.put(channel,channelinfo);
				  }
				}
				if (!rb.connecting && rb.isConnected()){  
                    // if already in channel refreshui         
					if (!rb.inChannel(channel)) {
					  rb.joinChannel(channel, channelpass);
					}else{
					  refreshUI(ctx);
					}

				}else if (!rb.connecting && !rb.isConnected()){
				  rb.connecting = true;
				  rb.startBot(intent);

				}

			
		}else{
			
			RadioBot rb = new RadioBot(ctx,nick,server,port, accountid, accountName, guesscharset, reconnect, ssl);			  
			  if (!channel.equals("")) {
				  synchronized(rb.channelMap){
				  rb.channelMap.put(channel, channelinfo);
				  }
			  }
			  ircBots.put(accountid, rb);			  
			  rb.startBot(intent);	
		}
		
				
	}
	
	synchronized public void kill() {
		
		for (Long key : ircBots.keySet()) {
			RadioBot rb = ircBots.get(key);
			if (rb != null) { 
			  rb.killing=true;
			  rb.disconnect();
			  rb.dispose();	
			}
		}		
		ircBots.clear();
        MessageQueue.INSTANCE.flush();
		
	}
	

    public int getStatusServer(long accountid) {
	  
    	//if account exists  and is  GREEN    	
    	//if account exists  and is connecting YELLOW
    	//if account !exists BLUE
    
      boolean isVoice = !(Globo.voiceServer == accountid && Globo.voiceType.equals("server"));
    	
	  if ( ircBots.containsKey(accountid) ) {
		  
		  if  ( ircBots.get(accountid).connecting  ) {
		      return  isVoice ? R.drawable.yellowsphere : R.drawable.ggyellow ; 
		  }else if (ircBots.get(accountid).isConnected()) {
			  return isVoice ? R.drawable.greensphere : R.drawable.gggreen; 
		  }else{
			  return isVoice ? R.drawable.bluesphere : R.drawable.ggblue;
		  }
		  
	  }else{ 
		  return isVoice ? R.drawable.bluesphere : R.drawable.ggblue;
	  }
	  
   }

    
    public int getStatusColorServer(long accountid, boolean checked) {
    	
	  if ( ircBots.containsKey(accountid) ) {		  
		  if  ( ircBots.get(accountid).connecting  ) {
		      return  checked ? R.color.ggyellowfaded : R.color.ggyellow ; 
		  }else if (ircBots.get(accountid).isConnected()) {
			  return checked ? R.color.gggreenfaded : R.color.gggreen; 
		  }else{
			  return checked ? R.color.ggbluefaded : R.color.ggblue;
		  }		  
	  }else{ 
		  return checked ? R.color.ggbluefaded : R.color.ggblue;
	  }	  
   }
    
   public int getStatusChannel(long accountid, String channel) {
	  
	   //if inchannel green
	   //if  connecting and in channelmap yellow
	   //else blue
	   
	   boolean isVoice = !(Globo.voiceServer == accountid && Globo.voiceChannel.equals(channel) && Globo.voiceType.equals("channel") );
	
	   if ( ircBots.containsKey(accountid) ) {
		   
		   RadioBot rb = ircBots.get(accountid);
		   if  ( rb.isConnected() && rb.inChannel(channel) )  {
			   return isVoice ? R.drawable.greensphere : R.drawable.gggreen;
		   }else if (rb.connecting && rb.channelMap.containsKey(channel)) {
			   return  isVoice ? R.drawable.yellowsphere : R.drawable.ggyellow ;
		   }else if (rb.isConnected() && rb.channelMap.containsKey(channel)) {
			   return  isVoice ? R.drawable.yellowsphere : R.drawable.ggyellow ;
		   }else if (rb.connecting && !rb.inChannel(channel)){
			   return  isVoice ? R.drawable.oarngesphere : R.drawable.ggoarnge ;
		   }else if (rb.isConnected() && !rb.inChannel(channel)){
			   return  isVoice ? R.drawable.oarngesphere : R.drawable.ggoarnge ;
		   }else{
			   return isVoice ? R.drawable.bluesphere : R.drawable.ggblue;
		   }
		   
		   
	   }else{ 
	     return isVoice ? R.drawable.bluesphere : R.drawable.ggblue;
	   }
	  
	   
   }
    
    
   public int getStatusColorChannel(long accountid, String channel, boolean checked) {
	   if ( ircBots.containsKey(accountid) ) {	   
		   RadioBot rb = ircBots.get(accountid);
		   if  ( rb.connecting && !channel.startsWith("#")  )  {
			   return  checked ? R.color.ggyellowfaded : R.color.ggyellow ;
		   }else if  ( (rb.isConnected() && rb.inChannel(channel) ) || (rb.isConnected() && !channel.startsWith("#") ) )  {
			   return checked ? R.color.gggreenfaded : R.color.gggreen;
		   }else if (rb.connecting && rb.channelMap.containsKey(channel)) {
			   return  checked ? R.color.ggyellowfaded : R.color.ggyellow ;
		   }else if (rb.isConnected() && rb.channelMap.containsKey(channel)) {
			   return  checked ? R.color.ggyellowfaded : R.color.ggyellow ;
		   }else if (rb.isConnected() && !rb.inChannel(channel)){
			   return  checked ? R.color.ggoarngefaded : R.color.ggoarnge ;
		   }else{
			   return checked ? R.color.ggbluefaded : R.color.ggblue;
		   }		   
	   }else{ 
		   return checked ? R.color.ggbluefaded : R.color.ggblue;
	   }
   }
   
   
    public static int[] getConnectedCount() {
        int[] counters = new int[3]; 
    	counters[0]= 0; //servers connected
    	counters[1]=0;  //channels connected
        counters[2]=0;  //servers connecting
    	
    	for (RadioBot rb : Globo.botManager.ircBots.values()  ) {
    	
    		if (rb.isConnected() ) {
    			counters[0] += 1;
    			counters[1] += rb.getChannels().length;
    		}else if (rb.connecting) {
    			counters[2] += 1;
    		}
    	}    	
    	return counters;
    }
   
    public static void refreshUI(Context ctx) {
  	  
       
     
      Intent serviceIconUpdate = new Intent(ctx, IrcRadioService.class);
      serviceIconUpdate.putExtra("action", "updateicon");
      ctx.startService(serviceIconUpdate);
  	  //check if actAccoutList visible then update

      if (Globo.actAccountListVisible) {  		 
  		    Bundle data = new Bundle();
  	        data.putString("action", "refresh");	        
  	        Message msg = new Message();
  	        msg.setData(data);
  	        Globo.handlerAccountList.sendMessage(msg);		  
  	  }
      
      
      if (Globo.actChatlogVisisble) {
    	  Bundle data = new Bundle();
    	  data.putString("action", "refresh");
    	  Message msg = new Message();
	      msg.setData(data);
	      Globo.handlerChatLog.sendMessage(msg);
      }
      
      if (Globo.actChannelListVisible) {  		 
		    Bundle data = new Bundle();
	        data.putString("action", "refresh");	        
	        Message msg = new Message();
	        msg.setData(data);
	        Globo.handlerChannelList.sendMessage(msg);		  
	  }
      
  	  
    }
    
    public void disconnectServer(long accountid) {
    	
    	if (this.ircBots.containsKey(accountid)) {
    		
    		ircBots.get(accountid).disconnectserver();
    		
    	}
    	
    }
    
   public void partChannel (long accountid,  String channel, Context ctx) {
	   
	   if (this.ircBots.containsKey(accountid) ) {
		   
		   if (this.ircBots.get(accountid).inChannel(channel)) {
               ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(accountid,channel);
			   this.ircBots.get(accountid).partChannel(channel, LangMan.INSTANCE.logLookup(IRCText.partmessage,plae.locale));
		   }
		   
	   }
	   
   }
   
public void partAccountChannel (long accountid,  String channel, Context ctx) {
	   
	   if (this.ircBots.containsKey(accountid) ) {
		   
		   if (this.ircBots.get(accountid).inChannel(channel)) {
               ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(accountid,channel);
               this.ircBots.get(accountid).partChannel(channel, LangMan.INSTANCE.logLookup(IRCText.partmessage,plae.locale));
		   }
		   
	   }
	   
   }
   



   public void disconnectAccount(long accountid) {
	   
	   if (this.ircBots.containsKey(accountid)) {		  
		   this.ircBots.get(accountid).disconnectserver();		  
	   }
	   
   }
    
}
