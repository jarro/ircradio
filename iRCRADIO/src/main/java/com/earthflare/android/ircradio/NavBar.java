package com.earthflare.android.ircradio;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.earthflare.android.logmanager.ChannelLog;
import com.earthflare.android.logmanager.LogManager;
import com.earthflare.android.logmanager.ServerLog;

public class NavBar {

	
	
	public static void initialize (Fragment fragment, Context ctx, LinearLayout ll, View.OnClickListener listener)  {
		
		ll.removeAllViews();
		
			
			for (ServerLog sl : LogManager.serverLogMap.values() ) {
				
				if (!(Globo.chatlogActType.equals("server") && sl.network == Globo.chatlogActNetworkName)) {
					
				Button b = (Button)Button.inflate(ctx, R.layout.nav_button, null);
				fragment.registerForContextMenu(b);
				
				b.setTag(new NavItem(sl.network,sl.accountName));
				SpannableString ss = new SpannableString(sl.accountName);
				ss.setSpan(new UnderlineSpan(), 0, sl.accountName.length(), 0);

				int textcolor = Globo.botManager.getStatusColorServer(sl.network, sl.checked);
				ss.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(textcolor)), 0, sl.accountName.length(), 0);
				
				
				boolean isVoice = (Globo.voiceServer == sl.network && Globo.voiceType.equals("server"));
				if (isVoice){
					ss.setSpan(new StyleSpan(Typeface.ITALIC), 0, sl.accountName.length(), 0);
				}
				
				b.setText(ss);
				b.setOnClickListener(listener);
				ll.addView(b);
				
				}
				
				
				for (ChannelLog cl : sl.channelLogMap.values() ) {
					
		
					if (!(Globo.chatlogActType.equals("channel") && sl.network == Globo.chatlogActNetworkName && cl.channel.equals(Globo.chatlogActChannelName))) {
		
					Button c = (Button)Button.inflate(ctx, R.layout.nav_button, null);
					fragment.registerForContextMenu(c);
					c.setTag(new NavItem(sl.network,sl.accountName,cl.channel));
					SpannableString ss = new SpannableString(cl.channel);
					
					c.setOnClickListener(listener);
					
					int textcolor;
					textcolor = Globo.botManager.getStatusColorChannel(sl.network, cl.channel, cl.checked);
					
					c.setTextColor(ctx.getResources().getColor(textcolor));
					
					boolean isVoice = (Globo.voiceServer == sl.network && Globo.voiceChannel.equals(cl.channel) && Globo.voiceType.equals("channel") );
					if (isVoice) {
						ss.setSpan(new StyleSpan(Typeface.ITALIC), 0, cl.channel.length(), 0);
					}
					c.setText(ss);
					ll.addView(c);
					
					}
					
				}			
		  }
	}
	
	
	
}
