package com.earthflare.android.ircradio;

import java.util.HashMap;

import android.media.AudioManager;
import android.speech.tts.TextToSpeech;


public class Speak {


    static void speak(String message, int queuemode, HashMap<String, String> speakid) {



        /*
		if (Globo.pref_splitstream) {
          //check if music muted
			
		  speakid.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_SYSTEM));
		  if (!Globo.musicmuted && Globo.pref_mutemusic) {
			  Globo.AM.setStreamMute(AudioManager.STREAM_MUSIC, true);
			  Globo.musicmuted = true;
		  }
		  
		  //test
		  
		 
		  
		  Globo.myTts.speak(message,queuemode,speakid);
		 */

        /*
        Globo.AM.requestAudioFocus(Globo.audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        speakid.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
        Globo.myTts.speak(message, queuemode, speakid);
        */


    }

}
