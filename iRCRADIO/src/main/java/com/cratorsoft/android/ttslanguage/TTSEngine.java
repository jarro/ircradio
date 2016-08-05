package com.cratorsoft.android.ttslanguage;

import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by j on 16/01/15.
 */
public class TTSEngine {



    private TextToSpeech.EngineInfo  ttsInfo;

    private List<TTSLanguage> listTTSLanguage;


    public TTSEngine(TextToSpeech.EngineInfo info){
        ttsInfo = info;
        listTTSLanguage = new ArrayList<TTSLanguage>();
    }

    /**
     * blocks adding language with no country if country version available.
     * purges languages with no country if adding language with country version.
     *
     * @param locale
     */
    public void addLanguage(Locale locale, TextToSpeech.EngineInfo engineInfo){



        boolean needleHasCountry = (null != locale.getCountry() && locale.getCountry().length() > 0);


        if (needleHasCountry){
            //remove generic language then add

            TTSLanguage ttsLanguageForRemoval = null;
            for (TTSLanguage ttsLanguage : listTTSLanguage){



                if (ttsLanguage.locale.getLanguage().equals(locale.getLanguage()) && !(null != ttsLanguage.locale.getCountry() && ttsLanguage.locale.getCountry().length() > 0) ){

                    ttsLanguageForRemoval = ttsLanguage;
                    break;
                }
            }

            listTTSLanguage.remove(ttsLanguageForRemoval);
            listTTSLanguage.add(new TTSLanguage(locale, engineInfo));


        }else{
            //add only if language not already in list

            boolean found = false;

            for (TTSLanguage ttsLanguage : listTTSLanguage){

                if (ttsLanguage.locale.getLanguage().equals(locale.getLanguage())){
                    found = true;
                    break;
                }

            }

            if (!found){
                listTTSLanguage.add(new TTSLanguage(locale, engineInfo));
            }

        }




    }


    public String getEngineName(){
        return ttsInfo.name;
    }


    public String getEngineLabel(){
        return ttsInfo.label;
    }

    public List<TTSLanguage> getListTTSLanguage(){
        return listTTSLanguage;
    }


    @Override
    public String toString() {
        return ttsInfo.label;
    }


    public synchronized List<CharSequence> getLanguageLabels(){

        List<CharSequence> languagelabels = new ArrayList<CharSequence>();

        for (TTSLanguage language : listTTSLanguage){

            boolean hascountry =  (null != language.locale.getCountry() && language.locale.getCountry().length() > 0);

            languagelabels.add(language.locale.getDisplayLanguage() + (hascountry ? " - " + language.locale.getDisplayCountry():"") );

        }
        return languagelabels;
    }


    public synchronized  Integer[] getActiveIntArray(){


        List<Integer> activelist = new ArrayList<Integer>();


        for (int i=0 ; i< listTTSLanguage.size(); i++){

            TTSLanguage lang = listTTSLanguage.get(i);

            if (lang.active){
                activelist.add(i);
            }

        }

        Integer[] activearray = activelist.toArray(new Integer[activelist.size()]);

        return activearray;

    }




}
