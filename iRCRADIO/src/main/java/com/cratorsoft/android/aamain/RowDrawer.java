package com.cratorsoft.android.aamain;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;

import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.R;


public class RowDrawer {

	public static final int HEADER = 0; 
	public static final int DETAIL = 1;
		
	public String name;
	public int type;
	public long id;
	
	public RowDrawer(int type, long id,  String name){		
		this.type = type;
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {		
		return name;
	}

	
	public static List<RowDrawer> getListDrawer(){	    

	    Resources resources = Globo.ctx.getResources();
        List<RowDrawer> rowList = new ArrayList<RowDrawer>();

		
		//rowList.add(new RowDrawer(RowDrawer.HEADER, LTDrawer.LABELMEDIZMO, resources.getString(R.string.menu_medizmo) ));
        rowList.add(new RowDrawer(RowDrawer.DETAIL, LTDrawerMenu.CHAT, resources.getString(R.string.button_chat) ));
		rowList.add(new RowDrawer(RowDrawer.DETAIL, LTDrawerMenu.ACCOUNTS, resources.getString(R.string.button_accounts) ));
        rowList.add(new RowDrawer(RowDrawer.DETAIL, LTDrawerMenu.MESSAGING, resources.getString(R.string.button_messaging) ));
        rowList.add(new RowDrawer(RowDrawer.DETAIL, LTDrawerMenu.NOTIFICATIONS, resources.getString(R.string.button_notifications) ));
        rowList.add(new RowDrawer(RowDrawer.DETAIL, LTDrawerMenu.LISTCHANNELS, resources.getString(R.string.button_listchannels) ));
        rowList.add(new RowDrawer(RowDrawer.DETAIL, LTDrawerMenu.PREFERENCES, resources.getString(R.string.button_preferences) ));
		return rowList;
	}
	
	
	
}
