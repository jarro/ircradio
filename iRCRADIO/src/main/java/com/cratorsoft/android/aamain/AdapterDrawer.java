package com.cratorsoft.android.aamain;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.earthflare.android.ircradio.R;


public class AdapterDrawer extends ArrayAdapter<RowDrawer>{

	LayoutInflater mInflater;
	
	public AdapterDrawer(Context context, int textViewResourceId, List<RowDrawer> objects) {		
	    super(context, textViewResourceId, objects);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		View v = convertView;
		
		if (getItemViewType(position) == RowDrawer.HEADER){
			//if (v == null){
				v = mInflater.inflate(R.layout.row_drawer_header,null);
			//}
			return bindHeaderRow(position,v);
		}else{
			//if (v == null){
				v = mInflater.inflate(R.layout.row_drawer_detail,null);
			//}
			return bindDetailRow(position,v);
		}		
	}

	private View bindDetailRow(int position, View v){
		RowDrawer row = getItem(position);
		TextView tv = (TextView) v.findViewById(R.id.text_name);
		tv.setText(row.name);
		return v;
	}
	
	private View bindHeaderRow(int position, View v){
		RowDrawer row = getItem(position);
		TextView tv = (TextView) v.findViewById(R.id.text_name);		
		tv.setText(row.name);
		return v;
	}
	
	@Override
	public long getItemId(int position) {		
		return this.getItem(position).id;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;		
	}

	@Override
	public int getItemViewType(int position) {		
		return getItem(position).type;
	}

	@Override
	public int getViewTypeCount() {		
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position).type == RowDrawer.DETAIL;
	}

    

	
	
}
