package com.earthflare.android.ircradio;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.earthflare.android.logmanager.ChannelLog;
import com.earthflare.android.logmanager.LogManager;
import com.earthflare.android.logmanager.ServerLog;


public class ExpandableListChannelAdapter extends BaseExpandableListAdapter {
    // Sample data set.  children[i] contains the children (String[]) for groups[i].
    
	private Context ctx;

    public Long[] groups = null;
    public String[] groupNames = null;
    boolean[] groupChecked = null;
    public String[][] children = null;
    boolean[][] childrenChecked = null;
    int[][] unreadcounters = null;
    //channels, channels unread,  private chats,  private chats unread
    
    LayoutInflater mInflater;
    
    //groupList = (ArrayList) ;
    //List<String> gl = new ArrayList<String[]>(LogManager.serverLogMap.keySet().toArray());                 
            
    public void refresh(){
    
    synchronized(LogManager.serverLogMap) {    	
    	groups = new Long[LogManager.serverLogMap.size()];
    	groupNames = new String[groups.length];
    	groupChecked = new boolean[groups.length];
    	children = new String[groups.length][];
    	childrenChecked = new boolean[groups.length][];
    	unreadcounters = new int[groups.length][];
    	
    	int grpcounter = 0;
    	for (ServerLog sl : LogManager.serverLogMap.values() ) {
    		
    		groups[grpcounter] = sl.network;
    		groupChecked[grpcounter] = sl.checked;
    		groupNames[grpcounter] = sl.accountName;
    		children[grpcounter] = new String[sl.channelLogMap.size()];
    		childrenChecked[grpcounter] = new boolean[sl.channelLogMap.size()];
    		unreadcounters[grpcounter] =  sl.countChecked();    	  	
    	  	
    		int childcounter = 0;
    		for(ChannelLog cl : sl.channelLogMap.values() ) {
    			children[grpcounter][childcounter] = cl.channel;
    			childrenChecked[grpcounter][childcounter] = cl.checked;
    			childcounter++;
    		}
    		grpcounter++;
    	}

    	
    }	
    	
    }
    
    
    public ExpandableListChannelAdapter(Context ctx) {
    	super();
    	this.ctx = ctx;
    	mInflater = ((Activity)ctx).getLayoutInflater();
    	refresh();
    
    }
    

    public Object getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
    	TextView textView;
    	if (convertView != null) {
    		textView = (TextView)convertView;
    	}else{
    		 		
    		textView  = (TextView)mInflater.inflate(R.layout.row_channellist, parent, false);
    	}
                
        SpannableString ss = new SpannableString(children[groupPosition][childPosition]);
                
        int textcolor;
        textcolor = Globo.botManager.getStatusColorChannel( groups[groupPosition] , children[groupPosition][childPosition], childrenChecked[groupPosition][childPosition] );		

		textView.setTextColor(ctx.getResources().getColor(textcolor));
		boolean isVoice = (Globo.voiceServer == groups[groupPosition] && Globo.voiceChannel.equals(children[groupPosition][childPosition]) && Globo.voiceType.equals("channel") );
		if (isVoice) {
			ss.setSpan(new StyleSpan(Typeface.ITALIC), 0, children[groupPosition][childPosition].length(), 0);
		}
        textView.setText(ss);
        return textView;
    }

    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    public int getGroupCount() {
        return groups.length;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {            	    	
    	
    	View ll;
    	if (convertView != null) {
    		ll = convertView;
    	}else{
    		 		
    		ll  = mInflater.inflate(R.layout.row_channelgrouplist, parent, false);
    	}
    	
    	TextView textView = (TextView) ll.findViewById(R.id.text1);    	    	
        
    	SpannableString ss = new SpannableString(this.groupNames[groupPosition]);
    	int textcolor = Globo.botManager.getStatusColorServer(groups[groupPosition], groupChecked[groupPosition]);
    	textView.setTextColor(ctx.getResources().getColor(textcolor));
    	boolean isVoice = (Globo.voiceServer == this.groups[groupPosition] && Globo.voiceType.equals("server"));
    	if (isVoice){
			ss.setSpan(new StyleSpan(Typeface.ITALIC), 0, this.groupNames[groupPosition].length(), 0);
		}
        textView.setText(ss);
    	
        TextView channels = (TextView) ll.findViewById(R.id.channels);
        TextView channelsunread = (TextView) ll.findViewById(R.id.channelsunread);
        TextView privatechat = (TextView) ll.findViewById(R.id.privatechat);
        TextView privatechatunread = (TextView) ll.findViewById(R.id.privatechatunread);
        
        channels.setText(ctx.getString(R.string.ui_channels) + " " + unreadcounters[groupPosition][0]);
        channelsunread.setText("  " +  ctx.getString(R.string.ui_unread) + " "  + unreadcounters[groupPosition][1]);
        privatechat.setText(ctx.getString(R.string.ui_private) + " "  + unreadcounters[groupPosition][2]);
        privatechatunread.setText("  " +  ctx.getString(R.string.ui_unread) + " " + unreadcounters[groupPosition][3]);
        
        return ll;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

}
