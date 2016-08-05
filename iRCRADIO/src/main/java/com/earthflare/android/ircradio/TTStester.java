package com.earthflare.android.ircradio;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

public class TTStester {

	static boolean downloading = false;
	
	public static void initdefault(Context ctx) {

        /*
		int result = Globo.myTts.isLanguageAvailable(Locale.getDefault());
		switch(result){
		case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
		    Globo.voiceDefault = Locale.getDefault();
		    Globo.voicedefaultLoaded = true;
		    TTStester.initlocale(ctx);
			break;
		case TextToSpeech.LANG_COUNTRY_AVAILABLE:
			Globo.voiceDefault = Locale.getDefault();
			Globo.voicedefaultLoaded = true;
			TTStester.initlocale(ctx);
			break;		  
		case TextToSpeech.LANG_AVAILABLE:
			Globo.voiceDefault = Locale.getDefault();
			Globo.voicedefaultLoaded = true;
			TTStester.initlocale(ctx);
		    break;
		case TextToSpeech.LANG_MISSING_DATA:
			Globo.voiceDefault = Locale.getDefault();
			Globo.voicedefaultLoaded = false;
			TTStester.initlocale(ctx);
			if (!downloading) {downloading = true; downloaddata(ctx);}
		    break;
		case TextToSpeech.LANG_NOT_SUPPORTED:		    
		    testfallbacklanguage(ctx);
			break;
		}
	    */
		
		
	}
	
	public static void initlocale(Context ctx) {

        /*
		int result = Globo.myTts.isLanguageAvailable(new Locale(Globo.voiceLanguage));
		switch(result){
		case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:	    
		    Globo.voicechannelLoaded = true;
			break;
		case TextToSpeech.LANG_COUNTRY_AVAILABLE:
			Globo.voicechannelLoaded = true;
			break;		  
		case TextToSpeech.LANG_AVAILABLE:
			Globo.voicechannelLoaded = true;			
		    break;
		case TextToSpeech.LANG_MISSING_DATA:			
			Globo.voicedefaultLoaded = false;
			if (!downloading) {downloading = true; downloaddata(ctx);}
		    break;
		case TextToSpeech.LANG_NOT_SUPPORTED:
			Globo.voicechannelLoaded = false;		    
			break;
		}
		*/
	}
	
	
	private static void downloaddata(Context ctx) {
		// missing data, install it
        Intent installIntent = new Intent();
        installIntent.setAction(
            TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(installIntent);

	}
	
	private static void testfallbacklanguage (Context ctx) {
        /*
		Globo.voiceDefault = Locale.US;
		int result = Globo.myTts.isLanguageAvailable(Globo.voiceDefault);
		if (result == TextToSpeech.LANG_MISSING_DATA) {			
			Globo.voicedefaultLoaded = false;
			if (!downloading) {downloading = true; downloaddata(ctx);}
		}else{
			Globo.voicedefaultLoaded = true;
			TTStester.initlocale(ctx);
		}
		*/
	}
	
}
