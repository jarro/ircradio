package com.earthflare.android.ircradio;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TemplateChat extends ActMessanger{

	// ACTION BAR
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;

	protected void initDrawer() {

		this.mDrawerLayout = (DrawerLayout) this
				.findViewById(R.id.drawer_layout);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
        
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.string.drawer_open, R.string.drawer_close); 		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {		
		super.onPostCreate(savedInstanceState);
		
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerToggle.syncState();
	}	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
        menu.clear();
		
		MenuInflater inflater = getMenuInflater();
		
		if( Globo.chatlogActType.equals("channel")) {
			
			if(Globo.chatlogActChannelName.startsWith("#")) {
				inflater.inflate(R.menu.chatmenu, menu);
			}else{
			    inflater.inflate(R.menu.privatechatmenu, menu);
			}
			
		}else{
			inflater.inflate(R.menu.servermenu, menu);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}



	public boolean onOptionsItemSelected (MenuItem item){
        
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
		switch (item.getItemId()){
		
		
		//channel		
		case R.id.userlist:
			((ActChatLog)this).selectUser();
			return true;
		case R.id.channeltopic:
			DialogManagerChat.showTopic(this);
			return true;
		
			
        
				
        //both
		case R.id.menuoptions:
			startActivity(new Intent(this, EditChannelPreferences.class));
		    return true;
		
		case R.id.menucommands:			
			this.openContextMenu( ((ActChatLog)this).listview);
			return true;
		}

		return false;

	}
	
    public void launchWeb(String url) {
		
		if (haveNet()) {
			Intent intent = new Intent(this, ActWebView.class);
			intent.putExtra("url", url);
			startActivity(intent);	
		}else{
			DialogInfo di = new DialogInfo(this, getString(R.string.error_title_networkerror),  getString(R.string.error_message_internetrequiredtodisplayinfopage) +"\n");
            di.show();
		}
		
		
		
	}
	
    public  boolean haveNet() {
		
		ConnectivityManager connec =  (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);		    
		if (connec.getActiveNetworkInfo() == null) return false;						
		if (connec.getActiveNetworkInfo().isConnectedOrConnecting() ) {
			return true;
		}else {
			return false;
		}			
	}

	
	

	
}
