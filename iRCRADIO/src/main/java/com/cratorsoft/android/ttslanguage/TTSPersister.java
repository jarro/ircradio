package com.cratorsoft.android.ttslanguage;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.earthflare.android.ircradio.Globo;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by j on 27/01/15.
 */
public enum TTSPersister {

    INSTANCE;

    public void mergeActiveList(TTSEngine engine){


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Globo.ctx);


        boolean exists = prefs.contains("ttsengine:" + engine.getEngineName() );

        if (exists){


            Set<String> activelanguages =  prefs.getStringSet("ttsengine:" + engine.getEngineName(), new LinkedHashSet<String>());

            for (TTSLanguage language: engine.getListTTSLanguage() ){

                String code = language.locale.toString();


                if (activelanguages.contains(code)){
                    language.active = true;
                }else{
                    language.active = false;
                }

            }



        }else{

            //attempt to activate defaults
            boolean activated_en = false;
            boolean activated_fr = false;
            boolean activated_it = false;
            boolean activated_de = false;
            boolean activated_es = false;


            for (TTSLanguage language: engine.getListTTSLanguage() ){

               if(!activated_en){
                   if (language.locale.getLanguage().equals("en")){
                       language.active = true;
                       activated_en = true;
                   }
               }

                if(!activated_fr){
                    if (language.locale.getLanguage().equals("fr")){
                        language.active = true;
                        activated_fr = true;
                    }
                }

                if(!activated_it){
                    if (language.locale.getLanguage().equals("it")){
                        language.active = true;
                        activated_it = true;
                    }
                }

                if(!activated_de){
                    if (language.locale.getLanguage().equals("de")){
                        language.active = true;
                        activated_de = true;
                    }
                }

                if(!activated_es){
                    if (language.locale.getLanguage().equals("es")){
                        language.active = true;
                        activated_es = true;
                    }
                }

            }

            TTSPersister.INSTANCE.saveActiveList(engine);

        }




    }


    public void saveActiveList(TTSEngine engine){


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Globo.ctx);
        Set<String> activelanguages = new HashSet<String>();

        for (TTSLanguage language: engine.getListTTSLanguage() ){
            if (language.active){
                activelanguages.add(language.locale.toString());
            }
        }

        prefs.edit().putStringSet("ttsengine:" + engine.getEngineName(),activelanguages).commit();





    }


}
