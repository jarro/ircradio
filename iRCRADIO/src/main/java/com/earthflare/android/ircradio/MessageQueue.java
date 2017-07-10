package com.earthflare.android.ircradio;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;

import com.cratorsoft.android.language.IRCText;
import com.cratorsoft.android.language.LangMan;
import com.cratorsoft.android.language.LangTable;
import com.cratorsoft.android.language.ParsedLanguageAndEngine;
import com.cratorsoft.android.language.SCLM;
import com.cratorsoft.android.ttslanguage.TTSPipeline;

import static xdroid.toaster.Toaster.toast;
import static xdroid.toaster.Toaster.toastLong;

public enum MessageQueue {

    INSTANCE;

    private LinkedList<MessageBundle> messages;

    private static final int MAXQUEUE = 50;
    private HashMap<String, String> speakID;


    public AtomicBoolean idle = new AtomicBoolean();
    public AtomicBoolean kicked = new AtomicBoolean();

    private Object queueLock = new Object();

    private SpeachThread mspeachThread;


    private MessageQueue() {


        speakID = new HashMap<String, String>();
        speakID.put("utteranceId", "ircradiosilence");


        messages = new LinkedList<MessageBundle>();
        idle.set(true);
        kicked.set(false);
        mspeachThread = new SpeachThread();
        kick();
    }

    public MessageBundle pop() {

        synchronized (queueLock) {
            MessageBundle dequeueObj = (MessageBundle) messages.removeFirst();
            // return the object that we remove from the queue
            return dequeueObj;

        }
    }

    public void add(MessageBundle mb) {


        synchronized (queueLock) {

            if (messages.size() > MAXQUEUE) {
                flush();
            }
            messages.addLast(mb);


            if (!kicked.get()) {
                return;
            }

            if (idle.compareAndSet(true, false)) {

                messanger();

            }

        }
    }

    public void addfront(MessageBundle mb) {


        synchronized (queueLock) {

            if (messages.size() > MAXQUEUE) {
                flush();
            }
            messages.addFirst(mb);

            if (!kicked.get()) {
                return;
            }

            if (idle.compareAndSet(true, false)) {

                messanger();
            }

        }

    }


    public void flush() {

        synchronized (queueLock) {

            messages = new LinkedList<MessageBundle>();
            idle.set(true);

        }
    }


    public boolean hasMessage() {

        synchronized (queueLock) {
            return (!messages.isEmpty());
        }

    }


    synchronized public void messanger() {
        if (!this.mspeachThread.isAlive()) {
            this.mspeachThread = new SpeachThread();
            this.mspeachThread.start();
            this.mspeachThread.setPriority(Thread.MIN_PRIORITY);
        }

    }

    //add silent message to start cycling
    public void kick() {

        if (idle.compareAndSet(true, false)) {
            kicked.set(true);
            messanger();

        }
    }


    public boolean standardMessage(MessageBundle mb) {
        boolean playedTTS = false;
        //abort while phone in use
        if (Globo.phonebusystate) {
            return playedTTS;
        }
        LangTable lt = LangMan.INSTANCE.getLangTable(mb.plae.locale);
        // &&  (LangMan.INSTANCE.getLangTable(mb.plae.locale)).blockMessageByUser.contains(mb.sender)

        LangTable langTable = LangMan.INSTANCE.getLangTable(mb.plae.locale);

        if (!testMute() && (langTable == null ? true: !(LangMan.INSTANCE.getLangTable(mb.plae.locale)).blockMessageByUser.contains(mb.sender)))  {

            String newmessage;
            if (Globo.pref_replacechat) {
                newmessage = LangMan.INSTANCE.speechReplace(mb.message, mb.plae.locale);
            } else {
                newmessage = mb.message;
            }

            if (newmessage.trim().length() > 0) {

                if (Globo.authorSpeech) {


              /*
              Globo.myTts.setLanguage(messagelocale);
	    	  Globo.myTts.playSilence(200, 0, null);
	    	  Speak.speak(sender + " " + Globo.voiceResource.getLRString("r_irc_says", messagelanguage)   + " " + newmessage, 1, this.speakID);
	    	  */
                    String parsedmessage = mb.sender + " " + LangMan.INSTANCE.speechLookup(IRCText.says, mb.plae.locale) + " " + newmessage;
                    TTSPipeline.getInstance().speak(parsedmessage, mb.plae.languageAndEngine);
                    playedTTS = true;
                } else {


              /*
	    	  Globo.myTts.setLanguage(messagelocale);
	    	  Globo.myTts.playSilence(200, 0, null);
	    	  Speak.speak(newmessage, 1, this.speakID);
	    	  */

                    TTSPipeline.getInstance().speak(newmessage, mb.plae.languageAndEngine);
                    playedTTS = true;
                }

            }

        }


        appWideToast(mb.sender + ": " + mb.message);

        return playedTTS;


    }

