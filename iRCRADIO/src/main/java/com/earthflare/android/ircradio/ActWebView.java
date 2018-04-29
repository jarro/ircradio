package com.earthflare.android.ircradio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLDecoder;
import java.util.regex.PatternSyntaxException;

public class ActWebView extends TemplateWebView  {

    private String forwardurl;
	private WebView wv;
	private WebView wvp;
    private String loadingurl = "file:///android_asset/loading/demo.html";
	private Boolean first;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	  	
    	super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    	
    	// Request for the progress bar to be shown in the title    	
    	//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.webview);
        // Make sure the progress bar is visible
    	//this.setProgressBarIndeterminateVisibility(true);
        setSupportProgressBarIndeterminateVisibility(true);
        first = true;
         
        wv = (WebView) findViewById(R.id.webview);
        
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        wv.setBackgroundColor(Color.parseColor("#333333"));
        //wv.setWebViewClient(new WebViewActClient());
    	//wv.loadUrl(this.getString(R.string.app_url) );
        
    	Intent intent = this.getIntent();    	
    	forwardurl = intent.getStringExtra("url");
    	
    	wv.loadUrl(loadingurl); 
    	
    	//perform preload
    	wvp =  (WebView) findViewById(R.id.prewebview);
    	wvp.setWebViewClient(new WebViewActClient());
    	wvp.setBackgroundColor(Color.parseColor("#333333"));  
    	WebSettings wvpwebSettings = wvp.getSettings();
        webSettings.setJavaScriptEnabled(true);
    	wvp.loadUrl(forwardurl);
    	
    	
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }
       
    
    private class WebViewActClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        
	    	//if irc url load connect to it :)
	    	if (url.startsWith("irc://") ) {	    		
               connectIrc(url);   			
	    	}	    		    	
	    	view.loadUrl(url);
	        return true;
	    }

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
		
			if (first) {
				
				first = false;
				wvp.setVisibility(View.VISIBLE);
				wv.setVisibility(View.GONE);
                setSupportProgressBarIndeterminateVisibility(false);
				//ActWebView.this.setProgressBarIndeterminateVisibility(false);
				
				
			}			
		}
	}
    
    
    
    private void connectIrc(String url) {
    	
            
    	
    	
           
            Context ctx = this.getApplicationContext();
            
    		String[] str = {"irc","","server:port","channel?pass"};
    		String[] serverpart = {"server.server.server","80"};
    		String[] channelpart = {"#channel", "pass"};
    		
    		
    		String channelpass = "";
    		String port = "6667";
    		String accountname = "";
    		long eventid = -1;
    		
    		try{
    		  str = url.split("/");
    		  
    		}catch(PatternSyntaxException e){
    	      //move along nothing to see here
    		}
    		
    		try{
    		  serverpart = str[2].split(":");
    		}catch(PatternSyntaxException e){
    		  serverpart[0] = str[2] ;
    		  serverpart[1] = "80";
    		}
    		
    		try{
    		  channelpart = str[3].split("\\?");
    		  
    		}catch(PatternSyntaxException e){
    		  channelpart[0] = str[3];
    		  channelpart[1] = "";
    		  
    		}
    		
    	    String server = serverpart[0];
    	    
    	    if (serverpart.length > 1) {
    	      port = serverpart[1];
    	    }else{
    	      //move along nothing to see here
    	    }
    	     
    		String channel = channelpart[0];
    		
    		if (channelpart.length > 1) {
    		  channelpass = channelpart[1];
    		}else{
    		  channelpass = ""; 	
    		}
            
            
    		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    		String nick = preferences.getString("pref_nick", "droid");
            
            String serverpass = "";
            String auth = "";
                  
            
            eventid = Long.parseLong(str[4]);
            accountname = URLDecoder.decode(str[5]);
            String joinleave = "0";
            
            Intent radioServiceIntent = new Intent(this, IrcRadioService.class);
            radioServiceIntent.putExtra("server",server);
            radioServiceIntent.putExtra("nick",nick);
            radioServiceIntent.putExtra("channel",channel);
            radioServiceIntent.putExtra("accountname",accountname);
            radioServiceIntent.putExtra("serverpass",serverpass);
            radioServiceIntent.putExtra("port",port);
            radioServiceIntent.putExtra("auth",auth);
            radioServiceIntent.putExtra("channelpass",channelpass);
            radioServiceIntent.putExtra("joinleave",joinleave);
            radioServiceIntent.putExtra("accountid", eventid);
			radioServiceIntent.putExtra("ttsprefixes", "");
            
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("action", "connecting");
            data.putString("accountname", accountname);            
            msg.setData(data);
            Globo.handlerAccountList.sendMessage(msg);
                                   
            radioServiceIntent.putExtra("action","connect");
            
            
            
            Globo.botManager.addChannel(ctx, radioServiceIntent);
            //ctx.startService(radioServiceIntent);
            
            this.finish();
            
            
    	
    	
    }



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		WebView a = (WebView)this.findViewById(R.id.prewebview);
		WebView b = (WebView)this.findViewById(R.id.webview);
		
		a.destroy();
		b.destroy();
		
		super.onDestroy();
		
		
		
	}



	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}  

    
    
    
}

