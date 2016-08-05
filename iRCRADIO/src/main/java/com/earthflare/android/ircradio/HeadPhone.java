package com.earthflare.android.ircradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadPhone extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// 0 for unplugged 1 for plugged
		int plugged = intent.getIntExtra("state", 0);
		
		switch (plugged) {
		
		case 0:
			//if disconnected stop tts update state
			Globo.headphoneconnected = false;
			if (Globo.pref_headphonemute) {
				  try{

					  Globo.purgeTTS();

				  }catch(Exception e){}
			}
			break;
		case 1:
			//if connected update state 
			Globo.headphoneconnected = true;
			break;		
		}
		
	}

}