    public boolean privateMessage(MessageBundle mb) {
        boolean playedTTS = false;
        //abort while phone in use
        if (Globo.phonebusystate) {
            return playedTTS;
        }

        if (!testMute()) {

            String newmessage;
            if (Globo.pref_replacechat) {
                newmessage = LangMan.INSTANCE.speechReplace(mb.message, mb.plae.locale);
            } else {
                newmessage = mb.message;
            }


            if (Globo.authorSpeech) {
              /*
	    	  Globo.myTts.setLanguage(messagelocale);
	    	  Globo.myTts.playSilence(200, 0, null);	    	  
	    	  Speak.speak(sender + " " + Globo.voiceResource.getLRString("r_irc_says", messagelanguage)  + " " + newmessage, 1, this.speakID);
	    	  */
                String parsedmessage = mb.sender + " " + LangMan.INSTANCE.speechLookup(IRCText.says, mb.plae.locale) + " " + newmessage;
                TTSPipeline.getInstance().speak(parsedmessage, mb.plae.languageAndEngine);
                playedTTS = true;
            } else {

              /*
	    	  Globo.myTts.setLanguage(messagelocale);
	    	  Globo.myTts.playSilence(200, 0, null);
	    	  Speak.speak(newmessage, 1, this.speakID);
	    	  */
                TTSPipeline.getInstance().speak(newmessage, mb.plae.languageAndEngine);
                playedTTS = true;
            }
        }

        appWideToast("(private)" + mb.sender + ": " + mb.message);

        return playedTTS;

    }


    public boolean actionMessage(MessageBundle mb) {

        boolean playedTTS = false;
        //abort while phone in use
        if (Globo.phonebusystate) {
            return playedTTS;
        }


        if (!testMute()) {

            /*
	    	Globo.myTts.setLanguage(messagelocale);
	    	Globo.myTts.playSilence(200, 0, null);
	    	Speak.speak(message, 1, this.speakID);
	        */
            TTSPipeline.getInstance().speak(mb.message, mb.plae.languageAndEngine);
            playedTTS = true;
        }


        appWideToast(mb.message);

        return playedTTS;


    }

    private void setLocale(String language) {
		
		/*
		
		if (language == null) {language = "default";}
		
		if (this.messagelanguage.equals(language)) {return;}
			
		
		this.messagelanguage = new String(language);
		
		if (language.equals(Globo.DEFAULT)) {
		  this.messagelocale = Globo.voiceDefault;
		    if (Globo.voicedefaultLoaded){
		    	this.voiceClear = true;}
		    else{
		    	this.voiceClear = false;
		    }
		}else{
		  
			
			//parse language country       	
			  if (language.length() > 2){
				  this.messagelocale = new Locale(language.substring(0,2),language.substring(3,5));				  			 
			  }else{
				  this.messagelocale = new Locale(language);
			  }

		  if (Globo.voicechannelLoaded){this.voiceClear = true;}else{
		    	this.voiceClear = false;
		    }
		}

		*/

    }

    private boolean testMute() {

        if (Globo.pref_headphonemute) {

            if (Globo.headphoneconnected && !Globo.mutestate) {
                return false;
            } else {
                return true;
            }
        } else {
            return Globo.mutestate;
        }


    }


    private void appWideToast(String message) {


        if (Globo.toaststate) {

            if (Globo.actAccountListVisible
                    || Globo.actListChannelsVisible
                    || Globo.actChannelListVisible
                    || Globo.actChatlogVisisble
                    || Globo.actNoticeVisible
                    || Globo.globalToast) {

                toastLong(message);

            }

        }


    }


    private class SpeachThread extends Thread implements Runnable {

        public void run() {

            boolean skippedTTS = false;

            do {

                if (MessageQueue.this.hasMessage()) {

                    MessageBundle mb = MessageQueue.this.pop();


                    if (mb.type == "standard") {

                        skippedTTS = standardMessage(mb);

                    } else if (mb.type == "action") {

                        skippedTTS = actionMessage(mb);

                    } else if (mb.type.equals("private")) {

                        skippedTTS = privateMessage(mb);
                    }

                } else {
                    //queue empty
                    idle.set(true);
                    GloboUtil.abandonAudioFocus();

                }

            }while (skippedTTS = true && !idle.get());



        }


    }


}
