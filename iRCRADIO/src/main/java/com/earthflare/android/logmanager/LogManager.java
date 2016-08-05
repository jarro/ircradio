package com.earthflare.android.logmanager;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.style.ForegroundColorSpan;

import com.cratorsoft.android.language.IRCText;
import com.cratorsoft.android.language.LangMan;
import com.cratorsoft.android.language.ParsedLanguageAndEngine;
import com.cratorsoft.android.language.SCLM;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.IrcRadioService;
import com.earthflare.android.ircradio.LineFormatter;
import com.earthflare.android.ircradio.MessageBundle;
import com.earthflare.android.ircradio.MessageQueue;
import com.earthflare.android.ircradio.MessageType;
import com.earthflare.android.ircradio.NavItem;
import com.earthflare.android.ircradio.R;
import com.earthflare.android.ircradio.RadioBot;
import com.earthflare.android.ircradio.SpanFormat;
import com.earthflare.android.ircradio.SpannableStringSerial;
import com.earthflare.android.notifications.NoticeManager;

public class LogManager {

	private static AtomicLong uid = new AtomicLong(0); 
	
	public static Map<Long ,ServerLog> serverLogMap = new HashMap<Long ,ServerLog>();
	
	
	
	
	
	synchronized public static void writeServerLog (String type, String message, String sender, long network, String accountname, Context ctx) {
	    
		synchronized (serverLogMap) {
		
		long tempuid = 0;
		
		ServerLog sl;
		SpannableStringSerial ss = new SpannableStringSerial(message);		
		ss.setSpan(new ForegroundColorSpan(0xffbebebe), 0,  message.length() , 0);
		
		if (serverLogMap.containsKey(network)) {
			sl = serverLogMap.get(network);
		}else{
			sl = new ServerLog(network, accountname);
			serverLogMap.put(network, sl);
			refreshUI(ctx);
	    }
		
		  tempuid = uid.incrementAndGet();
	      LogEntry le = new LogEntry(ss, tempuid);
		synchronized(sl){		  
		  sl.append(le);	     
		}
					
		
	    
	    if (  Globo.chatlogActType.equals("server") && Globo.chatlogActNetworkName == network   &&  Globo.actChatlogVisisble   )    {
	    	
	    	Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("action", "addline");
	        data.putSerializable("logentry", le );
	        msg.setData(data);
	        Globo.handlerChatLog.sendMessage(msg);	    	
	    }else{
	    	if (sl.checked ) {
	    		sl.checked = false;
	    		refreshServerCounters();
	    	}
	    }	  
	    
		}
	}
	
	
	public static void writeChannelLog (int type, String sender , String message, long network, String channel, String accountname, Context ctx, boolean internal, String nick) {
	    
		synchronized (serverLogMap) {
		
		long tempuid = uid.incrementAndGet();;
		boolean hasname = false;
		LogEntry logentry;
		
		SpannableStringSerial ss = new SpannableStringSerial(message);
		List<SpanFormat> spans = LineFormatter.format(type ,sender.length(), message.length() );        	
    	for (SpanFormat sf : spans) {
    		
    		ss.setSpan(new ForegroundColorSpan(sf.color), sf.start,  sf.end , 0);
    	}
		
		
		ServerLog sl;
		ChannelLog cl;
		
		if (serverLogMap.containsKey(network)) {
			sl = serverLogMap.get(network);
		}else{
			sl = new ServerLog(network, accountname);			
			serverLogMap.put(network, sl);			
	    }
		
		if (sl.channelLogMap.containsKey(channel)) {
			cl = sl.channelLogMap.get(channel);
		}else{
			cl = new ChannelLog(channel,internal);
			sl.channelLogMap.put(channel, cl);
			refreshUI(ctx);
		}
		
				
			
	    
        //create notification
		//test if contains name
		if (sender != nick) {
		  if (type == MessageType.STANDARD && message.contains(nick) ) {
           hasname = true;			
		   
           NoticeManager.addNamed(ctx, sl.accountName, message, network, channel, sender , tempuid);             
		   
			
		 }
		}
		
		synchronized(cl){     	    
			logentry = new LogEntry(ss,tempuid,hasname,type);
     	    cl.append(logentry);	      
		}	
		
		
	    // if this channel visible   
		if (   Globo.actChatlogVisisble  && Globo.chatlogActType.equals("channel") && Globo.chatlogActChannelName.equalsIgnoreCase(channel) && Globo.chatlogActNetworkName == network  ) {
	    	Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("action", "addline");
	        data.putSerializable("logentry", logentry );
	        
	        msg.setData(data);
	        Globo.handlerChatLog.sendMessage(msg);	    	
	    }else{
	    	if (cl.checked && !internal) {
	    		//say new private message if it is.
	    		LogManager.announceNewPrivateMessage(ctx, cl, sender, type, network);
	    		cl.checked = false;
	    		refreshChannelCounters(channel , ctx);
	    		
	    		//create new private notification
	    		if (type == MessageType.PRIVATE  || type == MessageType.PRIVATE_NOTICE || type == MessageType.PRIVATE_ACTION)  {	            			
	    		   NoticeManager.addPrivate(ctx, sl.accountName, message, network, channel, sender , tempuid);	    			
	    		}
	    	}
	    }
		
		}
	  
	}
	
