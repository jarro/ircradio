package com.cratorsoft.android.ttslanguage;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import com.earthflare.android.ircradio.Globo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by j on 28/01/15.
 */
public class TTSPipeline extends Thread {



    private static TTSPipeline instance = null;
    public synchronized static TTSPipeline getInstance(){

        if  (instance == null){
            instance = new TTSPipeline();
            instance.start();
        }
        return instance;
    }

    private Handler handler;


    private Map<String, TTSStatus> statusMap = new HashMap<String, TTSStatus>();
    private Map<String, TextToSpeech> engineMap = new HashMap<String, TextToSpeech>();

    private HashMap<String, String> speakID;


    @Override
    public void run() {

        speakID = new HashMap<String, String>();
        speakID.put("utteranceId", "speaky");

        try {
            Looper.prepare();
            handler = new Handler();
            Looper.loop();
        } catch (Throwable t) {
            // throw new RuntimeException(t);
        }
    }







    public synchronized void reset() {
        //shutdown any engines

        for (TextToSpeech tts: engineMap.values()){

            try{
                tts.shutdown();
            }catch (Exception e){}
        }

        statusMap = new HashMap<String, TTSStatus>();
        engineMap = new HashMap<String, TextToSpeech>();
    }

    public synchronized  void stopTTS(){

        for (TextToSpeech tts: engineMap.values()){

            try{
                tts.stop();
            }catch (Exception e){}
        }


    }

    public synchronized  void setPitch(float pitch){

        try {
            for (TextToSpeech tts :engineMap.values()){
                tts.setPitch(pitch);                
            }
        }catch (Exception e){}

    }


    public synchronized  void setSpeechRate(float rate){

        try {
            for (TextToSpeech tts :engineMap.values()){
                tts.setSpeechRate(rate);
            }
        }catch (Exception e){}

    }


    public synchronized  void speak (String message,  String language){


        TextToSpeech tts = getEngine(language);

        if (tts != null){
            Globo.AM.requestAudioFocus(Globo.audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            tts.speak(message,TextToSpeech.QUEUE_ADD,speakID);
        }



    }


    private TextToSpeech getEngine(String language){

        if (statusMap.containsKey(language)){

            TTSStatus status = statusMap.get(language);
            //set language return engine
            return setLanguage(status);


        }else{
            //create status

            TTSStatus status = TTSStatus.getInstance(language);
            statusMap.put(language, status);
            //initialize status engine

            if (status.status == TTSStatus.AVAILABLE) {

                if (engineMap.containsKey(status.enginename)) {
                    //set language return engine
                    return setLanguage(status);
                }else {
                    //create engine
                    TextToSpeech tts = TTSFactory.INSTANCE.getTTS(status.enginename, true);
                    engineMap.put(status.enginename, tts);

                    //set language return engine
                    return setLanguage(status);
                }


            }


        }

        return null;
    }



    private TextToSpeech setLanguage(TTSStatus status) {

        TextToSpeech tts = engineMap.get(status.enginename);
        int langchange =  tts.setLanguage(status.locale);
        return tts;

    }





}
