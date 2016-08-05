package com.earthflare.android.ircradio;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAccountAdapter extends CursorTreeAdapter {

	private SQLiteDatabase db;	
	private Context ctx;
	private LayoutInflater mInflater;
	
	public ExpandableListAccountAdapter(Cursor cursor, Context context, SQLiteDatabase db) {
		super(cursor, context);

	    this.db = db;	
        this.ctx = context;        		
		mInflater = ((Activity)context).getLayoutInflater();
	}

	@Override
	protected void bindChildView(View arg0, Context arg1, Cursor arg2,
			boolean arg3) {
		
		TextView tv = (TextView)arg0.findViewById(R.id.text1);
		String channel = arg2.getString(2);
		tv.setText(channel);
		ImageView iv = (ImageView)arg0.findViewById(R.id.statusIcon);
		iv.setImageResource(Globo.botManager.getStatusChannel(arg2.getLong(1), arg2.getString(2)));
		
	}

	@Override
	protected void bindGroupView(View view, Context ctx, Cursor cursor,
			boolean isExpanded) {
		
		TextView tv = (TextView)view.findViewById(R.id.text1);
		String accountname = cursor.getString(7);
		tv.setText(accountname);
		ImageView iv = (ImageView)view.findViewById(R.id.statusIcon);
		iv.setImageResource(Globo.botManager.getStatusServer(cursor.getLong(0)));
		
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		// TODO Auto-generated method stub
		
		Cursor childcursor;
		String accountid = groupCursor.getString(0);
		String sql = "select * from channel where accountid = " + accountid +  " order by channel";
	    childcursor = db.rawQuery(sql, null);
	    //((Activity)(ctx)).startManagingCursor(childcursor);
	    return childcursor;
		
	}

	@Override
	protected View newChildView(Context ctx, Cursor cursor,
			boolean isLastChild, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view  = mInflater.inflate(R.layout.row_accountlist, parent, false);		
		return view;
	}

	@Override
	protected View newGroupView(Context ctx, Cursor cursor,
			boolean isExpanded, ViewGroup parent) {
		
		
		 		
		View view  = mInflater.inflate(R.layout.row_group_accountlist, parent, false);
		return view;
	}

   

}