	private static void refreshChatNav() {
		if (Globo.actChatlogVisisble) {
	    	  Bundle data = new Bundle();
	    	  data.putString("action", "refresh");
	    	  Message msg = new Message();
		      msg.setData(data);
		      Globo.handlerChatLog.sendMessage(msg);
	      }
	}
	
	private static void refreshChannelCounters(String channel , Context ctx) {
		
		refreshChatNav();
		
		if (Globo.actChannelListVisible) {						
		  Bundle data = new Bundle();
		  data.putString("action", "refresh");	        
		  Message msg = new Message();
		  msg.setData(data);
		  Globo.handlerChannelList.sendMessage(msg);		  
		}
		
		if (!channel.startsWith("#")) {
		  //refresh notification
			
		     
		      Intent serviceIconUpdate = new Intent(ctx, IrcRadioService.class);
		      serviceIconUpdate.putExtra("action", "updateicon");
		      ctx.startService(serviceIconUpdate);			
		}				
	}
	
	
	private static void refreshServerCounters() {
	
		refreshChatNav();
		
		if (Globo.actChannelListVisible) {						
			  Bundle data = new Bundle();
			  data.putString("action", "refresh");	        
			  Message msg = new Message();
			  msg.setData(data);
			  Globo.handlerChannelList.sendMessage(msg);		  
			}
	}
	
	synchronized public static void deleteLogs () {		
		serverLogMap.clear();
		
	}
	
	
	private static String getTime() {		
		Format formatter;
		formatter = new SimpleDateFormat("<HH:mm> ");		
		Date d = new Date();
		d.getTime();
		String s = formatter.format(d);
		return s;		
	}	
	
	public static void refreshUI(Context ctx) {
	  	  
	     
	     
	      Intent serviceIconUpdate = new Intent(ctx, IrcRadioService.class);
	      serviceIconUpdate.putExtra("action", "updateicon");
	      ctx.startService(serviceIconUpdate);
	  	  //check if actAccoutList visible then update

	      
	      
	      
	      refreshChatNav();
	      
	      if (Globo.actChannelListVisible) {  		 
			    Bundle data = new Bundle();
		        data.putString("action", "refresh");	        
		        Message msg = new Message();
		        msg.setData(data);
		        Globo.handlerChannelList.sendMessage(msg);		  
		  }
	      
	      
	  	  
	    }
	
	public static int[] countChecked() {
		
		int[] counter = new int[]{0,0,0,0,0,0};
		 // servers total, servers unchecked,  channels total,  channels unchecked,  private chats ,  private chats unchecked
		
		synchronized(LogManager.serverLogMap.values() ) {
		for (ServerLog sl: LogManager.serverLogMap.values() ) {
		  counter[0] += 1;        			
		  if (!sl.checked) counter[1] += 1;	
			
		    for ( ChannelLog cl: sl.channelLogMap.values() ) {
		    	
		    	if ( cl.channel.startsWith("#"  )   ) {
					
					counter[2] += 1;
					if (!cl.checked) counter[3] += 1;
					
				}else{
					counter[4] += 1;
					if (!cl.checked) counter[5] += 1;
				}

		    }
		}
		}
		
	    return counter;
	  	
	}
	
	public static ServerLog getServer(long server, String accountname) {
		
		ServerLog sl;
		
		if (serverLogMap.containsKey(server) ) {
			
		  sl = serverLogMap.get(server);	
			
		}else{
			sl = new ServerLog(server, accountname);
			serverLogMap.put(server, sl);
		}
		
		return sl;
		
	}
	
	public static ChannelLog getChannel(long server, String channel, String accountname, boolean internal) {
		
		ServerLog sl = getServer(server, accountname);
		ChannelLog cl;
		
		if (sl.channelLogMap.containsKey(channel)) {
			
			cl = sl.channelLogMap.get(channel);
			
		}else{
			
			cl = new ChannelLog(channel, internal);
			sl.channelLogMap.put(channel, cl);
		}
		
		return cl;
	}
	
	
	synchronized public static void resetLogs () {		
		
		Map tempServerLogMap = serverLogMap;
		ServerLog sl;
		ChannelLog cl;
		serverLogMap = new HashMap<Long ,ServerLog>();
		
		for (RadioBot rb: Globo.botManager.ircBots.values() ) {
			
			if (rb.isConnected()) {
				sl = new ServerLog(rb.botID, rb.accountName);
				sl.checked = true;
				serverLogMap.put(rb.botID, sl );
				String[] chans = rb.getChannels();
				for (String channel: chans) {
					cl = new ChannelLog(channel, true);
					cl.checked = true;
					sl.channelLogMap.put(channel,cl);
				}
			}
		}
		
	}
	
