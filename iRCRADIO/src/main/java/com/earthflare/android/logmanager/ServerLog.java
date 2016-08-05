package com.earthflare.android.logmanager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import android.os.Bundle;

import com.earthflare.android.ircradio.Channel;
import com.earthflare.android.ircradio.ChannelComparator;

public class ServerLog {

	public static final int SERVERLOGMAX = 500;
	
	public boolean checked;
	public long network;
	public String accountName;
	public LinkedList<LogEntry> chatLog;
	//string 0 = type
	//string 1 = message
	public LinkedList<Channel> channels = new LinkedList<Channel>();
	public LinkedList<Channel> channelsProcessed = new LinkedList<Channel>();
	public boolean newchannels = false;
	
	public Map<String,ChannelLog> channelLogMap;
	
	public ServerLog(long network, String accountname ) {
	
		this.network = network;
		this.chatLog = new LinkedList<LogEntry>();
		this.channelLogMap = new HashMap<String,ChannelLog>();
		this.checked = false;
		this.accountName = accountname;
	}
	
	
	
	public void append(LogEntry le) {
		
	synchronized(chatLog){	
		chatLog.addLast(le);
	    if (chatLog.size() > SERVERLOGMAX ) {
	    	chatLog.removeFirst();
	    }
	}
	
	}
	
	
    public int[] countChecked() {
		
		int[] counter = new int[]{0,0,0,0};
		 // channels , channels unchecked,  private chats,  private chats unchecked
		
		for (ChannelLog cl: this.channelLogMap.values() ) {
		  
			if ( cl.channel.startsWith("#"  )   ) {
				
				counter[0] += 1;
				if (!cl.checked) counter[1] += 1;
				
			}else{
				counter[2] += 1;
				if (!cl.checked) counter[3] += 1;
			}
		  
		}
	    
	    return counter;
	  	
	}
	
    
    //channels manipulation
    private boolean sortOrderFlag = false;
    private boolean sortUsersFlag = false;
    private boolean sortChannelsFlag = false;
    
    public void resetSortFlags() {
    	sortOrderFlag = false;
    	sortUsersFlag = false;
    	sortChannelsFlag = false;
    }
    
    public void sortChannels() {
		
    	synchronized(channelsProcessed){
			if (sortOrderFlag == true && sortChannelsFlag == true ) {
				Collections.sort(channelsProcessed,ChannelComparator.nameComparatorDescending);
				resetSortFlags();
			    sortOrderFlag = false;
			    sortChannelsFlag = true;
			}else{
				Collections.sort(channelsProcessed,ChannelComparator.nameComparator);
				resetSortFlags();
			    sortOrderFlag = true;
			    sortChannelsFlag = true;
			}
			
		}
	}
    
	
    public void sortUsers() {
		
    	synchronized(channelsProcessed){
			if (sortOrderFlag == true && sortUsersFlag == true ) {
				Collections.sort(channelsProcessed,ChannelComparator.userComparator);
				resetSortFlags();
			    sortOrderFlag = false;
			    sortUsersFlag = true;
			}else{
				Collections.sort(channelsProcessed,ChannelComparator.userComparatorDescending);
				resetSortFlags();
			    sortOrderFlag = true;
			    sortUsersFlag = true;
			}
			
		}
	}
    
    public void synchChannels() {
    	synchronized (channelsProcessed){
    	synchronized (channels) {
    		channelsProcessed = (LinkedList<Channel>)channels.clone();
    		newchannels = false;
    		resetSortFlags();
    	}
    	}
    }
    
    public void filterChannels(Bundle bundle){
		
		int min = bundle.getInt("min");
		int max = bundle.getInt("max");
		String filter = bundle.getString("filtertext");
		boolean skipfilter = false;
		if (filter.equals("")){skipfilter = true;}
		
		synchronized (channelsProcessed){
		synchronized (channels) {
    		
		    //loop through channels delete any fails 
            LinkedList<Channel> templist = new LinkedList<Channel>();
    		ListIterator itr = channels.listIterator();
    		boolean passflag = true;
    		while(itr.hasNext()) {
    			
    			passflag = true;
    			Channel chan = (Channel)itr.next();
    			if (chan.users < min ) {
    				passflag = false;
    			}else if (chan.users > max) {
    				passflag = false;
    			}else if (skipfilter) {
    				passflag = true;
    			}else if (!chan.name.contains(filter)){
    				passflag = false;
    			}
    			
    			if (passflag) {templist.addLast(chan);}
    		}
    		
    		channelsProcessed = templist;
    		
		}
		}
		
		if (sortOrderFlag) {sortOrderFlag = false;}else{sortOrderFlag = true;}
		//synchronize sorting
		if (sortUsersFlag) {
			sortUsers();
		}else if (sortChannelsFlag) {
			sortChannels();
		}
		
	}
    
}
