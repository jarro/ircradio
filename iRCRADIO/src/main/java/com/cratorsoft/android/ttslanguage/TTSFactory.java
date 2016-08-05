package com.cratorsoft.android.ttslanguage;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.earthflare.android.ircradio.Globo;
import com.earthflare.android.ircradio.MessageQueue;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by j on 26/01/15.
 */
public enum TTSFactory {

    INSTANCE;

    private TTSFactory(){};

    public TextToSpeech getTTS(String engineName, boolean enablekicklistener){


        final Semaphore semaphore = new Semaphore(0);

        TextToSpeech tts = new TextToSpeech(Globo.ctx,new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
               semaphore.release();
            }
        },engineName);


        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (enablekicklistener) {
            tts.setOnUtteranceCompletedListener(Globo.ttsCompleteListener);
        }

        try{
            tts.setPitch(Globo.pref_pitch);
            tts.setSpeechRate(Globo.pref_speed);
        }catch (Exception e){}

        return tts;


    }



    public TextToSpeech getTTS(boolean enablekicklistener){

        final Semaphore semaphore = new Semaphore(0);

        TextToSpeech tts = new TextToSpeech(Globo.ctx,new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                semaphore.release();
            }
        });


        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (enablekicklistener) {
            tts.setOnUtteranceCompletedListener(Globo.ttsCompleteListener);
        }

        try{
            tts.setPitch(Globo.pref_pitch);
            tts.setSpeechRate(Globo.pref_speed);
        }catch (Exception e){}

        return tts;

    }



}
