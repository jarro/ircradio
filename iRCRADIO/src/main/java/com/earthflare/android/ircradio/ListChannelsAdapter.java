package com.earthflare.android.ircradio;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.earthflare.android.logmanager.LogManager;
import com.earthflare.android.logmanager.ServerLog;

public class ListChannelsAdapter extends BaseAdapter{

	private ServerLog serverLog; 
	private Context ctx;
	private LinkedList<Channel> channels;		
	
		
	private long network;
	
	LayoutInflater mInflater;
	
	public ListChannelsAdapter(Context ctx) {
		
	this.ctx = ctx;		   
	channels = new LinkedList<Channel>();
	
	mInflater = ((Activity)ctx).getLayoutInflater();
	
	}
	
	public void setList(long network) {
		
		this.network = network;		
		synchronized (LogManager.serverLogMap.get(network).channelsProcessed) {
		synchronized (this.channels) {	
		  this.channels = LogManager.serverLogMap.get(network).channelsProcessed;
		}}
		this.notifyDataSetChanged();
		
	}
	
	public void clean() {
		
		synchronized(channels){
			channels.clear();
			this.notifyDataSetChanged();
		}
		
	}
		
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return channels.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return channels.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		LinearLayout ll;
		if (convertView == null) {
			ll = (LinearLayout)mInflater.inflate(R.layout.row_listchannels, parent,false);
		}else{
			ll = (LinearLayout)convertView; 
		}
		
		TextView tvchannel = (TextView)ll.findViewById(R.id.textchannel);
		tvchannel.setText(channels.get(position).name);
		TextView users = (TextView)ll.findViewById(R.id.textusers);
		users.setText(channels.get(position).users + "" );
		return ll;
	}

	public boolean areAllItemsEnabled()
	{
	return true;
	}

	public boolean isEnabled(int position)
	{
	return true;
	}
	
	
	public Channel getChannel(int location) {
		synchronized(channels){
		  return channels.get(location);
		}
	}
	
	public boolean needupdate() {
		
		
		try {
			if (LogManager.serverLogMap.get(network).newchannels) {
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
}
