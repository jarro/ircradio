package com.earthflare.android.ircradio;

import java.util.LinkedList;
import java.util.ListIterator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.earthflare.android.logmanager.LogEntry;
import com.earthflare.android.logmanager.LogManager;

public class ListChatAdapter extends BaseAdapter {

	private LinkedList<LogEntry>  list; 
	private String type;
	private Context ctx;
	private Typeface typeface;
	private String accountname;
	
	LayoutInflater mInflater;
	
	public ListChatAdapter(Context ctx, Typeface typeface, String accountname) {
		
		this.ctx = ctx;
		this.typeface = typeface;
		this.accountname = accountname;
			
		mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		refresh();
		
	}
	
	public void testing() {
		
	    for (int i = 0; i < 50; i++) {
	    	addToList(new LogEntry(new SpannableString("test" + i),i));
	    }
	    for (int i = 0; i < 50; i++) {
	    	LogEntry le = new LogEntry(new SpannableString("QUIT" + i),i);
	    	le.type = MessageType.QUIT;
	    	addToList(le);
	    }
	    for (int i = 0; i < 50; i++) {
	    	addToList(new LogEntry(new SpannableString("ZZZZ" + i),i));
	    }
	    
	}
	
	public void refresh() {
		
		if (Globo.chatlogActType.equals("server") ) {
			type = "server";
			LinkedList<LogEntry> tempList =  LogManager.getServer(Globo.chatlogActNetworkName, accountname).chatLog;
			synchronized (tempList) {
			  list = (LinkedList<LogEntry>)tempList.clone();
			}
		}else{
			type = "channel";
			LinkedList<LogEntry> tempList = LogManager.getChannel(Globo.chatlogActNetworkName, Globo.chatlogActChannelName, accountname, true).chatlog;
			synchronized (tempList) {
			list = (LinkedList<LogEntry>) tempList.clone();
			}
		}
		
		
		
	}
	
	
	 public void addToList(LogEntry le){
			synchronized(list){
			list.addLast(le);
		    if (list.size() > Globo.CHANNELLOGMAX ) {
		    	list.removeFirst();
		    }
			}
		}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private View getBlankrow() {
		View blankrow = new View(ctx);
		blankrow.setTag(1);
		return blankrow;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	    
		switch(list.get(position).type) {		
		case MessageType.JOIN: 
			if (!Globo.pref_chan_join) {			  				  
			  return getBlankrow();
			}
            break;
		case MessageType.PART: 
			if (!Globo.pref_chan_part) {
				return getBlankrow();
			}
			break;
		case MessageType.KICK: 
			if (!Globo.pref_chan_kick) {
				return getBlankrow();
			}
			break;
		case MessageType.QUIT: 
			if (!Globo.pref_chan_quit) {
				return getBlankrow();
			}
			break;
		case MessageType.MODE: 
			if (!Globo.pref_chan_mode) {
				return getBlankrow();
			}
			break;
		case MessageType.INVITE: 
			if (!Globo.pref_chan_invite) {
				return getBlankrow();
			}
			break;
		case MessageType.STANDARD: 
			if (!Globo.pref_chan_message) {
				return getBlankrow();
			}
			break;
		}
		
		TextView tv = null;
		
		if (convertView != null) {
			if (convertView.getTag() == (Integer)2){
				tv = (TextView)convertView;
			}else{
				tv = (TextView)mInflater.inflate(R.layout.row_chat, parent, false);
				tv.setTag(2);
			}
		}else{
			tv = (TextView)mInflater.inflate(R.layout.row_chat, parent, false);
		}
			
		
		//append timestamp
		SpannableString ss = list.get(position).ss;
		if (Globo.pref_chan_timestamp) {
			SpannableStringBuilder sb = new SpannableStringBuilder(list.get(position).timestamp);
			sb.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0,  7 , 0);
			sb.append(ss);			
			tv.setText(sb);
		}else{			
			tv.setText(ss);
		}
		
		tv.setTypeface(typeface);
		
		if (list.get(position).hasname) {			
			tv.setBackgroundResource(R.color.clr_lightred);			
			tv.setTag(3);
		}
				
		return tv;
	}
	
	

	public boolean areAllItemsEnabled()
	{
	return true;
	}

	public boolean isEnabled(int position)
	{
	return true;
	}
	
	public int getPositionByUid (long uid) {
		//go to start if message gone
		
		synchronized (list) {
		ListIterator li = list.listIterator();
		int position = 0;
		LogEntry le;
		while ( li.hasNext() ) {
			le = (LogEntry)li.next();
			if (uid  == le.uid) {			   			   
				return position;
		   }else if (uid  < le.uid ){
			   return 0;
		   }
		
		   position ++;
		}
		
		return 0;
		}
	}

	
	
}
