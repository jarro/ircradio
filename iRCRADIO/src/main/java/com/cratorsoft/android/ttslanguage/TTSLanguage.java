package com.cratorsoft.android.ttslanguage;

import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by j on 16/01/15.
 */
public class TTSLanguage {


    public Locale locale;
    public boolean active = false;
    public TextToSpeech.EngineInfo engineInfo;

    public  String labelwithEngine;
    public  String nameWithEngine;

    public boolean blankPlaceholer = false;

    public TTSLanguage(Locale locale, TextToSpeech.EngineInfo engineInfo){
        this.locale = locale;
        this.engineInfo = engineInfo;

        initLabelWithEngine();
        initNameWithEngine();


    }

    public TTSLanguage(){
        blankPlaceholer = true;
    }


    private void initLabelWithEngine(){

        boolean hascountry =  (null != locale.getCountry() && locale.getCountry().length() > 0);
        boolean hasvariant =  (null != locale.getVariant() && locale.getVariant().length() > 0);

        labelwithEngine =  locale.getDisplayLanguage()
                           +  (hascountry? " - " + locale.getDisplayCountry() : "")
                           +  (hasvariant? " - " + locale.getDisplayVariant() : "")
                           + " (" + engineInfo.label + ")";


    }

    private void initNameWithEngine(){
        nameWithEngine = locale.toString() + ":" + engineInfo.name;
    }


    public String getLanguageForDB(){
        if (blankPlaceholer){
            return "en";
        }else{
            return nameWithEngine;
        }
    }

    @Override
    public String toString() {
        if (blankPlaceholer){
            return "Default";
        }else{
            return labelwithEngine;
        }
    }
}