	public static void clearChannel (long network, String channel, String accountname) {
		
	   ChannelLog cl = LogManager.getChannel(network, channel, accountname, true);
       cl.chatlog.clear();
       cl.checked = true;
	}
	
	public static void closeChannel (Context ctx, long network, String channel, String accountname) {
		
		
		   ServerLog sl = LogManager.getServer(network, accountname);
	       sl.channelLogMap.remove(channel);
		   
	       if (channel.startsWith("#")) {
				 //refresh notification
				 Intent serviceIconUpdate = new Intent(ctx, IrcRadioService.class);
			     serviceIconUpdate.putExtra("action", "updateicon");
			     ctx.startService(serviceIconUpdate);
			 }
	       
		}
	
	public static void closeServer (Context ctx, Long network) {
		
		   
		   LogManager.serverLogMap.remove(network);
	       	 //refresh notification
				 Intent serviceIconUpdate = new Intent(ctx, IrcRadioService.class);
			     serviceIconUpdate.putExtra("action", "updateicon");
			     ctx.startService(serviceIconUpdate);
			
	       
	}
	
	public static void clearServer (long network, String accountname) {
		
		ServerLog sl = LogManager.getServer(network, accountname);
	    sl.chatLog.clear();
	    sl.checked = true;
	}
	
	private static void announceNewPrivateMessage(Context ctx, ChannelLog cl, String sender, int type, long network) {
		//abort for stuff always announced
		if (type == MessageType.INVITE) {return;}
		
		
		
		if (cl.checked && !cl.channel.startsWith("#") && ( Globo.voiceServer != network || !sender.equals(Globo.voiceChannel))  ) {			
			//say you have a new private message
			MessageBundle mb = new MessageBundle();

            ParsedLanguageAndEngine plae = SCLM.INSTANCE.getChannelPLAE(network,cl.channel);

			if (type == MessageType.NOTICE && Globo.ttsspeak_privatenotice ){
				mb.message = LangMan.INSTANCE.speechLookup(IRCText.newnoticefrom, plae.locale) + " " + sender;
			}else if(type == MessageType.STANDARD && Globo.ttsspeak_privatemessage){
				mb.message = LangMan.INSTANCE.speechLookup(IRCText.newprivatemessagefrom, plae.locale) + " " + sender;
			}else{
				return;
			}
			  
			
			mb.type = "action";
            mb.plae = plae;

            MessageQueue.INSTANCE.addfront(mb);
		}
		
	}
	
	
	public static NavItem getActiveLog() {
		
		if(Globo.chatlogActType.equals(NavItem.CHANNEL) ) {
			
			if (LogManager.serverLogMap.containsKey(Globo.chatlogActNetworkName) ){
				if (LogManager.serverLogMap.get(Globo.chatlogActNetworkName).channelLogMap.containsKey(Globo.chatlogActChannelName) ){
					return new NavItem(Globo.chatlogActNetworkName, Globo.chatlogActAccountName, Globo.chatlogActChannelName);
				}else{
					return new NavItem(Globo.chatlogActNetworkName, Globo.chatlogActAccountName);
				}
			}
			
		}else if (Globo.chatlogActType.equals(NavItem.SERVER)){
			if (LogManager.serverLogMap.containsKey(Globo.chatlogActNetworkName) ){
				return new NavItem(Globo.chatlogActNetworkName, Globo.chatlogActAccountName);
			}
		}
		return LogManager.getFirstLog();
	}
	
	
	public static NavItem getNextLog(NavItem ni) {
		
		boolean found = false;
		NavItem nextLog = new NavItem(false);
		
			synchronized(LogManager.serverLogMap ) {
				
				if (LogManager.serverLogMap.isEmpty()) {
					//return empty
				    nextLog = new NavItem(false);
				    return nextLog;
				}
				
				if (LogManager.serverLogMap.containsKey(ni.network)) {
					//return network
					nextLog = new NavItem(ni.network,ni.accountname);
					return nextLog;
				}else{
					//return first network
					for (ServerLog sl: LogManager.serverLogMap.values()){
						nextLog = (new NavItem(sl.network,sl.accountName));
						break;
					}
				    return nextLog;
				}
				
				
			}
			

	}
	
	public static NavItem getFirstLog() {
		
		boolean found = false;
		NavItem nextLog = new NavItem(false);
		
			synchronized(LogManager.serverLogMap ) {
				
				if (LogManager.serverLogMap.isEmpty()) {
					//return empty
				    nextLog = new NavItem(false);
				    return nextLog;
				}
				//return first network
				for (ServerLog sl: LogManager.serverLogMap.values()){
					nextLog = (new NavItem(sl.network,sl.accountName));
					break;
				}
				return nextLog;
			}
		
	}

	
}
