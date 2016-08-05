package com.cratorsoft.android.adapter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cratorsoft.android.db.DBQAccount;
import com.cratorsoft.android.db.DBQChannel;
import com.cratorsoft.android.dbtable.AccountData;
import com.cratorsoft.android.dbtable.ChannelData;
import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;

public class ExpandableListORMAccountAdapter extends BaseExpandableListAdapter{

	
	private List<AccountData> mAccountList;
	private Map< Long, List<ChannelData> >  mChannelMap;
	
	private LayoutInflater mInflater;
	
	public ExpandableListORMAccountAdapter(Context ctx) {
		super();
        mInflater = ((Activity)ctx).getLayoutInflater();
	}

    public void setData(AdapterBundle ab){
        mAccountList = ab.accountList;
        mChannelMap = ab.channelMap;
        this.notifyDataSetChanged();
    }



	@Override
	public int getGroupCount() {		
		return mAccountList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		
		List<ChannelData>  chanlist =  mChannelMap.get(mAccountList.get(groupPosition).id);
		if (chanlist == null){
			return 0;
		}else{
			return chanlist.size();	
		}
		
		
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mAccountList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		AccountData accountData = (AccountData) getGroup(groupPosition);
		return mChannelMap.get(accountData.id).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {		
		return mAccountList.get(groupPosition).id;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
		return mChannelMap.get(mAccountList.get(groupPosition).id).get(childPosition).id;
	}

	@Override
	public boolean hasStableIds() {		
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		View v;
		if (convertView == null){
			v = this.newGroupView(parent);
		}else{
			v = convertView;
		}
		
		AccountData accountData = (AccountData)this.getGroup(groupPosition);
		
		TextView tv = (TextView)v.findViewById(R.id.text1);
		String accountname = accountData.accountname;
		tv.setText(accountname);
		ImageView iv = (ImageView)v.findViewById(R.id.statusIcon);
		iv.setImageResource(Globo.botManager.getStatusServer(accountData.id));
		
		return v;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		View v;
		if (convertView == null){
			v = this.newChildView(parent);
		}else{
			v = convertView;
		}
		
		TextView tv = (TextView)v.findViewById(R.id.text1);
		
		ChannelData channelData = (ChannelData)getChild(groupPosition, childPosition);
		String channel = channelData.channel;
		tv.setText(channel);
		ImageView iv = (ImageView)v.findViewById(R.id.statusIcon);
		
		iv.setImageResource(Globo.botManager.getStatusChannel(channelData.accountid, channelData.channel));
		
		return v;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {		
		return true;
	}



	
	protected View newGroupView(ViewGroup parent) {
		View view  = mInflater.inflate(R.layout.row_group_accountlist, parent, false);
		return view;
	}
	
	protected View newChildView(ViewGroup parent) {
		View view  = mInflater.inflate(R.layout.row_accountlist, parent, false);		
		return view;
	}

    public static class AdapterBundle{
        public List<AccountData> accountList;
        public Map< Long, List<ChannelData> >  channelMap;


        public static AdapterBundle getAdapterBundle(){

            AdapterBundle ab = new AdapterBundle();

            ab.channelMap = new HashMap< Long, List<ChannelData> >();

            try {
                ab.accountList = DBQAccount.getAccountList();

                for (AccountData account: ab.accountList){

                    List<ChannelData> channelList = DBQChannel.getChannelList(account.id);
                    ab.channelMap.put(account.id, channelList);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ab;
        }

    }
}
