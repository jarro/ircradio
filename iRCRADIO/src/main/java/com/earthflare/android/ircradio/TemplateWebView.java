package com.earthflare.android.ircradio;



import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;

public class TemplateWebView extends ActionBarActivity{

	    
		
	
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
