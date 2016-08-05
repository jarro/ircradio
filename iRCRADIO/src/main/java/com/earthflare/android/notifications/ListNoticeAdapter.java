package com.earthflare.android.notifications;


import java.util.LinkedList;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.earthflare.android.ircradio.R;

public class ListNoticeAdapter extends BaseAdapter{

	private LinkedList<Notice>  list; 
	private Context ctx;

	private final int maxNotice = 50;
	
	public ListNoticeAdapter(Context ctx) {
		
		this.ctx = ctx;
		
			synchronized (NoticeManager.noticeList) {
			  list = (LinkedList<Notice>)NoticeManager.noticeList.clone();
			}
		
	}
	
	 synchronized public void addToList(Notice notice){
			list.addFirst(notice);	
			
			if (list.size() > this.maxNotice) {
				list.removeLast();
			}
			this.notifyDataSetChanged();
			
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Notice getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		TextView tv = (TextView)TextView.inflate(ctx, R.layout.row_notification, null);
		String text = "";
		Notice notice = list.get(position);
		
		String accountname = notice.accountname;
		String timestamp = DateFormat.format("E kk:mm", notice.time).toString();
		
		if (notice.noticetype.equals("named")) {
			text += ctx.getString(R.string.channelmessage) + ", " + timestamp + "\n";			
			text += accountname + " " + notice.channel + " " + notice.sender; 
			
		}else if (notice.noticetype.equals("pmed")){
			text += ctx.getString(R.string.newprivatemessage) + ", " + timestamp + "\n";			
			text += accountname + " " + notice.sender;
			
		}
		
		
		
		tv.setText(text);
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
	
    public void clear() {
    	synchronized (list){
    	  synchronized (NoticeManager.noticeList) {
    		list.clear();
    	    NoticeManager.noticeList.clear();
    	  }
    	}
    	this.notifyDataSetChanged();
    }
	
}
