package com.earthflare.android.ircradio;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAccountAdapter extends CursorAdapter{

	public ListAccountAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor cursor) {
		// TODO Auto-generated method stub
	    LinearLayout layout = (LinearLayout)arg0;
	    TextView tv = (TextView)layout.findViewById(R.id.text1);
		tv.setText(cursor.getString(7));
		ImageView iv = (ImageView)layout.findViewById(R.id.statusIcon);
		//iv.setImageResource(Globo.botManager.getStatus(cursor.getLong(0), cursor.getString(1), cursor.getString(8)));
	    
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		LinearLayout layout = (LinearLayout)LinearLayout.inflate(context, R.layout.row_accountlist, null);
		TextView tv = (TextView)layout.findViewById(R.id.text1);
		tv.setText(cursor.getString(7));
		
		ImageView iv = (ImageView)layout.findViewById(R.id.statusIcon);
		//iv.setImageResource(Globo.botManager.getStatus(cursor.getLong(0), cursor.getString(1), cursor.getString(8)));
		
		return layout;
	}

}
