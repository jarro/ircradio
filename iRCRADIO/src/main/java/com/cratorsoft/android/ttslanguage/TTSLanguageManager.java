package com.cratorsoft.android.ttslanguage;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;

import com.earthflare.android.ircradio.Globo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by j on 15/01/15.
 */
public enum TTSLanguageManager {

    INSTANCE;


    private Map<String, TTSEngine> mapTTSEngine;
    private Map<String, TTSFallback> mapTTSFallback;
    private List<TTSLanguage> activeLanguageList;

    TTSLanguageManager(){

    }


    public void backgroundRefresh (){

        Runnable r = new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        };

        new Thread(r).start();

    }


    synchronized private void refresh(){



        mapTTSEngine = new HashMap<String, TTSEngine>();



        TextToSpeech tts = TTSFactory.INSTANCE.getTTS(false);
        List<TextToSpeech.EngineInfo> listEngine =  tts.getEngines();
        tts.shutdown();



        for (TextToSpeech.EngineInfo engineInfo : listEngine) {



            tts = TTSFactory.INSTANCE.getTTS(engineInfo.name, false);


            TTSEngine ttsEngine = new TTSEngine(engineInfo);
            mapTTSEngine.put(engineInfo.name, ttsEngine);


            Locale[] locales = Locale.getAvailableLocales();


            for (Locale locale : locales) {



                try
                {
                    int res = tts.isLanguageAvailable(locale);
                    boolean hasVariant = (null != locale.getVariant() && locale.getVariant().length() > 0);
                    boolean hasCountry = (null != locale.getISO3Country() && locale.getISO3Country().length() > 0);

                    boolean isLocaleSupported =
                            false == hasVariant && false == hasCountry && res == TextToSpeech.LANG_AVAILABLE ||
                                    false == hasVariant && true == hasCountry && res == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                                    res == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;

                    Log.d("ttsdump", "TextToSpeech Engine isLanguageAvailable " + locale  + " (supported=" + isLocaleSupported + ",res=" + res + ", country=" + locale.getCountry() + ", variant=" + locale.getVariant() + ")");

                    if (true == isLocaleSupported)
                    {
                        ttsEngine.addLanguage(locale, engineInfo);
                    }
                }
                catch (Exception ex)
                {
                    Log.e("ttsdump", "Error checking if language is available for TTS (locale=" + locale +"): " + ex.getClass().getSimpleName() + "-" + ex.getMessage());
                }


            }

            TTSPersister.INSTANCE.mergeActiveList(ttsEngine);

        }


        setLanguages();
        updateActiveLanguages();

        dump();


    }


    private void setLanguages(){

        mapTTSFallback = new HashMap<String, TTSFallback>();


        Set<String> languageSet = new HashSet<String>();


        for (TTSEngine engine : mapTTSEngine.values()){

            for (TTSLanguage language : engine.getListTTSLanguage() ){

                String langcode = language.locale.getLanguage();

                if ( !languageSet.contains(langcode)){

                    languageSet.add(langcode);
                    mapTTSFallback.put(langcode, new TTSFallback(new Locale(langcode), engine.getEngineName()));

                }


            }
        }


    }


    public synchronized void updateActiveLanguages(){

        activeLanguageList = new ArrayList<TTSLanguage>();

        for (TTSEngine engine : mapTTSEngine.values()){

            for (TTSLanguage language : engine.getListTTSLanguage() ){
                if (language.active){
                    activeLanguageList.add(language);
                }
            }
        }

    }


    public synchronized void dump(){

        String tag = "ttsdump";

        for (TTSEngine engine  : mapTTSEngine.values()){


            Log.d(tag, engine.getEngineName());
            Log.d(tag, engine.getEngineLabel());


            for (TTSLanguage ttsLanguage :  engine.getListTTSLanguage()){

                Log.d(tag, ttsLanguage.locale.toString());

            }


        }


        for (TTSFallback ttsFallback : mapTTSFallback.values()){

            Log.d(tag, ttsFallback.locale.getDisplayLanguage());

        }


    }


    public synchronized List<TTSEngine> getListTTSEngine(){
        return new ArrayList<TTSEngine>(mapTTSEngine.values());
    }


    public synchronized String getFallback(String langcode){

        TTSFallback fallback = mapTTSFallback.get(langcode);

        if (fallback == null){
            return null;
        }else{
            return fallback.enginename;
        }


    }

    public synchronized List<TTSLanguage> getActiveLanguageList(boolean includeblank){

        if (includeblank){
            List<TTSLanguage> templist = new ArrayList<TTSLanguage>();
            TTSLanguage blank = new TTSLanguage();

            templist.add(blank);
            templist.addAll(activeLanguageList);
            return templist;

        }else{
            return activeLanguageList;
        }

    }


    public synchronized boolean validateEngineLanguage(Locale locale, String enginename){


        TTSEngine engine = mapTTSEngine.get(enginename);

        boolean found = false;
        if (engine != null){

            for (TTSLanguage language : engine.getListTTSLanguage()){
                if (language.locale.equals(locale) ){
                    found = true;
                    break;
                }
            }

        }
        return found;
    }

}
