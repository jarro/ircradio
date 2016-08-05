package com.earthflare.android.logmanager;

import java.util.LinkedList;

import com.earthflare.android.ircradio.Globo;

public class ChannelLog {

	
	public String channel;	
	public Boolean checked;
	public LinkedList<LogEntry> chatlog;
	//string 0 = type
	//string 1 = message
	//string 2 = sender
	
	public ChannelLog( String channel , Boolean internal) {
		
		if (channel == null) {
		  this.channel = "*NULL";	
		}else{
		  this.channel = channel;
		}
		
		this.chatlog = new LinkedList<LogEntry>();
		checked = true;
	}
	
	
    public void append(LogEntry le) {
		
	synchronized (chatlog) {	
		chatlog.addLast(le);
	    if (chatlog.size() > Globo.CHANNELLOGMAX ) {
	    	chatlog.removeFirst();
	    }
		
	}
	    
	}
	
	
	
}
